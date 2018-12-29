package cn.com.yitong.util;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.UrlResource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class JclManagerImpl {

	public void jclTest() {
		System.out.println("JclManager依赖注入........");
	}

	
	public void registerBeanWithXmlAndAnnotion(String jarPath, String jarSpringXmlPackage, String... basePackages) {
		Assert.notNull(jarPath);
		// 获取spring上下文
		ApplicationContext applicationContextParent = SpringContextHolder.getApplicationContext();
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[] { "cn/com/common/sample/resource/spring-jcl.xml" }, applicationContextParent);
		DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
		ClassLoader beanClassLoader = beanFactory.getBeanClassLoader();
		try {
			// 注册注解定义的bean
			URL jarUrl = new File(jarPath).toURI().toURL();
			URL[] urls = new URL[] { jarUrl };
			URLClassLoader cl = new URLClassLoader(urls, beanClassLoader);
			Thread.currentThread().setContextClassLoader(cl);
			AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext();
			annotationConfigApplicationContext.setParent(applicationContext);
			annotationConfigApplicationContext.scan(basePackages);
			annotationConfigApplicationContext.refresh();

			// 注册xml定义的bean
			if (StringUtils.hasText(jarSpringXmlPackage)) {
				String jarSpringXmlPath = "jar:file:/" + jarPath + "!" + jarSpringXmlPackage;
				URL url = new URL(jarSpringXmlPath);
				UrlResource urlResource = new UrlResource(url);
				DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
				XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
				reader.loadBeanDefinitions(urlResource);
				BeanDefinitionRegistry registry = reader.getRegistry();
				String[] beanDefinitionNames = registry.getBeanDefinitionNames();
				for (String beanId : beanDefinitionNames) {
					BeanDefinition beanDefinition = registry.getBeanDefinition(beanId);
					beanFactory.registerBeanDefinition(beanId, beanDefinition);
				}
				SpringContextHolder.setContext(applicationContext);
				// 以下这行设置BeanFactory的ClassLoader，以加载外部类
				beanFactory.setBeanClassLoader(cl);
				// 设置spring上下文环境
				SpringContextHolder.setContext(annotationConfigApplicationContext);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public void destoryBeanWithXmlAndAnnotion(String jarPath, String jarSpringXmlPackage, String... basePackages) {
		// 获取spring上下文
		ApplicationContext applicationContextParent = SpringContextHolder.getApplicationContext();
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[] { "cn/com/common/sample/resource/spring-jcl.xml" }, applicationContextParent);
		DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
		ClassLoader beanClassLoader = beanFactory.getBeanClassLoader();
		try {
			// 扫描注解定义的bean
			URL jarUrl = new File(jarPath).toURI().toURL();
			URL[] urls = new URL[] { jarUrl };
			URLClassLoader cl = new URLClassLoader(urls, beanClassLoader);
			Thread.currentThread().setContextClassLoader(cl);
			AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext();
			annotationConfigApplicationContext.scan(basePackages);
			String[] beanAnnotionDefinitionNames = annotationConfigApplicationContext.getBeanDefinitionNames();
			// 从上下文环境中移除bean
			if (applicationContextParent instanceof AnnotationConfigApplicationContext) {
				AnnotationConfigApplicationContext context = (AnnotationConfigApplicationContext) applicationContextParent;
				BeanDefinitionRegistry beanDefinitionRegistry = (DefaultListableBeanFactory) context.getBeanFactory();
				for (String string : beanAnnotionDefinitionNames) {
					if (beanDefinitionRegistry.containsBeanDefinition(string)) {
						beanDefinitionRegistry.removeBeanDefinition(string);
					}
				}
			}

			// 扫描xml定义的bean
			if (StringUtils.hasText(jarSpringXmlPackage)) {
				String jarSpringXmlPath = "jar:file:/" + jarPath + "!" + jarSpringXmlPackage;
				URL url = new URL(jarSpringXmlPath);
				UrlResource urlResource = new UrlResource(url);
				DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
				XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
				reader.loadBeanDefinitions(urlResource);
				BeanDefinitionRegistry registry = reader.getRegistry();
				String[] beanDefinitionNames = registry.getBeanDefinitionNames();
				for (String beanId : beanDefinitionNames) {
					if (applicationContextParent.containsBean(beanId)) {
						if (applicationContextParent instanceof AnnotationConfigApplicationContext) {
							AnnotationConfigApplicationContext context = (AnnotationConfigApplicationContext) applicationContextParent;
							ApplicationContext parent = context.getParent();
							if (parent instanceof ClassPathXmlApplicationContext) {
								ClassPathXmlApplicationContext pathXmlApplicationContext = (ClassPathXmlApplicationContext) parent;
								BeanDefinitionRegistry beanDefinitionRegistry = (DefaultListableBeanFactory) pathXmlApplicationContext.getBeanFactory();
								if (beanDefinitionRegistry.containsBeanDefinition(beanId)) {
									beanDefinitionRegistry.removeBeanDefinition(beanId);
								}

							}
						}
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
