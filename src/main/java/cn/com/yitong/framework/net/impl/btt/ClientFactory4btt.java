package cn.com.yitong.framework.net.impl.btt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.json.JSONObject;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IHttpClientCaches;
import cn.com.yitong.framework.servlet.ServerInit;
import cn.com.yitong.util.JsonXml;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

public class ClientFactory4btt implements IClientFactory {
	// public static String Mobile_Bank_btt_URL = "";
	private Logger logger = YTLog.getLogger(this.getClass());

	private IHttpClientCaches httpClientCaches;

	private String serverURL = "-1";
	private String serverIP4btt;

	private int connTimeout = 6000;
	private int readTimeout = 60000;

	// 主机可保持连接的连接数
	private int connectionsPerHost = 200;
	// ConnectionPool中可最多保持的连接数
	private int maxTotalConnections = 1000;

	private String connTimeoutStr = "4000";
	private String readTimeoutStr = "60000";
	private String connectionsPerHostStr = "200";
	private String maxTotalConnectionsStr = "2000";

	// httpclient 多线程管理器
	public static ThreadSafeClientConnManager cm = null;

	private boolean isInited = false;
	private boolean debug = true;

	/**
	 * HttpClient 初始化方法
	 */
	public ClientFactory4btt() {
		// 判断是否取到交易url
		// 初始化BTT Server url地址
		String scheme = null;
		int port = 0;

		serverURL = ServerInit.getString("CORE_SERVER_URL");
		// 服务器ServerName
		// ServerURL=ServerURL.replace("mobcli",
		// System.getProperty(CMBC.WeblogicName));
		logger.debug("交易URL静态保存值 btt url:" + serverURL);

		scheme = serverURL.substring(0, serverURL.indexOf(":"));
		String tempstr = serverURL.substring(serverURL.indexOf("//") + 2);
		serverIP4btt = tempstr.substring(0, tempstr.indexOf(":"));
		String portstr = tempstr.substring(tempstr.indexOf(":") + 1,
				tempstr.indexOf("/"));
		port = StringUtil.string2Int(portstr);
		// 协议注册
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme(scheme, port, PlainSocketFactory
				.getSocketFactory()));

		cm.setMaxTotal(maxTotalConnections);
		cm.setDefaultMaxPerRoute(connectionsPerHost);
		HttpParams params = new BasicHttpParams();

		// 超时时间 设置
		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
				connTimeout);
		params.setParameter(CoreConnectionPNames.SO_TIMEOUT, readTimeout);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

		// 设置连接上限
		HttpRoute router = new HttpRoute(new HttpHost(scheme + "://"
				+ serverIP4btt, port));
		cm.setMaxForRoute(router, maxTotalConnections);
	}

	public boolean execute(IBusinessContext busiCtx, String transCode) {

		// 测试请求报文生成
		Element rqctx = busiCtx.getRequestContext(transCode);
		String url = serverURL + "/AjaxRequest";
		logger.info("server url:\t" + url);
		HttpPost post = new HttpPost(url);

		String xmlStr = StringUtil.formatXmlStr(rqctx.asXML());
		logger.info("send msg: \n" + xmlStr);
		// XML to JSON
		String sendData = JsonXml.xmlStrToJsonStr(xmlStr);
		logger.debug("send msg: \n" + StringUtil.replacePwd(sendData));

		long bttstarttime = System.currentTimeMillis();

		String responseStr = "";
		HttpEntity entity = null;
		InputStream in = null;
		BufferedReader br = null;
		HttpClient httpClient = null;
		boolean isloginTrans = false;
		try {
			// 从会话中取值
			String sid = busiCtx.getHttpSession().getId();
			// 初始化httpClient对象
			if (!debug && httpClientCaches.hasHttpClient(sid)) {
				httpClient = httpClientCaches.getClientBySid(sid);
			} else {
				httpClient = createHttpClient(busiCtx);
				if (!debug) {
					httpClientCaches.putHttpClient(sid, httpClient);
				}
			}
			// 设置并进行保存 httclient
			// busiCtx.setHttpClient(httpClient);
			// 设置通讯头
			setHttpHeader(post);
			StringEntity stringEntry = new StringEntity(sendData, "text/xml",
					"UTF-8");
			post.setEntity(stringEntry);

			HttpResponse rsp = httpClient.execute(post);
			if (rsp != null) {
				logger.debug("Response status code: " + rsp.getStatusLine());
				if (rsp.getStatusLine().getStatusCode() != 200) {
					logger.debug("request btt url:\t" + serverURL);
					busiCtx.setErrorInfo(AppConstants.STATUS_FAIL,
							"BTT回复数据状态错误!错误状态号:[" + rsp.getStatusLine() + "]",
							transCode);
					return false;
				}
				logger.debug("btt interface response time:"
						+ (System.currentTimeMillis() - bttstarttime));
				entity = rsp.getEntity();

				// 解析读取数据流
				entity = new BufferedHttpEntity(entity);
				in = entity.getContent();

				StringBuffer sb = new StringBuffer();
				in = entity.getContent();
				br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
				String tempStr;
				boolean firstLine = true;
				while ((tempStr = br.readLine()) != null) {
					if (firstLine) {
						firstLine = false;
					} else {
						sb.append("\n");
					}
					sb.append(tempStr);
				}
				responseStr = sb.toString();
				if (null == responseStr || responseStr.trim().length() == 0) {
					logger.error("the infocast server response msg length is 0!");
					responseStr = "{'result':'" + transCode
							+ " is error!','error_code':'ERROR'}";
				}
				// JSON to XML
				String xmlRsp = JsonXml.jsonStrToXmlStr(responseStr);
				busiCtx.setResponseEntry(xmlRsp);
				// 设置通讯成功
				// rst.setStatus(AppConstants.MSG_STATUS_OK);
				logger.info("receive data: \n" + responseStr); 
				return true;
			} else {
				logger.debug("btt interface response time:timeout!");
				busiCtx.setErrorInfo(AppConstants.STATUS_FAIL,
						"btt interface response status: timeout!!", transCode);
			}
		} catch (SocketTimeoutException ste) {
			logger.error("Conn btt TCP/IP Timeout!", ste);
			busiCtx.setErrorInfo(AppConstants.STATUS_FAIL,
					"Conn btt TCP/IP Timeout!", transCode);
		} catch (Exception cpe) {
			logger.error("btt interface reponse status error!", cpe);
			busiCtx.setErrorInfo(AppConstants.STATUS_FAIL,
					"btt interface reponse status error!", transCode);
		} finally {
			post.abort();
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
			if (null != entity) {
				try {
					EntityUtils.consume(entity);
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
			if (!debug){
				try {
					releaseSession(httpClient,busiCtx);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	private void setHttpHeader(HttpPost post) {
		// Connection:keep-alive
		post.setHeader("Connection", "keep-alive");
		// User-Agent:Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1
		// (KHTML, like Gecko) Chrome/14.0.803.0 Safari/535.1
		post.setHeader(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.803.0 Safari/535.1");
		post.setHeader("Content-Type", "text/xml; charset=UTF-8");
		post.setHeader("Accept", "text/mobilexml");
	}

	private HttpClient createHttpClient(IBusinessContext busiCtx) {
		HttpClient httpClient = new DefaultHttpClient(cm);
		httpClient.getParams().setIntParameter("http.connection.timeout",
				connTimeout);
		httpClient.getParams().setIntParameter("http.socket.timeout",
				readTimeout);

		((AbstractHttpClient) httpClient)
				.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy() {

					public long getKeepAliveDuration(HttpResponse response,
							HttpContext context) {
						long keepAlive = super.getKeepAliveDuration(response,
								context);
						if (keepAlive == -1) {
							// 如果keep-alive值没有由服务器明确设置，那么保持连接持续5秒。
							keepAlive = 50000;
						}
						return keepAlive;
					}
				});

		try {
			createSession(httpClient, busiCtx);
		} catch (Exception e) {
			logger.error("create btt session happed error!", e);
		}
		return httpClient;
	}

	/**
	 * 创建BTT Session 及并保持 httpClient
	 * 
	 * @throws Exception
	 */
	public void createSession(HttpClient httpClient, IBusinessContext busiCtx)
			throws Exception {
		logger.debug("to create btt session....");
		String dse_sessionid = null;
		String responseStr = "";
		HttpEntity entity = null;
		InputStream in = null;
		BufferedReader br = null;
		HttpPost post = new HttpPost(serverURL + "/AjaxEstablishSession");
		try {
			StringEntity myEntity = new StringEntity("", "UTF-8");
			// Connection:keep-alive
			post.setHeader("Connection", "keep-alive");
			// User-Agent:Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1
			// (KHTML, like Gecko) Chrome/14.0.803.0 Safari/535.1
			post.setHeader(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.803.0 Safari/535.1");
			post.setEntity(myEntity);
			HttpResponse response = httpClient.execute(post);
			HttpEntity resEntity = response.getEntity();
			// 解析读取数据流
			entity = new BufferedHttpEntity(resEntity);

			in = entity.getContent();
			StringBuffer sb = new StringBuffer();
			br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String tempStr;
			while ((tempStr = br.readLine()) != null) {
				sb.append(tempStr);
			}
			responseStr = sb.toString();
			logger.debug("receive data:[" + responseStr + "]");
			if (responseStr != null && responseStr.startsWith("{")) {
				JSONObject dataO = new JSONObject(responseStr);
				if (dataO != null) {
					dse_sessionid = (String) dataO.get("dse_sessionId");
				}
				logger.debug("to create btt session success : " + dse_sessionid);
			} else {
				logger.error("to create btt session failure!");
			}
		} finally {
			post.abort();
			if (null != br) {
				br.close();
			}
			if (null != in) {
				in.close();
			}
			if (null != entity) {
				EntityUtils.consume(entity);
			}
		}
	}

	/**
	 * 释放BTT Session 及 httpClient
	 * 
	 * @throws Exception
	 */
	public void releaseSession(HttpClient httpClient, IBusinessContext busiCtx)
			throws Exception {
		logger.debug("to create btt session....");
		HttpEntity entity = null;
		InputStream in = null;
		BufferedReader br = null;
		HttpPost post = new HttpPost(serverURL);
		try {
			// 创建数据
			JSONObject sendJson = new JSONObject();
			StringEntity myEntity = new StringEntity(sendJson.toString(),
					"UTF-8");
			// Connection:close 服务器主动关闭连接
			post.setHeader("Connection", "close");
			// User-Agent:Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1
			// (KHTML, like Gecko) Chrome/14.0.803.0 Safari/535.1
			post.setHeader(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.803.0 Safari/535.1");
			post.setEntity(myEntity);
			HttpResponse response = httpClient.execute(post);
			HttpEntity resEntity = response.getEntity();
			// 解析读取数据流
			entity = new BufferedHttpEntity(resEntity);

			in = entity.getContent();
			StringBuffer sb = new StringBuffer();
			br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String tempStr;
			while ((tempStr = br.readLine()) != null) {
				sb.append(tempStr);
			}
			String responseStr = sb.toString();

			logger.debug("release btt connection :" + responseStr);
			busiCtx.setResponseEntry(responseStr);
		} finally {
			post.abort();
			if (null != br) {
				br.close();
			}
			if (null != in) {
				in.close();
			}
			if (null != entity) {
				EntityUtils.consume(entity);
			}
		}
	}

	public String getConnTimeoutStr() {
		return connTimeoutStr;
	}

	public void setConnTimeoutStr(String connTimeoutStr) {
		this.connTimeoutStr = connTimeoutStr;

		if (connTimeoutStr.indexOf(".") == -1) {
			this.connTimeout = StringUtil.string2Int(connTimeoutStr);
		}
	}

	public String getReadTimeoutStr() {
		return readTimeoutStr;
	}

	public void setReadTimeoutStr(String readTimeoutStr) {
		this.readTimeoutStr = readTimeoutStr;
		if (readTimeoutStr.indexOf(".") == -1) {
			this.readTimeout = StringUtil.string2Int(readTimeoutStr);
		}
	}

	public String getConnectionsPerHostStr() {
		return connectionsPerHostStr;
	}

	public void setConnectionsPerHostStr(String connectionsPerHostStr) {
		this.connectionsPerHostStr = connectionsPerHostStr;
		if (connectionsPerHostStr.indexOf(".") == -1) {
			this.connectionsPerHost = StringUtil
					.string2Int(connectionsPerHostStr);
		}
	}

	public String getMaxTotalConnectionsStr() {
		return maxTotalConnectionsStr;
	}

	public void setMaxTotalConnectionsStr(String maxTotalConnectionsStr) {
		this.maxTotalConnectionsStr = maxTotalConnectionsStr;
		if (connectionsPerHostStr.indexOf(".") == -1) {
			this.connectionsPerHost = StringUtil
					.string2Int(connectionsPerHostStr);
		}
	}

}
