package cn.com.yitong.core.base;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.consts.SessConsts;
import cn.com.yitong.framework.base.IBusinessContext;

/**
 * 网络方面的工具类
 * 
 * @author lc3@yitong.com.cn
 * 
 */
public class WebUtils {


	private static final Logger logger = LoggerFactory
			.getLogger(WebUtils.class);
	private static final ObjectMapper jsonMapper = new ObjectMapper();
	private static final String RTN_CODE = "STATUS";
	private static final String RTN_MSG = "MSG";

	/**
	 * 通用返回json形式的异常处理类
	 * 
	 * @param e
	 *            异常
	 * @return
	 */
	public static void jsonExceptionHandler(HttpServletResponse response,
			Exception e) {
		Map<String, Object> rtn = returnErrorMsg(null, null, e);
		response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
		response.setContentType("application/json;charset=UTF-8");
		try {
			response.getWriter().print(jsonMapper.writeValueAsString(rtn));
		} catch (IOException e1) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 通用返回错误页面形式的异常处理类
	 * 
	 * @param request
	 * @param response
	 * @param e
	 */
	public static void htmlExceptionHandler(HttpServletRequest request,
			HttpServletResponse response, Exception e) {
		try {
			org.springframework.web.util.WebUtils.exposeErrorRequestAttributes(
					request, e, "mbank");
			request.getRequestDispatcher("/WEB-INF/page/error/500.jsp")
					.forward(request, response);
		} catch (Exception ex) {
			logger.error("跳转500网页出错", ex);
		}
	}

	/**
	 * 返回错误信息
	 * 
	 * @param rtn
	 *            已有的返回参数，为空会自动创建
	 * @param errorMsg
	 *            错误信息
	 * @return
	 */
	public static Map<String, Object> returnErrorMsg(Map<String, Object> rtn,
			String errorMsg) {
		return returnErrorMsg(rtn, errorMsg, null);
	}

	/**
	 * 返回成功信息
	 * 
	 * @param rtn
	 * @param msg
	 * @return
	 */
	public static Map<String, Object> returnSuccessMsg(Map<String, Object> rtn,
			String msg) {
		if (null == rtn) {
			rtn = new HashMap<String, Object>(2);
		}
		rtn.put(RTN_CODE, AppConstants.STATUS_OK);
		rtn.put(RTN_MSG, msg);
		return rtn;
	}

	/**
	 * 返回错误信息
	 * 
	 * @param rtn
	 *            已有的返回参数，为空会自动创建
	 * @param errorMsg
	 *            错误信息
	 * @param e
	 *            异常信息
	 * @return
	 */
	public static Map<String, Object> returnErrorMsg(Map<String, Object> rtn,
			String errorMsg, Throwable e) {
		if (null == rtn) {
			rtn = new HashMap<String, Object>(2);
		}
		rtn.put(RTN_CODE, AppConstants.STATUS_FAIL);
		if (null != e) {
			logger.error(errorMsg, e);
			if (null == errorMsg) {
				errorMsg = "异常信息为：" + e.getMessage();
			}
		} else if (null != errorMsg) {
			logger.info(errorMsg);
		}
		rtn.put(RTN_MSG, errorMsg);
		return rtn;
	}

	/**
	 * 判断是否为ajax请求
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isAjaxRequest(HttpServletRequest request) {
		Assert.notNull(request);
		return null != request.getHeader("x-requested-with");
	}

	/**
	 * 得到request的参数Map，并自动转换json参数
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, String[]> getParamMap(HttpServletRequest request) {
		Map<String, String[]> paramMap = request.getParameterMap();
		Map<String, String[]> rtnMap = new HashMap<String, String[]>();
		for (String key : paramMap.keySet()) {
			if (key.trim().startsWith("{")) {
				try {
					@SuppressWarnings("unchecked")
					Map<Object, Object> map = jsonMapper.readValue(key,
							Map.class);
					for (Entry<Object, Object> entry : map.entrySet()) {
						String eKey = String.valueOf(entry.getKey());
						Object eVal = entry.getValue();
						if (eVal instanceof String[]) {
							rtnMap.put(eKey, (String[]) eVal);
						} else if (eVal instanceof Object[]) {
							Object[] objArray = (Object[]) eVal;
							String[] strArray = new String[objArray.length];
							for (int i = 0; i < objArray.length; i++) {
								strArray[i] = null == objArray[i] ? null
										: objArray[i].toString();
							}
							rtnMap.put(eKey, (String[]) eVal);
						} else {
							rtnMap.put(eKey, new String[] { null == eVal ? null
									: eVal.toString() });
						}
					}
				} catch (Exception e) {
					logger.error("转换参数失败：{}", key, e);
				}

			} else {
				rtnMap.put(key, paramMap.get(key));
			}
		}
		return rtnMap;
	}

	/**
	 * 得到参数值
	 * 
	 * @param parameterMap
	 * @param key
	 * @return
	 */
	public static String getParamVal2ParameterMap(
			Map<String, String[]> parameterMap, String key) {
		if (null == parameterMap) {
			return null;
		}
		String[] val = parameterMap.get(key);
		return (null == val || val.length == 0) ? null : val[0];
	}

	/**
	 * 获取当前UserId
	 * 
	 * @param req
	 * @param ctx
	 * @return
	 */
	public static String getCurrentUserId(HttpServletRequest req,
			IBusinessContext ctx) {
		if (null == req) {
			return null;
		}
		String userId = (String) req.getSession().getAttribute(
				SessConsts.LOGIN_ID);
		if (!StringUtils.hasText(userId) && null != ctx) {
			userId = ctx.getParam("LOGIN_ID");
		}
		if (!StringUtils.hasText(userId)) {
			return null;
		}
		return userId;
	}

	/**
	 * @param request
	 * @return  下载文件的Url前半部分
	 */
	public static String getDownloadUrl(HttpServletRequest request) {
		String serverAddress = AppConstants.server_address;
		String downloadUrl = "/" + serverAddress;
		return downloadUrl;
	}

	/**
	 * 读配置文件信息
	 * 
	 * @param filePath
	 * @return Properties
	 */
	public Properties getProperties(String filePath) {
		InputStream inputStream = this.getClass().getClassLoader()
				.getResourceAsStream(filePath);
		Properties properties = new Properties();
		try {
			properties.load(inputStream);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return properties;
	}
}
