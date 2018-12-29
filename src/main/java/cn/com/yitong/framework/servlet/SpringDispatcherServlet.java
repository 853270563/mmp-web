package cn.com.yitong.framework.servlet;

import cn.com.yitong.util.YTLog;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Spring前端请求处理器代理类 ,可显示日志输出
 * 
 * @author yaoym
 * 
 */
@SuppressWarnings("serial")
public class SpringDispatcherServlet extends DispatcherServlet {

	private Logger logger = YTLog.getLogger(this.getClass());

	protected void doService(HttpServletRequest request,
			HttpServletResponse response) {
		String url = request.getRequestURL().toString();
		if (logger.isInfoEnabled()) {
			logger.info("SpringDispatcherServlet .start :\t" + url);
		}
		try {
			super.doService(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (logger.isInfoEnabled()) {
			logger.info("SpringDispatcherServlet.end :\t" + url);
		}
	}
}
