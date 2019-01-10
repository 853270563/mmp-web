package cn.com.yitong.modules.mobileCrm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.yitong.ares.login.BizLogger;
import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.INetTools;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.servlet.ServerInit;
import cn.com.yitong.util.MessageTools;
import cn.com.yitong.util.YTLog;

@Controller
public class MobileCrmController extends BaseControl {
	public final String INTER_NET = ServerInit.getString("INTER_NET");
	public final int INTER_NET_PORT = ServerInit.getInt("INTER_NET_PORT");
	public final String INTER_NET_SERVER_NAME = ServerInit.getString("INTER_NET_SERVER_NAME");
	private Logger logger = YTLog.getLogger(this.getClass());
	@Autowired
	ICrudService service;
	
	final String BASE_PATH = "creditCard/";
	final BizLogger bizLogger = BizLogger.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("netTools4bankcs")
	INetTools netToolsbankcs;//通讯组件
		

	@SuppressWarnings("unchecked")
	@RequestMapping("mobileCrm/mobileCrm.do")
	@ResponseBody
	public Map cardApply(HttpServletRequest request) {
		logger.info("信用卡申请...start...");
		String transCode = BASE_PATH+"mobileCrm";
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 加载参数
		if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
			return rst;
		}
		// 检查报文定义
//		if (!requestBuilder.buildSendMessage(ctx, confParser, transCode)) {
//			MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
//			return rst;
//		}
//		String userId = ctx.getSessionText("USER_ID").toString() != null ? ctx.getSessionText("USER_ID").toString() : "";
//		String orgId = ctx.getSessionText("OFFICE_ID").toString() != null ? ctx.getSessionText("OFFICE_ID").toString() : "";
		// 数据库操作区	
		Map params = ctx.getParamMap();
	 
		Map<String, Object> mobileMap = new HashMap<String,Object>();
		logger.info(params);
		boolean isOK=true;
		try {
			mobileMap = MobileCrmService.mobileCrm(ctx);
			//rst.put("mobileMap", mobileMap);
			rst.putAll(mobileMap);
		} catch (Exception e) {
			isOK =false;
			logger.error("信用卡申请进件异常："+e);
			rst.put(AppConstants.STATUS, AppConstants.STATUS_FAIL);
			rst.put(AppConstants.MSG, AppConstants.MSG_FAIL);
		}
		if(isOK){
			rst.put(AppConstants.STATUS, AppConstants.STATUS_OK);
			rst.put(AppConstants.MSG, AppConstants.MSG_SUCC);
		}
		logger.info("信用卡申请...end...");
		return rst;
	}	
	/**
	 * 移动crm小微
	 * @param request
	 * @return
	 */
	
	@SuppressWarnings("unchecked")
	@RequestMapping("mobileCrmXw/mobileCrmXw.do")
	@ResponseBody
	public Map crmXw(HttpServletRequest request) {
		logger.info("crmXW...start...");
		String transCode = BASE_PATH+"mobileCrmXw";
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 加载参数
		if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
			return rst;
		}
		// 数据库操作区	
		Map params = ctx.getParamMap();
	 
		Map<String, Object> mobileMap = new HashMap<String,Object>();
		logger.info(params);
		boolean isOK=true;
		try {
			mobileMap = MobileCrmService.mobileCrmXw(ctx);
			//rst.put("mobileMap", mobileMap);
			rst.putAll(mobileMap);
		} catch (Exception e) {
			isOK =false;
			logger.error("信用卡申请进件异常："+e);
			rst.put(AppConstants.STATUS, AppConstants.STATUS_FAIL);
			rst.put(AppConstants.MSG, AppConstants.MSG_FAIL);
		}
		if(isOK){
			rst.put(AppConstants.STATUS, AppConstants.STATUS_OK);
			rst.put(AppConstants.MSG, AppConstants.MSG_SUCC);
		}
		logger.info("信用卡申请...end...");
		return rst;
	}
	
	/**
	 * 移动crm小微趋势图
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("mobileCrmXwPic/mobileCrmXwPic.do")
	@ResponseBody
	public Map crmXwPic(HttpServletRequest request) {
		logger.info("crmXW...start...");
		String transCode = BASE_PATH+"mobileCrmXwPic";
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 加载参数
		if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
			return rst;
		}
		// 数据库操作区	
		Map params = ctx.getParamMap();
	 
		Map<String, Object> mobileMap = new HashMap<String,Object>();
		logger.info(params);
		boolean isOK=true;
		try {
			mobileMap = MobileCrmService.mobileCrmXwPic(ctx);
			//rst.put("mobileMap", mobileMap);
			rst.putAll(mobileMap);
		} catch (Exception e) {
			isOK =false;
			logger.error("趋势图数据申请进件异常："+e);
			rst.put(AppConstants.STATUS, AppConstants.STATUS_FAIL);
			rst.put(AppConstants.MSG, AppConstants.MSG_FAIL);
		}
		if(isOK){
			rst.put(AppConstants.STATUS, AppConstants.STATUS_OK);
			rst.put(AppConstants.MSG, AppConstants.MSG_SUCC);
		}
		logger.info("趋势图数据申请...end...");
		return rst;
	}	
}