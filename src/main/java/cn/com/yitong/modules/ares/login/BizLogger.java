package cn.com.yitong.modules.ares.login;

import cn.com.yitong.modules.ares.login.util.BizLogManager;

/**
 * 日志接口类，提供给应用程序设计人员调用日志接口
 * @author hry@yitong.com.cn
 * @date 2015年1月9日
 */
public class BizLogger {
	
	/*一般日志*/
	public final static String LOG_LEVEL_INFO = "INFO";
	/*重要日志*/
	public final static String LOG_LEVEL_WARN = "WARN";
	/*重大日志*/
	public final static String LOG_LEVEL_ERROR = "ERROR";
	
	/*日志调用类*/
	private String callingClass;
	/*应用标签*/
	private String apptag;
	
	public void setCallingClass(String callingClass) {
		this.callingClass = callingClass;
	}

	public void setApptag(String apptag) {
		this.apptag = apptag;
	}
	
	private BizLogger(){
	}
	
	public static BizLogger getLogger(Class<?> clazz){
		return getLogger(clazz,null);
	}
	
	public static BizLogger getLogger(Class<?> clazz, String apptag){
		BizLogger bizLogger=  new BizLogger();
		bizLogger.setApptag(apptag);
		bizLogger.setCallingClass(clazz.getName());
		return bizLogger;
	}
	
	public void info(String logdata) {
		info(logdata, null);
	}
	
	public void info(String logdata, String eventId) {
		save(LOG_LEVEL_INFO, logdata, eventId);
	}
	
	public void warn(String logdata) {
		warn(logdata,null);
	}
	
	public void warn(String logdata, String eventId) {
		save(LOG_LEVEL_WARN, logdata, eventId);
	}
	
	public void error(String logdata) {
		error(logdata,null);
	}
	
	public void error(String logdata, String eventId) {
		save(LOG_LEVEL_ERROR,logdata,eventId);
	}
	
	private void save(String logLevel, String logData, String eventId){
		BizLogManager bizLogManager = BizLogManager.getInstance();
		bizLogManager.save(logLevel, logData, callingClass, apptag, eventId);
	}
}
