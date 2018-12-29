package cn.com.yitong.modules.common.deviceCrash.controller;

import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.modules.common.deviceCrash.model.DeviceCrashLog;
import cn.com.yitong.modules.common.deviceCrash.service.DeviceCrashLogService;
import cn.com.yitong.util.MapUtil;
import cn.com.yitong.util.TransSeqNoUtils;
import cn.com.yitong.util.YTLog;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@SuppressWarnings({"unused", "unchecked"})
public class DeviceCrashController extends BaseControl{
	
	private Logger logger = YTLog.getLogger(this.getClass());
	
	private static final SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
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
    private DeviceCrashLogService deviceCrashLogService;
    
	@RequestMapping("common/deviceCrash/saveCrashLog.do")
    @ResponseBody
    public Map<String, Object> eraseDeviceCheck(HttpServletRequest request) {
		String trans_code = "saveCrashLog";
    	String transCode = "common/deviceCrash/" + trans_code;
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
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
		boolean ok = false;
		try {
			// 数据库操作
			DeviceCrashLog log = MapUtil.map2EntityHandler(params, DeviceCrashLog.class);
			log.setLogId(TransSeqNoUtils.getSeqNoUtilsByDate(6));
			log.setCreateTime(sdfDateTime.format(new Date()));
			deviceCrashLogService.save(log);
	        ok = true;
		} catch (Exception e) {
			// 输出错误的关键信息
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
    }

    /**
	 * 事务前置处理
	 */
	private boolean transPrev(IBusinessContext ctx, String transCode, Map rst) {
		return CtxUtil.transPrev(ctx, transCode, json2MapParamCover, requestBuilder, confParser, rst);
	}

	/**
	 * 事务之后处理
	 */
	private void transAfter(IBusinessContext ctx, String transCode, Map rst, boolean ok) {
		CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
	}
}
