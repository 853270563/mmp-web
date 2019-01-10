package cn.com.yitong.framework.core.bean;

import java.io.UnsupportedEncodingException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import cn.com.yitong.util.ParamUtil;

public class SmsProperties {

	private static final String CONFIG = "sms";
	private static ResourceBundle CONFIG_RES = ResourceBundle.getBundle(CONFIG);

	private SmsProperties() {
	}

	public static String getString(String key) {
		try {
			return CONFIG_RES.getString(key);
		} catch (MissingResourceException e) {
			// CONFIG_RES = ResourceBundle.getBundle(CONFIG);
		}
		return "";
	}

	public static String getUtf8(String key) {
		String text = getString(key);
		try {
			return new String(text.getBytes("ISO8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static int getInt(String key) {
		String value = getString(key);
		if (!ParamUtil.isEmpty(value)) {
			try {
				return Integer.parseInt(value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	public static boolean getBoolean(String key) {
		return "true".equals(getString(key));
	}

}