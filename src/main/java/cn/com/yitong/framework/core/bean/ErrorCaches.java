package cn.com.yitong.framework.core.bean;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IErrorCaches;
import cn.com.yitong.framework.service.IErrorCacheService;
import cn.com.yitong.framework.servlet.ServerInit;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

public class ErrorCaches implements IErrorCaches {
	private Logger logger = YTLog.getLogger(ErrorCaches.class);
	@Autowired
	private IErrorCacheService errorCacheService;

	private Map<String, Object> errorCodes = new HashMap<String, Object>();
	private Map<String, Object> errorCodeMappings = new HashMap<String, Object>();

	private static String APP_ID = "APP_ID";
	private static String ERR_MSG_CN = "ERR_MSG_CN";
	private static String ERR_MSG_EN = "ERR_MSG_EN";
	private static String ERR_MSG_HK = "ERR_MSG_HK";
	private static String REF_ERR_CODE = "REF_ERR_CODE"; //P_ERROR_MAPPING网银保存的第三方错误码

	@Override
	public void init() {
		logger.debug("--------- init ErrorCaches start -----");
		initErrorCode();
		logger.debug("errorCodes : " + errorCodes);
		initErrorMapping();
		logger.debug("errorCodeMappings : " + errorCodeMappings);
		logger.debug("--------- init ErrorCaches end --------");
		
		ServerInit.errorCaches = this;
		
	}
	
	
	public void initErrorCode(){
		Map m = new HashMap();
		List<Map> allCode = errorCacheService.findAllErrCode(m);
		String errCode = "";
		for (Map record : allCode) {
			errCode = (String)record.get(AppConstants.ERR_CODE);
			errorCodes.put(errCode, record);
		}
	}
	
	public void initErrorMapping(){
		Map m = new HashMap();
		List<Map> allMapping = errorCacheService.findAllErrMapping(m);
		String refErrCode = "";
		for (Map record : allMapping) {
			refErrCode = (String)record.get(REF_ERR_CODE);
			errorCodeMappings.put(refErrCode, record);
		}
	}
	/**
	 * 根据错误码获取错误信息（网银）
	 * @param ctx
	 * @param code
	 * @return
	 */
	public String getErrMsgbyCode(String language,String code){
		String errMsg = "";
		if(StringUtil.isEmpty(language)){
			language = AppConstants.ZH_HK;
		}
		if(errorCodes.containsKey(code)){
			Map errMap = (Map)errorCodes.get(code);
			errMsg = (String)errMap.get(language);
		}
		return errMsg;
	}
	
	/**
	 * 根据错误码获取错误信息(外部系统)
	 * @param ctx
	 * @param code
	 * @return
	 */
	public String getErrMsgbyRefcode(String language,String refcode){
		String errMsg = "";
		if(StringUtil.isEmpty(language)){
			language = AppConstants.ZH_HK;
		}
		if(errorCodeMappings.containsKey(refcode)){
			Map errMap = (Map)errorCodeMappings.get(refcode);
			String errcode = (String)errMap.get(AppConstants.ERR_CODE);
			errMsg = getErrMsgbyCode(language,errcode);
		}
		return errMsg;
	}
	
	/**
	 * 根据错误码获取错误信息(外部系统)
	 * @param ctx
	 * @param code
	 * @return
	 */
	public Map getErrInfobyRefcode(String language,String refcode){
		String errcode = "";
		String errMsg = "";
		Map errInfo = new HashMap();
		if(StringUtil.isEmpty(language)){
			language = AppConstants.ZH_HK;
		}
		if(errorCodeMappings.containsKey(refcode)){
			Map errMap = (Map)errorCodeMappings.get(refcode);
			errcode = (String)errMap.get(AppConstants.ERR_CODE);
			errMsg = getErrMsgbyCode(language,errcode);
		}
		errInfo.put(AppConstants.ERR_CODE, errcode);
		errInfo.put(AppConstants.ERR_MSG, errMsg);
		return errInfo;
	}
}
