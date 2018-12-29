package cn.com.yitong.framework.filter;

import cn.com.yitong.common.utils.ConfigUtils;
import cn.com.yitong.common.utils.JsonUtils;
import cn.com.yitong.common.utils.SpringContextUtils;
import cn.com.yitong.common.utils.StringUtils;
import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.consts.NS;
import cn.com.yitong.core.session.Session;
import cn.com.yitong.core.session.timeout.SessionTimeOutMonitorManager;
import cn.com.yitong.core.util.SecurityUtils;
import cn.com.yitong.core.util.ThreadContext;
import cn.com.yitong.core.session.service.SessionService;
import cn.com.yitong.tools.vo.SimpleResult;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.util.ResourceUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

public class SessionFilter implements Filter {

	private Logger logger = YTLog.getLogger(this.getClass());
	private Pattern[] regPatterns = new Pattern[0];

	/**
	 * 会话服务
	 */
	private SessionService sessionService = SpringContextUtils.getBean(SessionService.class);

	@Override
	public void init(FilterConfig config) throws ServletException {
		String exclude = config.getInitParameter("exclude");
		if (logger.isDebugEnabled()) {
			logger.debug("session filter exclude: \n" + exclude);
		}
		if (StringUtil.isNotEmpty(exclude)) {
			String[] patterns = exclude.trim().split(",|;");
			regPatterns = new Pattern[patterns.length];
			for (int i = 0; i < patterns.length; i++) {
				if (StringUtil.isNotEmpty(patterns[i])) {
					regPatterns[i] = Pattern.compile(patterns[i].trim());
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("session filter pattern size:"
						+ regPatterns.length);
			}
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession session = req.getSession();

		String url = req.getRequestURL().toString();

		if (logger.isInfoEnabled()) {
			logger.info("access url :" + url + ", sessionid:" + session.getId());
		}

		//检查JS注入
		if (!checkJsScript(req)) {
			return;
		}

		// 开发调试模式，不做session验证
		if ("1".equals(ConfigUtils.getValue("debug_model"))) {
			/*try {
				File file = ResourceUtils.getFile("classpath:META-INF/debug/data.json");
				String jsonString = FileUtils.readFileToString(file, "utf-8");
				Map<Object, Object> jsonToMap = JsonUtils.jsonToMap(jsonString);
				Set<Entry<Object, Object>> entrySet = jsonToMap.entrySet();
				for (Entry<Object, Object> entry : entrySet) {
					session.setAttribute((String)entry.getKey(), entry.getValue());
				}
			} catch (Exception e) {
				logger.warn(e.getMessage());
				e.printStackTrace();
			}*/
			filterChain.doFilter(request, response);
			return;
		}

		//验证当前服务状态
		SimpleResult result = sessionService.preRequestCheck(url);
		String sessionId = session.getId();
		if(!result.isSeccess()) {
			SessionTimeOutMonitorManager.doHandler(result, sessionId);
			returnErrMsg(response, "{\"STATUS\":\"005\",\"MSG\":\"" + result.getMsg() + "\", \"RESULT\":\"" + result.getResult() + "\"}");
			return ;
		}

		//验证消息重发
		/*result = sessionService.preRequestCheckMsgId(sessionId, SecurityUtils.getRequestMessageId(), req);
		if(!result.isSeccess()) {
			SessionTimeOutMonitorManager.doHandler(result, sessionId);
			returnErrMsg(response, "{\"STATUS\":\"005\",\"MSG\":\"" + result.getMsg() + "\", \"RESULT\":\"" + result.getResult() + "\"}");
			return ;
		}*/

		// Ares Session检查
		String requestSessionId = SecurityUtils.formatToken(ThreadContext.getHttpRequest().getRequestedSessionId());
		if(!checkAresSession()) {
			result = sessionService.checkSessionStatus(requestSessionId, false);
			SessionTimeOutMonitorManager.doHandler(result, requestSessionId);
			returnErrMsg(response, "{\"STATUS\":\"005\",\"MSG\":\"" + result.getMsg() + "\", \"RESULT\":\"" + result.getResult() + "\"}");
			return;
		}

		//检查用户会话是否超时
		if (checkUserSession(request, session)) {
			filterChain.doFilter(request, response);
			return;
		}

		for (Pattern pattern : regPatterns) {
			if (checkUrlPattern(url, pattern)) {
				if (logger.isDebugEnabled()) {
					logger.debug("skip session filter!");
				}
				filterChain.doFilter(request, response);
				return;
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("AJAX session error!");
		}
		//检查当前SESSION 状态
		result = sessionService.checkSessionStatus(requestSessionId, true);
		if(null != result) {
			SessionTimeOutMonitorManager.doHandler(result, requestSessionId);
			returnErrMsg(response, "{\"STATUS\":\"005\",\"MSG\":\"" + result.getMsg() + "\", \"RESULT\":\"" + result.getResult() + "\"}");
		}else {
			SessionTimeOutMonitorManager.doHandler(new SimpleResult(false, "SessionTimeout", "005"), sessionId);
			returnErrMsg(response, "{\"STATUS\":\"005\",\"MSG\":\"SessionTimeout!\"}");
		}
	}

	/**
	 * 查检Ares Session信息完整性，验证是否超时
	 * @return
	 */
	private boolean checkAresSession() {
		if(SecurityUtils.canCodec() && 1 == SecurityUtils.getCurrentCodecType()) {
			Session session = SecurityUtils.getSessionRequired();
			if(SecurityUtils.isLogining()) {
				return true;
			} else if(ThreadContext.getHttpRequest().isCodec() && StringUtils.isEmpty(session.getSkey())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 返回处理
	 * @param response
	 * @param errMsg
	 * @throws IOException
	 */
	private void returnErrMsg(ServletResponse response, String errMsg) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.write(errMsg);
		IOUtils.closeQuietly(out);
	}

	/**
	 * 检查JS注入
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean checkJsScript(HttpServletRequest request) {
		// 请求原文
		Map<String, String[]> params = request.getParameterMap();
		if (null == params || params.isEmpty()) {
			return true;
		}
		StringBuffer bf = new StringBuffer();
		for (String key : params.keySet()) {
			bf.append(key);
			Object value = params.get(key);
			if (value instanceof String[]) {
				String[] values = (String[]) value;
				for (String text : values) {
					bf.append(text);
				}
			} else {
				bf.append(value);
			}
		}
		// 检查是否含有JS脚本或可疑字符
		/*String url = bf.toString();
		if (url.contains("<") || url.contains("(") || url.contains("\"")) {
			logger.warn("---req warn chars---" + url);
			return false;
		}*/
		return true;
	}

	/**
	 * 会话检查细则
	 * 
	 * @param session
	 * @return
	 */
	private boolean checkUserSession(ServletRequest hq, HttpSession session) {
		String isLogin = (String) session.getAttribute(NS.ISLOGIN);
		if (!AppConstants.TRUE.equals(isLogin)) {
			if (logger.isDebugEnabled()) {
				logger.debug("session logout!");
			}
			return false;
		}
		return true;
	}

	/**
	 * url正则匹配检查
	 * @param url
	 * @param pattern
	 * @return
	 */
	private boolean checkUrlPattern(final String url, Pattern pattern) {
		return pattern.matcher(url).find();
	}

	/**
	 * 显示请求内容
	 * 
	 * @param request
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	private void showHeadValues(HttpServletRequest request) {
		Enumeration e = request.getHeaderNames();
		while (e.hasMoreElements()) {
			String a = (String) e.nextElement();
			logger.debug(a + "=" + request.getHeader(a));
		}
	}

	@Override
	public void destroy() {
		regPatterns = null;
		logger = null;
	}
}
