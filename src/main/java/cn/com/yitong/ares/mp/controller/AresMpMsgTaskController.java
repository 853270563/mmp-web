package cn.com.yitong.ares.mp.controller;

import cn.com.yitong.core.model.SysDict;
import cn.com.yitong.core.util.DictUtils;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.ares.mp.service.AresMpMsgTaskService;
import cn.com.yitong.util.YTLog;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 孙伟(sunw@yitong.com.cn)
 */
@Controller
public class AresMpMsgTaskController extends BaseControl {

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

    @Autowired
    private AresMpMsgTaskService aresMpMsgTaskService;


    final String BASE_PATH = "ares/mp/";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    
    /**
     * 消息推送
     * @param request
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("ares/mp/aresMpMsgTaskInsert.do")
    @ResponseBody
    public Map insertAresMpMsgTask(HttpServletRequest request, Map<String, Object> pamap) {
        Map rst = new HashMap();
    	// 数据库操作区
        Map params = pamap;
        String trans_code = "AresMpMsgTask";
        String transCode = BASE_PATH + trans_code;
        IBusinessContext ctx = null;
    	if (request != null) {
            // 初始化数据总线
            ctx = new BusinessContext(request,  IBusinessContext.PARAM_TYPE_MAP);
            // 检查报文定义
            if (!transPrev(ctx, transCode, rst)) {
                return rst;
            }
            if (CtxUtil.debugTrans(trans_code)) {
                boolean ok = client.execute(ctx, transCode);
                transAfter(ctx, transCode, rst, ok);
                return rst;
            }
            // 数据库操作区
            params = ctx.getParamMap();
    	}
        
        Map<String, String> map = this.paramCheck(params);
        if (!map.isEmpty()) {
        	return map;
        }
        if(logger.isInfoEnabled()) {
            logger.info("消息任务=================" + params.toString());
        }
        boolean ok = false;
        try {
        	ok= aresMpMsgTaskService.insertAresMpMsgTask(params);
        } catch (Exception e) {
        	ok = false;
        	logger.error(e.getMessage());
        }
        if (request != null) {
            transAfter(ctx, transCode, rst, ok);
        } else {
        	rst.put("STATUS", "1");
        }
        return rst;
    }
    
    /**
     * 消息模板推送
     * @param request
     * @return
     */
    @SuppressWarnings("rawtypes")
	@RequestMapping("ares/mp/aresMpMsgTmplInsert.do")
    @ResponseBody
    public Map tmplInsert(HttpServletRequest request) {
        String trans_code = "AresMpMsgTmpl";
        String transCode = BASE_PATH + trans_code;
        Map rst = new HashMap();
        // 初始化数据总线
        IBusinessContext ctx = new BusinessContext(request,
                IBusinessContext.PARAM_TYPE_MAP);
        // 检查报文定义
        if (!transPrev(ctx, transCode, rst)) {
            return rst;
        }
        if (CtxUtil.debugTrans(trans_code)) {
            boolean ok = client.execute(ctx, transCode);
            transAfter(ctx, transCode, rst, ok);
            return rst;
        }
        // 数据库操作区
        Map params = ctx.getParamMap();
        
        Map<String, String> map = this.tmplParamCheck(params);
        if (!map.isEmpty()) {
        	return map;
        }
        if(logger.isInfoEnabled()) {
            logger.info("消息任务=================" + params.toString());
        }
        boolean ok = false;
        try {
        	ok= aresMpMsgTaskService.insertAresMpMsgTask(params);
        } catch (Exception e) {
        	ok = false;
        	logger.error(e.getMessage());
        }
        transAfter(ctx, transCode, rst, ok);
        return rst;
    }

    /**
     * 消息模板参数check
     * 
     * @param params
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private Map<String, String> tmplParamCheck(Map params) {
    	Map<String, String> rst  = new HashMap<String, String>();
    	String msg = "";
    	
    	Map<String, Object> tmplMap = service.load("ARES_MP_MSG_TMPL.loadTmpl", params);
    	if (tmplMap == null || tmplMap.isEmpty()) {
        	rst.put("STATUS", "0");
        	msg += "消息模板：" + params.get("TMPLID") + " 不存在；";
        	rst.put("MSG", msg);
    	}
        // 接受客户号来源
        boolean dictFlag = false;
        List<SysDict> dictList = DictUtils.getDictionaries("REC_CUST_FROM");
        String recCustFrom = (String)params.get("RECCUSTFROM");
        for (SysDict dict : dictList) {
        	if (recCustFrom.equals(dict.getValue())) {
        		dictFlag = true;
        		break;
        	}
        }
        if (!dictFlag) {
        	rst.put("STATUS", "0");
        	msg += "接受客户号来源：" + recCustFrom + " 不存在；";
        	rst.put("MSG", msg);
        }
        // 发送开始时间，发送结束时间
        try {
        	sdf.parse((String) params.get("SENDBEGINTIME"));
        	if (params.get("SENDENDTIME") != null) {
        		sdf.parse((String) params.get("SENDENDTIME"));
        	}
        } catch (Exception e) {
        	rst.put("STATUS", "0");
        	msg += "日期格式不正确。";
        	rst.put("MSG", msg);
        }
        
        if (!rst.isEmpty()) {
        	return rst;
        }
        
        params.put("CHANTYPE", tmplMap.get("CHAN_TYPE"));
        params.put("SENDTYPE", tmplMap.get("SEND_TYPE"));
        params.put("PUSHTYPE", tmplMap.get("PUSH_TYPE"));
        params.put("MSGTYPE", tmplMap.get("TYPE_ID"));
        params.put("CLIENTAPPID", tmplMap.get("APP_ID"));
        params.put("ISTMPL", "1");
        
        String tmplBody = (String) tmplMap.get("TMPL_BODY");
        Object[] tagVales = ((String) params.get("TMPLTAGVAL")).split(",");
        String msgBody = MessageFormat.format(tmplBody.replaceAll("\\$\\{", "\\{"), tagVales);
        params.put("MSGBODY", msgBody);
        params.put("CLICKACTTYPE", tmplMap.get("CLICK_ACT_TYPE"));
        params.put("CLICKACTBODY", tmplMap.get("CLICK_ACT_BODY"));
        params.put("PUSHCLIENTTYPE", tmplMap.get("CLIENT_TYPE"));
        params.put("DISTVAL", tmplMap.get("DIST_VAL"));
        params.put("PRIOVAL", tmplMap.get("PRIO_VAL"));
        
		return rst;
	}
	/**
     * 参数check
     * @param params
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private Map<String, String> paramCheck(Map params) {
    	Map<String, String> rst = new HashMap<String, String>();
    	String msg = "";
        boolean dictFlag = false;
        List<SysDict> dictList = null;
        // 通道类型
        dictList = DictUtils.getDictionaries("CHAN_TYPE");
        String chanType = (String)params.get("CHANTYPE");
        for (SysDict dict : dictList) {
        	if (chanType.equals(dict.getValue())) {
        		dictFlag = true;
        		break;
        	}
        }
        if (!dictFlag) {
        	rst.put("STATUS", "0");
        	msg += "通道类型：" + chanType + " 不存在；";
        	rst.put("MSG", msg);
        }
        // 消息发送类型
        dictFlag = false;
        dictList = DictUtils.getDictionaries("SEND_TYPE");
        String sendType = (String)params.get("SENDTYPE");
        for (SysDict dict : dictList) {
        	if (sendType.equals(dict.getValue())) {
        		dictFlag = true;
        		break;
        	}
        }
        if (!dictFlag) {
        	rst.put("STATUS", "0");
        	msg += "消息发送类型：" + sendType + " 不存在；";
        	rst.put("MSG", msg);
        }
        // 推送类型
        dictFlag = false;
        dictList = DictUtils.getDictionaries("PUSH_TYPE");
        String pushType = (String)params.get("PUSHTYPE");
        for (SysDict dict : dictList) {
        	if (pushType.equals(dict.getValue())) {
        		dictFlag = true;
        		break;
        	}
        }
        if (!dictFlag) {
        	rst.put("STATUS", "0");
        	msg += "推送类型：" + pushType + " 不存在；";
        	rst.put("MSG", msg);
        }
        // 消息类型
        if (params.get("MSGTYPE") != null) {
            Map msgTypeMap = service.load("ARES_MP_MSG_TASK.loadMsgType", params);
            if (msgTypeMap == null || msgTypeMap.isEmpty()) {
            	rst.put("STATUS", "0");
            	msg += "消息类型：" + params.get("MSGTYPE") + " 不存在；";
            	rst.put("MSG", msg);
            }
        }
        // 接受客户号来源
        dictFlag = false;
        dictList = DictUtils.getDictionaries("REC_CUST_FROM");
        String recCustFrom = (String)params.get("RECCUSTFROM");
        for (SysDict dict : dictList) {
        	if (recCustFrom.equals(dict.getValue())) {
        		dictFlag = true;
        		break;
        	}
        }
        if (!dictFlag) {
        	rst.put("STATUS", "0");
        	msg += "接受客户号来源：" + recCustFrom + " 不存在；";
        	rst.put("MSG", msg);
        }
        // 点击通知动作类型
        dictFlag = false;
        String clickActType = (String)params.get("CLICKACTTYPE");
        if(clickActType!=null  && !clickActType.equals("")){
            dictList = DictUtils.getDictionaries("CLICK_ACT_TYPE");
            for (SysDict dict : dictList) {
                if (clickActType.equals(dict.getValue())) {
                    dictFlag = true;
                    break;
                }
            }
        }

        if (!dictFlag && !"3".equals(clickActType)) {
        	rst.put("STATUS", "0");
        	msg += "点击通知动作类型：" + clickActType + " 不存在；";
        	rst.put("MSG", msg);
        }
        // 推送终端类型
        dictFlag = false;
        dictList = DictUtils.getDictionaries("PUSH_CLIENT_TYPE");
        String pushClientType = (String)params.get("PUSHCLIENTTYPE");
        for (SysDict dict : dictList) {
        	if (pushClientType.equals(dict.getValue())) {
        		dictFlag = true;
        		break;
        	}
        }
        if (!dictFlag) {
        	rst.put("STATUS", "0");
        	msg += "推送终端类型：" + pushClientType + " 不存在；";
        	rst.put("MSG", msg);
        }
        // 客户端应用标识
        Map<String, Object> load = service.load("ARES_MP_MSG_TASK.loadApp", params);
        if (load == null || load.isEmpty()) {
        	rst.put("STATUS", "0");
        	msg += "客户端应用：" + (String)params.get("CLIENTAPPID") + " 不存在；";
        	rst.put("MSG", msg);
        }
        // 发送开始时间，发送结束时间
        try {
        	sdf.parse((String) params.get("SENDBEGINTIME"));
        	if (params.get("SENDENDTIME") != null) {
        		sdf.parse((String) params.get("SENDENDTIME"));
        	}
        } catch (Exception e) {
        	rst.put("STATUS", "0");
        	msg += "日期格式不正确。";
        	rst.put("MSG", msg);
        }
        
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