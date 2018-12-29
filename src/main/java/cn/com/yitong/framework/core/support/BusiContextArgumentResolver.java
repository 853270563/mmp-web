package cn.com.yitong.framework.core.support;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IParamCover;
import cn.com.yitong.framework.service.ICommonService;

/**
 * 自定义主线参数解析 Created by wenin819@gmail.com on 2014-05-22.
 */
public class BusiContextArgumentResolver implements WebArgumentResolver {
	@Autowired
	protected ICommonService commonService;
	@Autowired
	@Qualifier("jsonParamCover")
	protected IParamCover jsonParamCover;

	/* 
	 * @see org.springframework.web.bind.support.WebArgumentResolver#resolveArgument(org.springframework.core.MethodParameter, org.springframework.web.context.request.NativeWebRequest)
	 */
	@Override
	public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) throws Exception {
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		String transCode = "";
		RequestMapping requestMapping = methodParameter.getMethodAnnotation(RequestMapping.class);
		String[] requestMappingValues = requestMapping.value();
		String requestMappingUrl = requestMappingValues[0];
		String requestUrl = request.getServletPath();
		requestUrl=requestUrl.substring(1);
		String[] pathVariables = getPathVariables(requestMappingUrl, requestUrl);
		if (pathVariables != null && pathVariables.length > 0) {
			transCode = pathVariables[0];
		}
		if (methodParameter.getParameterType().equals(IBusinessContext.class)) {
			// 初始化数据总线
			IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
			commonService.generyTransLogSeq(ctx, transCode);
			// 加载参数
			ctx.initParamCover(jsonParamCover, transCode, false);
			return ctx;
		}
		return UNRESOLVED;
	}

	public static String[] getPathVariables(String requestMappingUrl, String requestUrl) {
		String[] pathVariables = null;
		String[] requestMappingUrls = requestMappingUrl.split("\\{");
		for (int i = 0; i < requestMappingUrls.length; i++) {
			if (requestMappingUrls[i].contains("}")) {
				requestMappingUrls[i] = requestMappingUrls[i].replaceAll(".*}", "");
			}
			requestUrl = requestUrl.replaceFirst(requestMappingUrls[i], "%x_x%");
		}
		requestUrl = requestUrl.replaceFirst("%x_x%", "");
		pathVariables = requestUrl.split("%x_x%");
		return pathVariables;
	}

}
