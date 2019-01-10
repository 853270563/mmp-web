package cn.com.yitong.framework.core.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import cn.com.yitong.common.utils.ConfigUtils;
import cn.com.yitong.common.utils.SpringContextUtils;
import cn.com.yitong.common.utils.StringUtils;
import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.consts.SessConsts;
import cn.com.yitong.core.session.Session;
import cn.com.yitong.core.session.timeout.SessionTimeOutMonitorManager;
import cn.com.yitong.core.util.SecurityUtils;
import cn.com.yitong.core.util.ThreadContext;
import cn.com.yitong.modules.session.service.SessionService;
import cn.com.yitong.tools.vo.SimpleResult;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.UserAgentUtils;
import cn.com.yitong.util.YTLog;

public class SessionFilter implements Filter {

	private Logger logger = YTLog.getLogger(this.getClass());
	private Pattern[] regPatterns = new Pattern[0];
	@SuppressWarnings("unused")
	private final String url_session_timeout = "/WEB-INF/page/error/sessionError.jsp";

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
		String url = req.getRequestURL().toString();

		if (logger.isDebugEnabled()) {
			logger.debug("access url :\t" + url);
			String parameter = req.getHeader("User-Agent");
			logger.debug("User-Agent: " + parameter + " isPC:" + UserAgentUtils.isComputer(req));
		}
		HttpSession session = req.getSession();
		//		if (logger.isDebugEnabled()) {
		// showHeadValues(req);
		//		}
		/*if (!checkJsScript(req)) {
			returnErrMsg(response, "{\"STATUS\":\"0\",\"MSG\":\"  js已经注入 \"");
		}*/
		
		// 开发调试模式，不做session验证
		logger.info("是否为开发模式================》》" + (ConfigUtils.getValue("debug_model").equals("1") ? "是" : "否"));
		if ("1".equals(ConfigUtils.getValue("debug_model"))) {
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

		// Ares Session检查
		String requestSessionId = SecurityUtils.formatToken(ThreadContext.getHttpRequest().getRequestedSessionId());
		if (!checkAresSession()) {
			result = sessionService.checkSessionStatus(requestSessionId, false);
			SessionTimeOutMonitorManager.doHandler(result, requestSessionId);
			returnErrMsg(response, "{\"STATUS\":\"005\",\"MSG\":\"" + result.getMsg() + "\", \"RESULT\":\"" + result.getResult() + "\"}");
			return;
		}
		//验证消息重发
		result = sessionService.preRequestCheckMsgId(sessionId, SecurityUtils.getRequestMessageId(), req);
		if(!result.isSeccess()) {
			SessionTimeOutMonitorManager.doHandler(result, sessionId);
			returnErrMsg(response, "{\"STATUS\":\"005\",\"MSG\":\"" + result.getMsg() + "\", \"RESULT\":\"" + result.getResult() + "\"}");
			return ;
		}

		/**
		 * 已经登录的直接放过
		 */
		if (checkUserSession(request, session)) {
			filterChain.doFilter(request, response);
			return;
		}
		/**
		 * 过滤不需要session验证的请求
		 */
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
	protected boolean checkAresSession() {
		if(SecurityUtils.canCodec()) {
			Session session = SecurityUtils.getSessionRequired();
			if(SecurityUtils.isLogining()) {
				return true;
			} else if(ThreadContext.getHttpRequest().isCodec() && StringUtils.isEmpty(session.getSkey())) {
				return false;
			}
		}
		return true;
	}

	protected void returnErrMsg(ServletResponse response, String errMsg) throws IOException {
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.write(errMsg);
		if(logger.isDebugEnabled()) {
			logger.debug(errMsg);
		}
		out.flush();
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
		String url = bf.toString();
		if (url.contains("<") || url.contains("(") || url.contains("\"")) {
			logger.warn("---req warn chars---" + url);
			return false;
		}
		return true;
	}

	/**
	 * 会话检查细则
	 * 
	 * @param session
	 * @return
	 */
	private boolean checkUserSession(ServletRequest hq, HttpSession session) {
		String isLogin = (String) session.getAttribute(SessConsts.ISLOGIN);
		if (!AppConstants.TRUE.equals(isLogin)) {
			if (logger.isDebugEnabled()) {
				logger.debug("user has not login!");
			}
			return false;
		}
		return true;
	}

	private boolean checkUrlPattern(String url, Pattern pattern) {
		java.util.regex.Matcher m = pattern.matcher(url);
		return m.find();
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

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		String pattenStr = "/index.do";
		Pattern pattern = Pattern.compile(pattenStr);
		System.out.println(" pattern  defined success ");
		String url = "/common/itemlabel.do";

		String url2 = "/index.do";
		java.util.regex.Matcher m = pattern.matcher(url2);
		if (m.find()) {
			System.out.println("success");
		} else {
			System.out.println("failure");
		}
	}

}
