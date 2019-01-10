package cn.com.yitong.ares.core;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import cn.com.yitong.util.common.StringUtil;

public class AresConf {

	private static final String CONFIG = "META-INF/ares_conf";
	private static ResourceBundle CONFIG_RES = ResourceBundle.getBundle(CONFIG);

	public static String jclModuleRootPath;
	
	private AresConf() {
	}

	public static String jclRootPath() {
		return getString("JCL_ROOT_PATH");
	}

    public static String getAppVer(){
        return getString("APP_VER");
    }

    public static String getAppPath(){
        return getString("APP_PATH");
    }


    public static String getString(String key) {
		try {
			return CONFIG_RES.getString(key);
		} catch (MissingResourceException e) {
			// CONFIG_RES = ResourceBundle.getBundle(CONFIG);
		}
		return "";
	}

	public static int getInt(String key) {
		String value = getString(key);
		if (!StringUtil.isEmpty(value)) {
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