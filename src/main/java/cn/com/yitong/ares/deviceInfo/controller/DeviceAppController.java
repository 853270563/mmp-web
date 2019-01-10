package cn.com.yitong.ares.deviceInfo.controller;

import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.ares.deviceInfo.service.DeviceAppService;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author winkie 2015/6/24
 *
 */
@Controller
@SuppressWarnings({"unused", "unchecked"})
public class DeviceAppController extends BaseControl{
	
	private Logger logger = YTLog.getLogger(this.getClass());
	
	final String BASE_PATH = "ares/deviceInfo/";
	
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
	private DeviceAppService deviceAppService;
    
    /**
     * 查询设备应用
     * @param request
     * @return
     */
	@RequestMapping("ares/deviceInfo/queryDeviceApps.do")
    @ResponseBody
    public Map<String, Object> queryDeviceApps(HttpServletRequest request) {
		if(logger.isInfoEnabled()) {
			logger.info("查询设备应用=================");
		}
		String trans_code = "queryDeviceApps";
    	String transCode = BASE_PATH + trans_code;
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
			String appType = (String)params.get("APP_TYPE");
			String appSystem = (String)params.get("APP_SYSTEM");
			if(StringUtil.isEmpty(appType) || StringUtil.isEmpty(appSystem)) {
				if(logger.isInfoEnabled()) {
					logger.info("参数为空");
				}
				rst.put("MSG", "参数为空，请检查");
				return rst;
			}
			// 数据库操作
			rst = deviceAppService.queryBlackWhiteApps(appType, appSystem);
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
	 * @param ctx
	 * @param transCode
	 * @param rst
	 * @return
	 */
	private boolean transPrev(IBusinessContext ctx, String transCode, Map rst) {
		// 交易开始，设置交易流水
		commonService.generyTransLogSeq(ctx, transCode);
		return CtxUtil.transPrev(ctx, transCode, json2MapParamCover, requestBuilder, confParser, rst);
	}

	/**
	 * 事务之后处理
	 * @param ctx
	 * @param transCode
	 * @param rst
	 * @param ok
	 */
	private void transAfter(IBusinessContext ctx, String transCode, Map rst, boolean ok) {
		// 生成交易结果
		CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
		// 保存交易日志
		commonService.saveJsonTransLog(ctx, transCode, rst);
	}
}
