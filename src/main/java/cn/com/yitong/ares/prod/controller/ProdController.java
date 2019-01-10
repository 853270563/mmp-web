package cn.com.yitong.ares.prod.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.market.jjk.service.CustomFileMngService;
import cn.com.yitong.util.CustomFileType;
import cn.com.yitong.util.YTLog;

/**
 * @author huzy@yitong.com.cn
 */
@Controller
@SuppressWarnings({"unused", "unchecked"})
public class ProdController extends BaseControl {
	public static final String IMG_PARENT_URL = "/download/userResource/img.do?fileName=";
	public static final String FILE_PARENT_URL = "/download/userResource/file.do?fileName=";

	@Resource
	private CustomFileMngService customFileMngService;
    private Logger logger = YTLog.getLogger(this.getClass());
    @Autowired
    ICrudService service;
    @Autowired
    @Qualifier("requestBuilder4db")
    IRequstBuilder requestBuilder;// 请求报文生成器
    @Autowired
    @Qualifier("responseParser4db")
    IResponseParser responseParser;// 响应报文解析器
    @Autowired
    @Qualifier("urlClient4db")
    IClientFactory client;// 响应报文解析器
    @Autowired
    @Qualifier("EBankConfParser4db")
    IEBankConfParser confParser;// 报文装载器

    final String BASE_PATH = "ares/prod/";

    /**
     * 产品列表
     *
     * @param request
     * @return
     */
    @RequestMapping("ares/db/prodList.do")
    @ResponseBody
    public Map<String, Object> prodList(HttpServletRequest request,HttpServletResponse response) {
        String trans_code = "prodList";
        String transCode = BASE_PATH + trans_code;
        Map<String, Object> rst = new HashMap<String, Object>();
        // 初始化数据总线
        IBusinessContext ctx = new BusinessContext(request,
                IBusinessContext.PARAM_TYPE_MAP);
        // 检查报文定义
        if (!transPrev(ctx, transCode, rst)) {
            return rst;
        }
        // 数据库操作区
        Map<String, Object> params = ctx.getParamMap();
        boolean ok = false;
        try {
            int pageNo = Integer.parseInt((String)params.get("PAGE_NO"));
            int pageSize = Integer.parseInt((String)params.get("PAGE_SIZE"));

            int firstIndex = (pageNo - 1) * pageSize;
            int lastIndex = pageNo * pageSize;

            params.put("firstIndex", firstIndex);
            params.put("lastIndex", lastIndex);
            List<Map<String,Object>> list = service.findList("PROD.prodList", params);
            if(list!=null  && list.size()>0)
			{
            	for(Map<String,Object> prd:list){
					prd.put("PICTURE", customFileMngService.getFileByFileName2Base64((String) prd.get("PICTURE"), CustomFileType.FILE));
            		
            	}
			}
            rst.put("list", list);
            ok = true;
        } catch (Exception e) {
            // 输出错误的关键信息
            logger.error(ctx.getTransLogBean(transCode), e);
        }
        transAfter(ctx, transCode, rst, ok);
        return rst;
    }

    /**
     * 产品详情
     *
     * @param request
     * @return
     */
    @RequestMapping("ares/db/queryProd.do")
    @ResponseBody
    public Map<String, Object> queryProd(HttpServletRequest request) {
        String transCode = BASE_PATH + "queryProd";
        Map<String, Object> rst = new HashMap<String, Object>();
        // 初始化数据总线
        IBusinessContext ctx = new BusinessContext(request,
                IBusinessContext.PARAM_TYPE_MAP);
        // 检查报文定义
        if (!transPrev(ctx, transCode, rst)) {
            return rst;
        }
        // 数据库操作区
        Map<String, Object> params = ctx.getParamMap();
        boolean ok = false;
        try {
            List list = service.findList("PROD.queryProd", params);
            rst.put("list", list);
            ok = true;
        } catch (Exception e) {
            // 输出错误的关键信息
            ok = false;
            logger.error(ctx.getTransLogBean(transCode), e);
        }

        transAfter(ctx, transCode, rst, ok);

        return rst;
    }

    /**
     * 事务前置处理
     *
     * @param ctx
     * @param transCode
     * @param rst
     * @return
     */
    private boolean transPrev(IBusinessContext ctx, String transCode, Map<String, Object> rst) {
        return CtxUtil.transPrev(ctx, transCode, json2MapParamCover,
                requestBuilder, confParser, rst);
    }

    /**
     * 事务之后处理
     *
     * @param ctx
     * @param transCode
     * @param rst
     * @param ok
     */
    private void transAfter(IBusinessContext ctx, String transCode, Map<String, Object> rst,
                            boolean ok) {
        CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
    }

}
