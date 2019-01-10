package cn.com.yitong.util.common;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import cn.com.yitong.ares.error.AresRuntimeException;

public class ValidUtils {

	/**
	 * 验证多个参数是否为空
	 * 
	 * @param paramMap
	 */
	public static void validEmpty(Map<String, String> paramMap) {
		Set<Entry<String, String>> paramSet = paramMap.entrySet();
		for (Entry<String, String> entry : paramSet) {
			if (StringUtils.isBlank(entry.getValue())) {
				throw new AresRuntimeException("common.parameter_empty", entry.getKey());
			}
		}
	}

	/**
	 * 验证单个参数是否为空
	 * 
	 * @param key
	 *            参数名称
	 * @param value
	 *            参数值
	 */
	public static String validEmpty(String key, Map<String, String> paramMap) {
		String value = paramMap.get(key);
		if (StringUtils.isBlank(value)) {
			throw new AresRuntimeException("common.parameter_empty", key);
		}
		return value;
	}
	
	/**
	 * 验证单个参数是否为空
	 * 
	 * @param key
	 *            参数名称
	 * @param value
	 *            参数值
	 */
	public static String validEmpty(String value) {
		if (StringUtils.isBlank(value)) {
			throw new AresRuntimeException("common.parameter_empty", value);
		}
		return value;
	}

	/**
	 * 验证单个参数是否为空
	 * 
	 * @param key
	 *            参数名称
	 * @param value
	 *            参数值
	 */
	public static void validEmpty(Map<String, String> paramMap, String... key) {
		for (String str : key) {
			String value = paramMap.get(str);
			if (StringUtils.isBlank(value)) {
				throw new AresRuntimeException("common.parameter_empty", str);
			}
		}
	}
	
	/**
	 * 验证list是否为空
	 * @param list
	 * @return
	 */
	public static boolean isEmpty(List list){
		if(null == list || list.size() ==0){
			return true;
		}
		return false;
	}
	/**
	 * 验证map是否为空
	 * @param list
	 * @return
	 */
	public static boolean isEmpty(Map map){
		if(null == map || map.size() ==0){
			return true;
		}
		return false;
	}
}
