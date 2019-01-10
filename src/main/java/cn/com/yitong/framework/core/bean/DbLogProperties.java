package cn.com.yitong.framework.core.bean;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import cn.com.yitong.util.StringUtil;

public class DbLogProperties {

	private static final String CONFIG = "db_log";
	private static ResourceBundle CONFIG_RES = ResourceBundle.getBundle(CONFIG);

	private static final String FIX_SQL_MAP = "_SQL_MAP";
	private static final String FIX_BASE_CTX_PARAM = "_CTX_BASE_PARAM";
	private static final String FIX_REQ_CTX_PARAM = "_CTX_REQ_PARAM";
	private static final String FIX_RSP_CTX_PARAM = "_CTX_RSP_PARAM";
	private static final String FIX_MSG_PARAM = "_MSG_PARAM";
	private static final String FIX_MSG_TMP = "_MSG_TMP";

	private DbLogProperties() {
	}

	/**
	 * 是否需要记录数据库日志
	 * 
	 * @param transCode
	 * @return
	 */
	public static boolean isNeedSaveLog(String transCode) {
		return getBoolean(transCode);
	}

	/**
	 * 获取SQL_MAP
	 * 
	 * @param transCode
	 * @return
	 */
	public static String getSqlMapConfig(String transCode) {
		return getString(transCode + FIX_SQL_MAP);
	}

	/**
	 * 获取需要提取的请求上下文参数
	 * 
	 * @param transCode
	 * @return
	 */
	public static String[] getBaseCtxParams(String transCode) {
		String params = getString(transCode + FIX_BASE_CTX_PARAM);
		if (StringUtil.isNotEmpty(params)) {
			return params.split(",");
		}
		return null;
	}

	/**
	 * 获取需要提取的请求上下文参数
	 * 
	 * @param transCode
	 * @return
	 */
	public static String[] getRequestCtxParams(String transCode) {
		String params = getString(transCode + FIX_REQ_CTX_PARAM);
		if (StringUtil.isNotEmpty(params)) {
			return params.split(",");
		}
		return null;
	}

	/**
	 * 获取需要提取的请求上下文参数
	 * 
	 * @param transCode
	 * @return
	 */
	public static String[] getResponseCtxParams(String transCode) {
		String params = getString(transCode + FIX_RSP_CTX_PARAM);
		if (StringUtil.isNotEmpty(params)) {
			return params.split(",");
		}
		return null;
	}

	/**
	 * 獲取屬性
	 * @param key
	 * @return
	 */
	private static String getString(String key) {
		try {
			return CONFIG_RES.getString(key);
		} catch (MissingResourceException e) {
		}
		return "";
	}

	/**
	 * 获取布尔值
	 * @param key
	 * @return
	 */
	private static boolean getBoolean(String key) {
		return "true".equals(getString(key));
	}

	/**
	 * 消息中參數
	 * 
	 * @param transCode
	 * @return
	 */
	public static String[] getMessageParams(String transCode) {
		// TODO Auto-generated method stub
		String params = getString(transCode + FIX_MSG_PARAM);
		if (StringUtil.isNotEmpty(params)) {
			return params.split(",");
		}
		return null;
	}

	/**
	 * 消息模板
	 * 
	 * @param transCode
	 * @return
	 */
	public static String getMessageTemplate(String transCode) {
		return getString(transCode + FIX_MSG_TMP);
	}
}