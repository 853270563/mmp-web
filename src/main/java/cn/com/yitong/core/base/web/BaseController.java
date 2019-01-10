package cn.com.yitong.core.base.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.yitong.core.base.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 
 * @author lc3@yitong.com.cn
 *
 */
public class BaseController {
	
	/**
	 * 日志
	 */
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	
	@ExceptionHandler({Exception.class})
	public void exceptionHandler(HttpServletRequest request, HttpServletResponse response,
			Exception e) {
		WebUtils.jsonExceptionHandler(response, e);
	}

}
