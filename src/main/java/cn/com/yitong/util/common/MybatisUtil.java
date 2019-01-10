package cn.com.yitong.util.common;

import java.util.HashMap;
import java.util.Map;

public class MybatisUtil {

	/**
	 * 分离出需要的MAP
	 * 
	 * @param src
	 * @param srcKeys
	 * @param toMap
	 */
	public static Map genery(Map<String, Object> src, String[] srcKeys) {
		Map toMap = new HashMap();
		genery(src, srcKeys, toMap, srcKeys);
		return toMap;
	}

	/**
	 * 分离出需要的MAP
	 * 
	 * @param src
	 * @param srcKeys
	 * @param toMap
	 */
	public static void genery(Map<String, Object> src, String[] srcKeys,
			Map<String, Object> toMap) {
		genery(src, srcKeys, toMap, srcKeys);
	}

	/**
	 * 提取字段，快速构造MAP
	 * 
	 * @param src
	 * @param srcKeys
	 * @param toMap
	 * @param toKeys
	 */
	public static void genery(Map<String, Object> src, String[] srcKeys,
			Map<String, Object> toMap, String[] toKeys) {
		int size = Math.min(srcKeys.length, toKeys.length);
		for (int i = 0; i < size; i++) {
			toMap.put(toKeys[i], src.get(srcKeys[i]));
		}
	}
}
