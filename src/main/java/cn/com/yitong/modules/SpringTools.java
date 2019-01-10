package cn.com.yitong.modules;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class SpringTools implements ApplicationContextAware {
	private static ApplicationContext applicationContext;
	
	private static Map cacheCtrls = new HashMap();

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		SpringTools.applicationContext = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * 业务模型控制器处理实例 是否已装载
	 * 
	 * @param beanName
	 * @return
	 */
	public static boolean isInitedCtrlBean(String beanName) {
		return cacheCtrls.containsKey(beanName);
	}

	/**
	 * 快速加载控制器
	 * 
	 * @param beanName
	 * @return
	 */
	public static <T> T getCtrlBean(String beanName) {
		if (isInitedCtrlBean(beanName)) {
			return (T) cacheCtrls.get(beanName);
		} else {
			Object bean = applicationContext.getBean(beanName);
			cacheCtrls.put(beanName, bean);
			return (T) bean;
		}
	}

	/**
	 * 删除缓存 业务模型控制器处理实例
	 * 
	 * @param beanName
	 */
	public static void removeCache(String beanId) {
		cacheCtrls.remove(beanId);
	}
	
	public static void removeCache() {
		cacheCtrls.clear();
	}
	 

	public static Object getBean(String beanName) {
		return applicationContext.getBean(beanName);
	}

	public static <T> T getBean(String beanName, Class<T> clazz) {
		return applicationContext.getBean(beanName, clazz);
	}

	public static void setContext(ApplicationContext applicationContext) {
		SpringTools.applicationContext = applicationContext;
	}
}
