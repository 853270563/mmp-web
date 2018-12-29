package cn.com.yitong.core.cache;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheFactoryBean;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 创建CacheManager FactoryBean
 * @author zhuzengpeng
 *
 */
public class CacheManagerFactoryBean implements FactoryBean<CacheManager>, InitializingBean {
	
	private static Logger logger = LoggerFactory.getLogger(CacheManagerFactoryBean.class);
	
	// 缓存类型：simple, redis
	@Value("${cache.type}")
	private String cacheType;
	
	// simple cache需要配置
	@Autowired(required=false)
	@Value("${cache.names}") 
	private String cacheNames;
	
	// redis cache需要配置
	@Autowired(required=false)
	private RedisTemplate<?, ?> redisTemplate; 
	
	private CacheManager cacheManager;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if("simple".equals(cacheType.trim()))
			cacheManager = createSimpleCacheManager();
		else if("redis".equals(cacheType.trim()))
			cacheManager = createRedisCacheManager();
		else {
			throw new IllegalArgumentException("cacheType值不正确，" +
					"请确认在properties文件中配置cache.type，可选值为：simple 或 redis");
		}
	}
	
	/**
	 * 创建 simple cache manager
	 * @return
	 */
	private CacheManager createSimpleCacheManager() {
		logger.info("创建simpleCache,cacheNames:{}", cacheNames);
		SimpleCacheManager cacheManager = new SimpleCacheManager();
		if(StringUtils.isBlank(cacheNames))
			throw new IllegalArgumentException("缓存类型为simple，需要在properties中配置cache.names");
			
		String[] names = StringUtils.split(cacheNames, ",");
		
		List<ConcurrentMapCache> caches = new ArrayList<ConcurrentMapCache>();
		for(String cacheName : names) {
			ConcurrentMapCacheFactoryBean factoryBean = new ConcurrentMapCacheFactoryBean();
			factoryBean.setName(cacheName);
			factoryBean.afterPropertiesSet();
			
			caches.add(factoryBean.getObject());
		}
		
		cacheManager.setCaches(caches);
		cacheManager.afterPropertiesSet();
		
		return cacheManager;
	}
	
	/**
	 * 创建 redis cache manager
	 * @return
	 */
	private CacheManager createRedisCacheManager() {
		logger.info("创建redis cache");
		if(redisTemplate == null)
			throw new IllegalArgumentException("redisTemplate bean 没有配置, 无法使用redis cache");
		
		return new RedisCacheManager(redisTemplate);
	}

	@Override
	public CacheManager getObject() throws Exception {
		return cacheManager;
	}

	@Override
	public Class<CacheManager> getObjectType() {
		return CacheManager.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
