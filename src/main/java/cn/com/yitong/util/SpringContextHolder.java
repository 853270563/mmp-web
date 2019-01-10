package cn.com.yitong.util;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import cn.com.yitong.framework.servlet.ServerInit;

/**
 * 以静态变量保存Spring ApplicationContext, 可在任何代码任何地方任何时候中取出ApplicaitonContext.
 * 
 */
@Service
public class SpringContextHolder implements ApplicationContextAware,
		DisposableBean {

	private static ApplicationContext applicationContext = null;

	private static Logger logger = Logger.getLogger(SpringContextHolder.class);
/*
	static {
		File file = new File("log");
		if (!file.isDirectory()) {
			file.mkdir();
		}
		init();
	}*/

	/**
	 * 加载spring上下文
	 */
	public static void init() {
		new ClassPathXmlApplicationContext(new String[] {
				"classpath*:applicationContext.xml",
				"classpath*:applicationContext-ibatis.xml" });
	}

	/**
	 * 实现ApplicationContextAware接口, 注入Context到静态变量中.
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {
		logger.debug("注入ApplicationContext到SpringContextHolder:"
				+ applicationContext);
		if (SpringContextHolder.applicationContext != null) {
			logger
					.warn("SpringContextHolder中的ApplicationContext被覆盖,原有Context为:"
							+ SpringContextHolder.applicationContext);
		}
		SpringContextHolder.applicationContext = applicationContext; // NOSONAR
	}

	/**
	 * 实现DisposableBean接口,在Context关闭时清理静态变量.
	 */
	public void destroy() throws Exception {
		SpringContextHolder.cleanApplicationContext();

	}

	/**
	 * 取得存储在静态变量中的ApplicationContext.
	 */
	public static ApplicationContext getApplicationContext() {
		checkApplicationContext();
		return applicationContext;
	}
	
	/**
	 * 动态改变context， 热部署
	 * @author ZhangPeiZhong 20140926
	 */
	
	public static void setContext(ApplicationContext context)
	{
		applicationContext = context;

		//ServerInit.springCtx = (WebApplicationContext)applicationContext;
	}

	/**
	 * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		checkApplicationContext();
		return (T) applicationContext.getBean(name);
	}

	/**
	 * 清除applicationContext静态变量.
	 */
	public static void cleanApplicationContext() {
		logger.debug("清除SpringContextHolder中的ApplicationContext:"
				+ applicationContext);
		applicationContext = null;
	}

	/**
	 * 检查ApplicationContext不为空.
	 */
	private static void checkApplicationContext() {
		if (applicationContext == null) {
			throw new IllegalStateException(
					"applicaitonContext未注入,请在applicationContext.xml中定义SpringContextHolder");
		}
	}

}
