package cn.com.yitong.ares.mp.controller;

import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.util.YTLog;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 孙伟(sunw@yitong.com.cn)
 */
@Controller
public class AresMpMsgTypeController extends BaseControl {

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

    final String BASE_PATH = "ares/mp/";

    
    /**
     * 消息类型列表
     * @param request
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("ares/mp/aresMpMsgTypeList.do")
    @ResponseBody
    public Map insertAresMpMsgTask(HttpServletRequest request) {
        String trans_code = "AresMpMsgType";
        String transCode = BASE_PATH + trans_code;
        Map rst = new HashMap();
        // 初始化数据总线
        IBusinessContext ctx = new BusinessContext(request,
                IBusinessContext.PARAM_TYPE_MAP);
        // 检查报文定义
        if (!transPrev(ctx, transCode, rst)) {
            return rst;
        }
        boolean ok = false;
        try {
        	List<Map<String, String>> msgTypeList = service.findList("MP_MSG_PUSH.selectMsgTypeList", null);
        	rst.put("LIST", msgTypeList);
        	ok = true;
        } catch (Exception e) {
        	logger.error(e.getMessage());
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
    @SuppressWarnings("rawtypes")
    private boolean transPrev(IBusinessContext ctx, String transCode, Map rst) {
        // 交易开始，设置交易流水
        commonService.generyTransLogSeq(ctx, transCode);
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
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void transAfter(IBusinessContext ctx, String transCode, Map rst,
                            boolean ok) {
        // 生成交易结果
        CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
        // 保存交易日志
        commonService.saveJsonTransLog(ctx, transCode, rst);
    }
}