package cn.com.yitong.core.util;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;

import cn.com.yitong.common.utils.SpringContextUtils;
import cn.com.yitong.core.cache.CacheNames;

/**
 * @author zhuzengpeng<zzp@yitong.com.cn>
 */
public class CacheUtils {
	
	public static CacheManager cacheManager = SpringContextUtils.getBean(CacheManager.class);
	private static StringRedisTemplate redisTemplate = SpringContextUtils.getBean(StringRedisTemplate.class);

	/**
	 * 把值放入缓存
	 */
	public static void put(Object key, Object value) {
		cacheManager.getCache(CacheNames.DEFAULT_CACHE_NAME).put(key, value);
	}

	/**
	 * 根据KEY取得缓存中的值
	 */
	public static Object get(Object key) {
		ValueWrapper vw =  cacheManager.getCache(CacheNames.DEFAULT_CACHE_NAME).get(key);
		if(vw != null) {
			return vw.get();
		}
		return null;
	}
	
	/**
	 * 清除缓存
	 */
	public static void evict(Object key) {
		cacheManager.getCache(CacheNames.DEFAULT_CACHE_NAME).evict(key);
	}
	
	/**
	 * 值放入缓存同时设置有效时长(在本框架中只有设置缓存为Redis时才有效)
	 * @param key
	 * @param value
	 * @param expiration 超时时长。单位：分钟。设置为0时没有超时时长。
	 */
	public static void put(String key, Object value, long expiration) {
		cacheManager.getCache(CacheNames.DEFAULT_CACHE_NAME).put(key, value);
		if(expiration > 0) {
			setExpires(key, expiration);
		}
	}
	
	/**
	 * 在Redis集群环境下,对键值设置有效时长
	 * @param key 
	 * @param expiration 有效时长(单位分钟)
	 */
	public static void setExpires(String key, long expiration) {
		if(cacheManager instanceof RedisCacheManager) {		
			redisTemplate.expire(key, expiration, TimeUnit.MINUTES);
		}
	}
}
