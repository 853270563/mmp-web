package cn.com.yitong.framework.filter;

import cn.com.yitong.consts.Properties;
import cn.com.yitong.util.YTLog;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 应用版本更新控制
 * 
 * @author sunwei
 *
 */
public class AppUpdateControlFilter implements Filter {
	
	private static final List<Long> cusList = new ArrayList<Long>();

	private Logger logger = YTLog.getLogger(this.getClass());

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		// 次数为0的场合，默认不控制
		int limit = Properties.getInt("update_query_limit") ;
		if (limit == 0) {
			chain.doFilter(request, response);
			return;
		}
		// 当前时间
		long currentTimeMillis = System.currentTimeMillis();
		// 一分钟前
		long minuteBefore = currentTimeMillis - 1000 * 60L;
		// 删除一分钟前的记录
		while (!cusList.isEmpty()) {
			Long cusTime = cusList.get(0);
			if (cusTime < minuteBefore) {
				cusList.remove(0);
			} else {
				break;
			}
		}
		// 一分钟之内有超过配置次数的版本更新查询请求，则不进行处理
		if (cusList.size() > limit - 1) {
			returnMsg(response, "{\"MSG\":\"交易成功!\",\"STATUS\":\"1\"}");
			
			HttpServletRequest req = (HttpServletRequest) request;
			String url = req.getRequestURL().toString();
			if (logger.isDebugEnabled()) {
				logger.debug("access url :\t" + url);
				logger.debug("======一分钟版本更新查询的请求总次数已超过限制次数，本次请求被忽略。");
			}
			return;
		}
		cusList.add(currentTimeMillis);
		chain.doFilter(request, response);
	}

	private void returnMsg(ServletResponse response, String msg)
			throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.write(msg);
		out.flush();
	}

	@Override
	public void destroy() {

	}

}
