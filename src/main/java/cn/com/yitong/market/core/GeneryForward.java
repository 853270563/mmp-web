package cn.com.yitong.market.core;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;

import cn.com.yitong.common.utils.ConfigUtils;
import cn.com.yitong.common.utils.StringUtils;
import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.core.bean.MBTransConfBean;
import cn.com.yitong.framework.core.vo.MBTransItem;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.util.YTLog;

/**
 * 返回页面；
 *
 * @author yaoym
 */
@Controller
public class GeneryForward extends BaseControl {
    private Logger logger = YTLog.getLogger(this.getClass());
    @Autowired
    @Qualifier("EBankConfParser4temp")
    IEBankConfParser confParser;// 报文装载器

    @Autowired
    @Qualifier("requestBuilder4db")
    IRequstBuilder requestBuilder;// 请求报文生成器

    @Autowired
    @Qualifier("EBankConfParser4db")
    IEBankConfParser confParserdb;// 报文装载器


    @RequestMapping("/test/trans.do")
	public String trans(HttpServletRequest request, Model model) {
		model.addAttribute("WEBSOCKET_PORT", ConfigUtils.getValue("WEBSOCKET_PORT"));
        return "trans";
    }

    @RequestMapping("/index.do")
    public String index(HttpServletRequest request) {
        return "index";
    }

    @RequestMapping("/ares.do")
    public String ares(HttpServletRequest request) {
        return "ares";
    }

    @RequestMapping("/portal.do")
    public String portal(HttpServletRequest request) {
        return "portal";
    }

    @RequestMapping("/ydyx.do")
    public String ydyx(HttpServletRequest request) {
        return "ydyx";
    }

    /**
     * 显示请求报文结构
     *
     * @param request
     * @param filePath
     * @return
     */
    @RequestMapping("/test/form/req.do")
    public String formReq(HttpServletRequest request, String filePath) {
        Map rst = new HashMap();
        MBTransConfBean conf = confParser.findTransConfById(filePath);
        List<MBTransItem> items = conf.getSed();
        
        List<MBTransItem> resItems = new ArrayList<MBTransItem>();
        for(MBTransItem item:items){
        	String xpath = item.getXmlPath();
			if (xpath.indexOf("Service_Header") > -1) {
				continue;
			} else if (xpath.indexOf("Service_Body") > -1) {
				List<MBTransItem> bodyItems = item.getChildren();
				for(MBTransItem byItem:bodyItems){
					resItems.add(byItem);
				}
			}
        }
        
        if(!resItems.isEmpty()){
        	request.setAttribute("items", resItems);
        }else{
			request.setAttribute("items", items);
        }
        
        request.setAttribute("conf", conf.getProperties());

        return "form/req";
    }

    /**
     * 显示响应报文结构
     *
     * @param request
     * @param filePath
     * @return
     */
    @RequestMapping("/test/form/rsp.do")
    public String formRsp(HttpServletRequest request, String filePath) {
		Map rst = new HashMap();
		MBTransConfBean conf = confParser.findTransConfById(filePath);
		List items = conf.getRcv();
		request.setAttribute("items", items);
		request.setAttribute("conf", conf.getProperties());

		return "form/rsp";
    }



    @RequestMapping("/test/qdzhSave.do")
    @ResponseBody
    public Map<String, Object> qdzhSave(HttpServletRequest request, String transCode, String fileName) {
        transCode = transCode.replaceAll(".do", "");
        String rootpath = AppConstants.TEST_FILE_PATH;
        Map<String, Object> rst = new HashMap<String, Object>();
        IBusinessContext ctx = new BusinessContext(request,
                IBusinessContext.PARAM_TYPE_MAP);
        // 检查报文定义
        if (!CtxUtil.transPrev(ctx, transCode, json2MapParamCover,
                requestBuilder, confParserdb, rst)) {
            return rst;
        }
        if (StringUtils.isEmpty(fileName)) {
            fileName = transCode;
        }

        // 初始化数据总线
        ctx.getParamMap().remove("transCode");
        ctx.getParamMap().remove("fileName");
        String json = JSON.toJSONString(ctx.getParamMap());

        String filePath = String.format("%s/%s_%s.json", rootpath, transCode.replaceAll("/", "_"), fileName);

        if (!new File(rootpath).exists()) {
            logger.warn("目录不存在{}");
            rst.put("STATUS", "0");
            rst.put("MSG", "目录不存在!");
            return rst;
        }
        try {
            FileWriter fw = new FileWriter(filePath);
            fw.write(json);
            fw.close();
            rst.put("STATUS", "1");
        } catch (IOException e) {
            logger.error("test qdzhSave error", e);
            rst.put("STATUS", "0");
            rst.put("MSG", "文件保存失败!");
        }
        return rst;
    }


    @RequestMapping("/test/qdzhFind.do")
    @ResponseBody
    public Map<String, Object> qdzhFind(String transCode, HttpServletRequest request) {
        transCode = transCode.replaceAll("/", "_").replaceAll(".do", "");
        Map<String, Object> rst = new HashMap<String, Object>();
        String rootpath = AppConstants.TEST_FILE_PATH;
        // 初始化数据总线
        final String filePre = transCode + "_";

        File dir = new File(rootpath);
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File f, String fileName) {
                if (fileName.startsWith(filePre) && !fileName.contains("svn")) {
                    return true;
                }
                return false;
            }
        });

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        if (null != files && files.length > 0) {
            for (File file : files) {
                Map<String, Object> map = new HashMap<String, Object>();
                String fileName = file.getName();
                map.put("NAME", fileName.replace(filePre, "").replace(".json", ""));
                map.put("URL", fileName);
                try {
                    map.put("CONTENT", FileUtils.readFileToString(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                list.add(map);
            }
        }

        rst.put("LIST", list);
        // 加载参数
        return rst;
    }



}
