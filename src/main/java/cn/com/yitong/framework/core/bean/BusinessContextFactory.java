package cn.com.yitong.framework.core.bean;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import cn.com.yitong.framework.base.IBusinessContext;

@Component
public class BusinessContextFactory {

	public IBusinessContext createBusiContext(HttpServletRequest request) {
		return new BusinessContext(request);
	}

}
