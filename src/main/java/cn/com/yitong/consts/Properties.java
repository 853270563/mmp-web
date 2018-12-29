package cn.com.yitong.consts;

import cn.com.yitong.common.utils.ConfigUtils;

public class Properties {

	private Properties() {
	}

	public static String getString(String key) {
		return ConfigUtils.getValue(key, "");
	}

	public static int getInt(String key) {
		return ConfigUtils.getValue(key, 0);
	}

	public static boolean getBoolean(String key) {
		return ConfigUtils.getValue(key, false);
	}

}