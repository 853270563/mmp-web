package cn.com.yitong.util;

import org.apache.log4j.Logger;

/**
 * 提供统一的过滤处理，如密码等敏感信息处理；
 * 
 * @author yaoym
 * 
 */
public class YTLog extends Logger {

	public static boolean enable = true;

	protected YTLog(String name) {
		super(name);
	}

	public void info(Object message) {
		super.info(forbidOutput(message));
	}

	public void info(Object message, Throwable t) {
		super.info(forbidOutput(message), t);
	}

	public void debug(Object message) {
		super.debug(forbidOutput(message));
	}

	public void debug(Object message, Throwable t) {
		super.debug(forbidOutput(message), t);
	}

	public void warn(Object message) {
		super.warn(forbidOutput(message));
	}

	public void warn(Object message, Throwable t) {
		super.warn(forbidOutput(message), t);
	}

	public void error(Object message) {
		super.error(forbidOutput(message));
	}

	public void error(Object message, Throwable t) {
		super.error(forbidOutput(message), t);
	}

	/**
	 * 提供统一的过滤规则
	 */
	private String forbidOutput(Object message) {
		if (message != null) {
			return StringUtil.replacePwd(message.toString());
		}
		return "";
	}
}
