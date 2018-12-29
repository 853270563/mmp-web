package cn.com.yitong.core.util;

import cn.com.yitong.common.utils.SpringContextUtils;
import cn.com.yitong.core.cache.CacheNames;
import cn.com.yitong.modules.ares.dictSyn.dao.DictDao;
import cn.com.yitong.modules.ares.dictSyn.model.SysDict;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 缓存工具类
 * @author zhuzengpeng
 *
 */
public class DictUtils {
	
	private static CacheManager cacheManager = SpringContextUtils.getBean(CacheManager.class);
	private static DictDao dictDao = SpringContextUtils.getBean(DictDao.class);
	/**
	 * 值放入缓存
	 */
	public static void putDictionaries(String dictTypeCode, List<SysDict> dictionaries) {
		if(dictionaries != null)
			cacheManager.getCache(CacheNames.DICT_CACHE_NAME).put(dictTypeCode, dictionaries);
	}
	
	/**
	 * 从缓存取得值
	 */
	@SuppressWarnings("unchecked")
	public static List<SysDict> getDictionaries(String dictTypeCode) {
		ValueWrapper vw =  cacheManager.getCache(CacheNames.DICT_CACHE_NAME).get(dictTypeCode);
		if(vw==null){
			return getDictValuesFromDb(dictTypeCode);
		}else{
			return (List<SysDict>)vw.get();
		}
	}
	
	/**
	 * 清除dictTypeCode对应的缓存
	 */
	public static void cleanDictionaries(String dictTypeCode) {
		cacheManager.getCache(CacheNames.DICT_CACHE_NAME).evict(dictTypeCode);
	}

	/**
	 * 清楚 cacheName 缓存
	 * @param cacheName 缓存名称
	 */
	public static void cleanDictByCacheName(String cacheName) {
		if(null != cacheManager.getCache(cacheName)) {
			cacheManager.getCache(cacheName).clear();
		}
	}
	
	private static List<SysDict> getDictValuesFromDb(String dictTypeCode) {
		List<SysDict> list = dictDao.getDictValuesFromDb(dictTypeCode);
		putDictionaries(dictTypeCode, list);
		return list;
	}
	
	public static Map<String, String> getValue2LabelMap(String dictTypeCode) {
		List<SysDict> list = getDictionaries(dictTypeCode);
		Map<String, String> map = new HashMap<String, String>();
		for(SysDict dict:list) {
			map.put(dict.getValue(), dict.getLabel());
		}
		return map;
	}
	
	/**
	 * 根据数据字典TYPE、VALUE查询出对应的VALUE值的中文含义
	 */
	public static String getDictLabel(String value, String dictTypeCode, String defaultLabel) {
		List<SysDict> list = getDictionaries(dictTypeCode);
		for(SysDict dict:list) {
			if(value.equals(dict.getValue())) {
				defaultLabel = dict.getLabel();
				break;
			}
		}
		return defaultLabel;
	}

	/**
	 * 根据字典类型和描述 查询值
	 * @param dictTypeCode 字典类型
	 * @param label 描述
	 * @param defaultValue 默认值
	 * @return
	 */
	public static String getDictValue(String dictTypeCode, String label, String defaultValue) {
		List<SysDict> list = getDictionaries(dictTypeCode);
		if(null != list && list.size() > 0) {
			for(SysDict dict : list) {
				if(label.equals(dict.getLabel())) {
					defaultValue = dict.getValue();
					break;
				}
			}
		}
		return defaultValue;
	}
}
