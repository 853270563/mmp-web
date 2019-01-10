package cn.com.yitong.util.common;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.lang.math.NumberUtils;

public class Properties {

	// 配置文件，为交易指定特定的交易控制器模型
	private static final String CONFIG = "META-INF/ares_conf";
	private static ResourceBundle CONFIG_RES = ResourceBundle.getBundle(CONFIG);

	public static String getString(String transCode) {
		try {
			return CONFIG_RES.getString(transCode);
		} catch (MissingResourceException e) {
		}
		return "";
	}
		
	public static int getInt(String transCode) {
		try {
			return NumberUtils.toInt(CONFIG_RES.getString(transCode),0);
		} catch (MissingResourceException e) {
		}
		return 0;
	}
	
	public static Long getLong(String transCode) {
		try {
			return NumberUtils.toLong(CONFIG_RES.getString(transCode),0);
		} catch (MissingResourceException e) {
		}
		return 0l;
	}
}
