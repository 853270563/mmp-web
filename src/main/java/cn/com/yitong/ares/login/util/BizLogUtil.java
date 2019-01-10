package cn.com.yitong.ares.login.util;

import cn.com.yitong.common.utils.JsonUtils;
import cn.com.yitong.common.utils.http.HttpContext;
import cn.com.yitong.common.utils.http.HttpUtils;
import cn.com.yitong.core.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
/**
 * 日志工具类
 * 
 * @author hry@yitong.com.cn
 * @date 2015年1月13日
 */
public class BizLogUtil {

	private static Logger logger = LoggerFactory.getLogger(BizLogUtil.class);
	
	transient int lineNumber;
	transient String fileName;
	transient String className;
	transient String methodName;
	
	private static Method getStackTraceMethod;
	private static Method getClassNameMethod;
	private static Method getMethodNameMethod;
	private static Method getLineNumberMethod;

	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	static {
		Class<?>[] noArgs = null;
		try{
			getStackTraceMethod = Throwable.class.getMethod("getStackTrace",noArgs);
			Class<?> stackTraceElementClass = Class.forName("java.lang.StackTraceElement");
			getClassNameMethod = stackTraceElementClass.getMethod("getClassName", noArgs);
			getMethodNameMethod = stackTraceElementClass.getMethod("getMethodName", noArgs);
			getLineNumberMethod = stackTraceElementClass.getMethod("getLineNumber", noArgs);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 初始化一个BizLogInfo
	 * @author hry@yitong.com.cn
	 * @date 2015年1月13日
	 */
	public static BizLogInfo createLogInfo(String loglevel, String logdata, String callingClass, String apptag, Session session){
		BizLogInfo logInfo = new BizLogInfo();
		logInfo.setUser_id(session.getUserId());
		logInfo.setDevice_id(session.getDeviceCode());
		logInfo.setServer_id(session.getServerId());
		logInfo.setMsg_id(session.getMsgId());
		logInfo.setEvent_id(session.getEventId());
		logInfo.setSession_id(session.getId());
		
		logInfo.setLog_time(dateFormat.format(new Date()));
		logInfo.setLog_level(loglevel);
		logInfo.setLog_data(logdata);
		logInfo.setApp_tag(apptag);
		logInfo.setUrl(getCallInfo(callingClass));
		return logInfo;
	}

	/**
	 * 行为日志实体转换为参数map
	 */
	public static Map<String, Object> logConverToMap(BizLogInfo logInfo) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("LOG_ID", UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20));
		map.put("MSG_ID", logInfo.getMsg_id());
		map.put("EVENT_ID", logInfo.getEvent_id());
		map.put("USER_ID", logInfo.getUser_id());
		map.put("DEVICE_ID", logInfo.getDevice_id());
		map.put("SERVER_ID", logInfo.getServer_id());
		map.put("URL", logInfo.getUrl());
		map.put("APP_TAG", logInfo.getApp_tag());
		map.put("LOG_LEVEL", logInfo.getLog_level());
		map.put("LOG_TIME", logInfo.getLog_time());
		map.put("LOG_DATA", logInfo.getLog_data());
		map.put("SESSION_ID", logInfo.getSession_id());
		map.put("CHANNEL_ID", logInfo.getChannel_id());
		map.put("APP_ID", logInfo.getApp_id());
		map.put("APP_VERS", logInfo.getApp_vers());
		map.put("IP_ADDR", logInfo.getIp_addr());
		map.put("GPS", logInfo.getGps());
		map.put("PLACE", logInfo.getPlace());
		return map;
	}

	/**
	 * 获取日志调用class信息
	 * @return 返回字符串格式(包名.类名.方法名:调用行数)
	 * @author hry@yitong.com.cn
	 * @date 2015年1月13日
	 */
	public static String getCallInfo(String callingClass) {
		Object[] noArgs = null;
		String className = null;
		String methodName = null;
		int lineNumber = 0;
		try{
			Object[] elements = (Object[]) (Object[]) getStackTraceMethod.invoke(new Throwable(), noArgs);
			for (int i = elements.length - 1; i >= 0; i--) {
				String thisClass = (String) getClassNameMethod.invoke(elements[i], noArgs);
				if (callingClass.equals(thisClass)) {
					className=thisClass;
					methodName = ((String) getMethodNameMethod.invoke(elements[i], noArgs));
					lineNumber = (Integer) getLineNumberMethod.invoke(elements[i], noArgs);
					break;
				}
			}
			if(className != null){
				return className+"."+methodName+":"+lineNumber;
			}
		}catch (Exception e){
			logger.error("Method.invoke异常，获取调用方法信息失败", e);
		}
		return callingClass;
	}
	/**
	 * 
	 * @param logInfoStr 带提交的日志Json串
	 * @author lc3@yitong.com.cn
	 */
	public static boolean postToServer(String logInfoStr, String url){
		boolean flag;
        try{
			String returnJson = HttpUtils.httpPost(new HttpContext(url).addParam("logList", logInfoStr));
	        logger.debug("returnJson:{}", returnJson);
			Map returnMap = JsonUtils.jsonToMap(returnJson);
			flag = (null != returnJson && "1".equals(returnMap.get("STATUS")));
		}catch (Exception e) {
			logger.error("日志服务器接口连接异常", e);
			flag = false;
		}
		return flag;
	}
}
