package cn.com.yitong.framework.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.MDC;

import cn.com.yitong.util.StringUtil;

public class Log4jFilter implements Filter{

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		// HttpSession session = req.getSession();
		// String userNo = (String) session.getAttribute(SessConsts.USER_NO);
		String uri = req.getRequestURI();
		String uuid = StringUtil.uuid();
		
		MDC.put("uri", this.nullHandler(uri));
		MDC.put("uuid", this.nullHandler(uuid));
		chain.doFilter(request, response);
		
		MDC.remove("uri");
		MDC.remove("uuid");
	}
	
	private String nullHandler(String value) {
		if (value == null) {
			return "";
		}
		return value;
	}

	@Override
	public void destroy() {
		
	}
}
