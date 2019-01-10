package cn.com.yitong.ares.core;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

/**
 * 系统启动
 * 
 * @author yaoym
 * 
 */
public class SpringContextLoaderListener extends org.springframework.web.context.ContextLoaderListener {
	// private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public void contextInitialized(ServletContextEvent event) {
		ServletContext ctx = event.getServletContext();
		super.contextInitialized(event);
		// 系统加载
		aresInitial(ctx);
	}

	/**
	 * 加载Ares平台的静态基础对象
	 * 
	 * @param servletContext
	 */
	private void aresInitial(ServletContext servletContext) {
		// 初始化
		AresApp.getInstance().init(this);

	}
}