package cn.com.yitong.ares.jcl;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import cn.com.yitong.modules.SpringTools;

@Service("jclManager")
public class JclManagerImpl implements JclManager {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void registerBeanWithXmlAndAnnotion(String jarPath,
			String jarSpringXmlPath, String... beanIds) {
		long time = System.currentTimeMillis();
		Assert.notNull(jarPath);
		// 获取spring上下文
		ApplicationContext applicationContext = SpringTools
				.getApplicationContext();

		DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext
				.getAutowireCapableBeanFactory();
		ClassLoader beanClassLoader = beanFactory.getBeanClassLoader();
		try {
			// 注册注解定义的bean
			URL jarUrl = new File(jarPath).toURI().toURL();
			URL[] urls = new URL[] { jarUrl };
			URLClassLoader cl = new URLClassLoader(urls, beanClassLoader);
			Thread.currentThread().setContextClassLoader(cl);

			// 注册xml定义的bean
			if (StringUtils.hasText(jarSpringXmlPath)) {

				URL url = new URL(jarSpringXmlPath);
				UrlResource urlResource = new UrlResource(url);
				DefaultListableBeanFactory factory = new DefaultListableBeanFactory();

				XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(
						factory);

				int loadBeanDefinitions = reader
						.loadBeanDefinitions(new EncodedResource(urlResource));
				logger.info("============loadBeanDefinitions==========={}",
						loadBeanDefinitions);
				BeanDefinitionRegistry registry = reader.getRegistry();
				String[] beanDefinitionNames = registry
						.getBeanDefinitionNames();
				for (String beanId : beanDefinitionNames) {
					BeanDefinition beanDefinition = registry
							.getBeanDefinition(beanId);
					beanFactory.registerBeanDefinition(beanId, beanDefinition);
				}
				SpringTools.setContext(applicationContext);
				// 以下这行设置BeanFactory的ClassLoader，以加载外部类
				beanFactory.setBeanClassLoader(cl);

			}
			if (null != beanIds) {
				for (String beanId : beanIds) {
					SpringTools.getBean(beanId);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		long time1 = System.currentTimeMillis() - time;
		logger.info("耗时：{}", time1);
	}

	@Override
	public void destoryBeanWithXmlAndAnnotion(String jarPath,
			String jarSpringXmlPath, String... beanIds) {
		// 获取spring上下文
		ApplicationContext applicationContextParent = SpringTools
				.getApplicationContext();
		DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContextParent
				.getAutowireCapableBeanFactory();
		try {
			// 扫描注解定义的bean
			// 扫描xml定义的bean
			if (StringUtils.hasText(jarSpringXmlPath)) {
				URL url = new URL(jarSpringXmlPath);
				UrlResource urlResource = new UrlResource(url);
				DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
				XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(
						factory);
				reader.loadBeanDefinitions(new EncodedResource(urlResource));
				BeanDefinitionRegistry registry = reader.getRegistry();
				String[] beanDefinitionNames = registry
						.getBeanDefinitionNames();
				for (String beanId : beanDefinitionNames) {
					if (applicationContextParent.containsBean(beanId)) {
						DefaultListableBeanFactory beanFactory1 = (DefaultListableBeanFactory) applicationContextParent
								.getAutowireCapableBeanFactory();
						if (beanFactory1.containsBean(beanId)) {
							if (beanId.startsWith("org.spring")) {
								continue;
							}
							logger.warn("remove beanId :{}", beanId);
							beanFactory1.removeBeanDefinition(beanId);
						}
					}
				}
			}
			
			SpringTools.setContext(applicationContextParent);

			if (null != beanIds) {
				for (String beanId : beanIds) {
					SpringTools.removeCache(beanId);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}