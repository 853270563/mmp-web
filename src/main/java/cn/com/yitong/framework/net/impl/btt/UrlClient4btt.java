package cn.com.yitong.framework.net.impl.btt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.json.JSONException;
import org.springframework.stereotype.Component;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.consts.SessConsts;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.servlet.ServerInit;
import cn.com.yitong.util.CharsetUtil;
import cn.com.yitong.util.JsonXml;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

@Component
public class UrlClient4btt implements IClientFactory {

	private Logger logger = YTLog.getLogger(this.getClass());

	private String serverURL = ServerInit.getString("CORE_SERVER_URL");
	private final static int bttTimeOut = 75000; // 网银连接btt超时时间

	private List<String> cookies;

	@Override
	public boolean execute(IBusinessContext busiCtx, String transCode) {
		// 测试请求报文生成
		Element rqctx = busiCtx.getRequestContext(transCode);
		String xmlStr = StringUtil.formatXmlStr(rqctx.asXML());
		if (logger.isDebugEnabled()) {
			logger.debug(transCode + " send xml msg: \n" + xmlStr);
		}
		// 繁体中文检查,大丰核心不支持繁体中文
		if (!CharsetUtil.isCnHKString(xmlStr)) {
			if (logger.isDebugEnabled()) {
				logger.debug(transCode + " 该交易只支持繁体中文!");
			}
			busiCtx.setErrorInfo(AppConstants.STATUS_FAIL, "该交易只支持繁体中文!",
					transCode);
			return false;
		}

		String sid = busiCtx.getSessionText(SessConsts.SESS_SEQ);
		String sendData = JsonXml.xmlStrToJsonStr(xmlStr);

		HttpURLConnection httpConn = null;
		PrintWriter out = null;
		BufferedReader in = null;
		String responseStr = null;
		try {
			// BTT采用长连接
			if (!checkSession(busiCtx, transCode)) {
				createBttSession(busiCtx, sid, transCode);
				if (cookies == null || cookies.isEmpty()) {
					if (logger.isDebugEnabled()) {
						logger.debug(transCode + " btt server create seession is error!");
					}
					busiCtx.setErrorInfo(AppConstants.STATUS_FAIL, "BTT会话创建异常!", transCode);
					return false;
				}
			}
			String url = serverURL + "/AjaxRequest";
			if (logger.isDebugEnabled()) {
				logger.debug(transCode + " server url:\t" + url);
			}
			URL urlClient = new URL(url);
			httpConn = (HttpURLConnection) urlClient.openConnection();
			setHttpConnection(httpConn);
			httpConn.setRequestProperty("Cookie", cookies.get(0));
			showHttpRequestHeaders(httpConn);
			if (logger.isDebugEnabled()) {
				logger.debug(transCode + " send json msg: \n"
						+ StringUtil.replacePwd(sendData));
			}
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(httpConn.getOutputStream());
			// 发送请求参数
			out.print(sendData);
			out.flush();

			in = new BufferedReader(new InputStreamReader(
					httpConn.getInputStream(), "UTF-8"));
			StringBuffer sb = new StringBuffer();
			String line;
			boolean firstLine = true;
			while ((line = in.readLine()) != null) {
				if (firstLine) {
					firstLine = false;
				} else {
					sb.append("\n");
				}
				sb.append(line);
			}
			responseStr = sb.toString();
			showHttpResponseHeaders(httpConn);
			if (StringUtil.isEmpty(responseStr) && logger.isInfoEnabled()) {
				logger.error(transCode
						+ " btt server response msg length is 0!");
				responseStr = "{'result':'" + transCode
						+ " is error!','error_code':'ERROR'}";
				return false;
			}
			if (logger.isDebugEnabled()) {
				logger.debug(transCode + " old receive data: \n" + responseStr);
			}
			boolean needCovert = responseStr.contains("=");
			if (needCovert) {
				responseStr = responseStr.replaceAll("=", ":\"");
				responseStr = responseStr.replaceAll(",", "\",");
				responseStr = responseStr.replaceAll("\\}", "\"}");
				responseStr = responseStr.replaceAll("\\]\\\"", "]");
				responseStr = responseStr.replaceAll("\\\"\\[", "[");
				responseStr = responseStr.replaceAll("\\}\\\"", "}");
				responseStr = responseStr.replaceAll("\\\"\\{", "{");
				responseStr = responseStr.replaceAll("\\\"null\\\"", "\"\"");
			}
			if (logger.isDebugEnabled()) {
				logger.debug(transCode + " new receive data: \n" + responseStr);
			}
			// JSON to XML
			if (responseStr.startsWith(AppConstants.JSON_START_WITH)) {
				String xmlRsp = JsonXml.jsonStrToXmlStr(responseStr);
				busiCtx.setResponseEntry(xmlRsp);
				return true;
			} else {
				// 清除BTT会话信息
				logger.warn(transCode
						+ " btt session is time out, need create again: \n"
						+ responseStr);
				busiCtx.setErrorInfo(AppConstants.STATUS_FAIL, "BTT反馈异常!",
						transCode);
				return false;
			}
		} catch (MalformedURLException e) {
			logger.error(transCode + " btt url is error", e);
			busiCtx.setErrorInfo(AppConstants.STATUS_FAIL, "请求URL异常!",
					transCode);
		} catch (IOException e) {
			logger.error(transCode + " btt io is error", e);
			busiCtx.setErrorInfo(AppConstants.STATUS_FAIL, "交易超時", transCode);
		} catch (JSONException e) {
			logger.error(transCode + " btt json is error", e);
			busiCtx.setErrorInfo(AppConstants.STATUS_FAIL, "请求数据异常", transCode);
		} finally {
			if (out != null) {
				out.close();
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			try {
				// releaseSession(null, busiCtx);
			} catch (Exception e) {
			}
		}
		// urlCookieCaches.removeCookie(sid);
		busiCtx.setErrorInfo(AppConstants.STATUS_FAIL, "系统异常!", transCode);
		return false;
	}

	/**
	 * 测试请求头
	 * 
	 * @param httpConn
	 */
	private void showHttpResponseHeaders(HttpURLConnection httpConn) {
		if (logger.isDebugEnabled()) {
			Map<String, List<String>> map = httpConn.getHeaderFields();
			for (String key : map.keySet()) {
				logger.debug("[<<" + key + "=" + map.get(key).toString() + "]");
			}
		}
	}

	/**
	 * 请求头
	 * 
	 * @param httpConn
	 */
	private void showHttpRequestHeaders(HttpURLConnection httpConn) {
		if (logger.isDebugEnabled()) {
			Map<String, List<String>> map = httpConn.getRequestProperties();
			for (String key : map.keySet()) {
				logger.debug("[>>" + key + "=" + map.get(key).toString() + "]");
			}
		}
	}

	/**
	 * 设置请求属性信息
	 * 
	 * @param httpConn
	 * @throws java.net.ProtocolException
	 */
	private void setHttpConnection(HttpURLConnection httpConn)
			throws ProtocolException {
		httpConn.setRequestMethod("POST");
		httpConn.setConnectTimeout(bttTimeOut);
		httpConn.setReadTimeout(bttTimeOut);
		httpConn.setRequestProperty("Connection", "keep-alive");
		httpConn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
		httpConn.setRequestProperty("Accept", "text/mobilexml");
		httpConn.setRequestProperty(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.803.0 Safari/535.1");
		httpConn.setDoInput(true);
		httpConn.setDoOutput(true);
	}

	/**
	 * 检查会话
	 * 
	 * @param busiCtx
	 * @return
	 * @throws org.json.JSONException
	 */
	private boolean checkSession(IBusinessContext busiCtx, String transCode) {
		HttpURLConnection httpConn = null;
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			String url = serverURL + "/AjaxSession";
			if (logger.isDebugEnabled()) {
				logger.debug("server url:\t" + url);
			}
			URL urlClient = new URL(url);
			httpConn = (HttpURLConnection) urlClient.openConnection();
			setHttpConnection(httpConn);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(httpConn.getOutputStream());
			// 发送请求参数
			out.print("{}");
			out.flush();

			logger.info("btt ajax session check input stream .....");
			in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			String responseStr = sb.toString();
			logger.info("session check:" + responseStr);
			if ("00".equals(responseStr)) {
				return true;
			}
		} catch (MalformedURLException e) {
			logger.error("create btt session,http server url error", e);
		} catch (IOException e) {
			logger.error("create btt session,http server io error", e);
		} finally {
			if (out != null) {
				out.close();
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		return false;
	}

	/**
	 * 创建会话
	 * 
	 * @param busiCtx
	 * @return
	 * @throws org.json.JSONException
	 */
	private boolean createBttSession(IBusinessContext busiCtx, String sid,
			String transCode) throws JSONException {
		HttpURLConnection httpConn = null;
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			String url = serverURL + "/AjaxEstablishSession";
			if (logger.isDebugEnabled()) {
				logger.debug("server url:\t" + url);
			}
			URL urlClient = new URL(url);
			httpConn = (HttpURLConnection) urlClient.openConnection();
			httpConn.setRequestMethod("POST");
			httpConn.setConnectTimeout(5000);
			httpConn.setReadTimeout(5000);
			// httpConn.setRequestProperty("Connection", "keep-alive");
			httpConn.setRequestProperty("Content-Type",
					"text/xml; charset=UTF-8");
			httpConn.setRequestProperty("Accept", "text/mobilexml");
			httpConn.setRequestProperty(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.803.0 Safari/535.1");
			httpConn.setDoOutput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(httpConn.getOutputStream());
			// 发送请求参数
			out.print("{}");
			out.flush();

			showHttpResponseHeaders(httpConn);

			Map<String, List<String>> map = httpConn.getHeaderFields();
			cookies = map.get("Set-Cookie");
			if (logger.isDebugEnabled()) {
			}
			return true;
		} catch (MalformedURLException e) {
			logger.error("create btt session,http server url error", e);
		} catch (IOException e) {
			logger.error("create btt session,http server io error", e);
		} finally {
			if (out != null) {
				out.close();
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			try {
				httpConn.disconnect();
			} catch (Exception e) {
			}
		}
		return false;
	}

	@Override
	public void releaseSession(HttpClient httpClient, IBusinessContext busiCtx)
			throws Exception {
		destory();
	}

	@PreDestroy
	protected void destory() {
		if (cookies == null || cookies.isEmpty()) {
			return;
		}
		String url = serverURL + "/AjaxLogout";
		HttpURLConnection httpConn = null;
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("server url:\t" + url);
			}
			URL urlClient = new URL(url);
			httpConn = (HttpURLConnection) urlClient.openConnection();
			// Connection:close 服务器主动关闭连接
			httpConn.setRequestProperty("Connection", "close");
			httpConn.setRequestProperty("Content-Type",
					"text/xml; charset=UTF-8");
			httpConn.setRequestProperty("Accept", "text/mobilexml");
			httpConn.setRequestProperty(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.803.0 Safari/535.1");
			httpConn.setRequestProperty("Cookie", cookies.get(0));
			httpConn.setDoOutput(true);
			httpConn.setDoInput(false);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(httpConn.getOutputStream());
			// 发送请求参数
			out.print("{}");
			out.flush();
		} catch (MalformedURLException e) {
			logger.error("create btt session,http server url error", e);
		} catch (IOException e) {
			logger.error("create btt session,http server io error", e);
		} finally {
			if (out != null) {
				out.close();
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			httpConn.disconnect();
		}
	}

}
