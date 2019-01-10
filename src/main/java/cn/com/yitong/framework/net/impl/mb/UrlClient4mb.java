package cn.com.yitong.framework.net.impl.mb;

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

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.servlet.ServerInit;
import cn.com.yitong.util.FileUtil;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

@Component
public class UrlClient4mb implements IClientFactory {

	private Logger logger = YTLog.getLogger(this.getClass());

	private final String serverURL = ServerInit.getString("MB_SERVER_URL");

	@Override
	public boolean execute(IBusinessContext busiCtx, String transCode) {
		// 测试请求报文生成
		// XML to JSON
		HttpURLConnection httpConn = null;
		PrintWriter out = null;
		BufferedReader in = null;
		String responseStr = null;
		try {
			String url = serverURL;
			if (logger.isDebugEnabled()) {
				logger.info(transCode + " server url:\t" + url);
			}
			URL urlClient = new URL(url);
			httpConn = (HttpURLConnection) urlClient.openConnection();
			setHttpConnection(httpConn);
			showHttpRequestHeaders(httpConn);
			String xmlStr = StringUtil.formatXmlStr((String) busiCtx
					.getRequestEntry());
			if (logger.isDebugEnabled()) {
				logger.debug(transCode + " send xml msg: \n" + xmlStr);
			}
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(httpConn.getOutputStream());
			// 发送请求参数
			out.print(xmlStr);
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

			if (StringUtil.isEmpty(responseStr)) {
				logger.error(transCode
						+ " the infocast server response msg length is 0!");
				responseStr = "{'result':'" + transCode
						+ " is error!','error_code':'ERROR'}";
			}
			busiCtx.setResponseEntry(responseStr);
			// 设置通讯成功
			// rst.setStatus(AppConstants.MSG_STATUS_OK);
			if (logger.isDebugEnabled()) {
				logger.info(transCode + " receive data: \n" + responseStr);
			}
			return true;
		} catch (MalformedURLException e) {
			logger.error("mb url is error", e); 
		} catch (IOException e) {
			logger.error("mb io is error", e); 
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
		busiCtx.setErrorInfo(AppConstants.STATUS_FAIL,
				"MB通讯异常 !", transCode);
		return false;
	}

	/**
	 * 测试请求头
	 * 
	 * @param httpConn
	 */
	private void showHttpResponseHeaders(HttpURLConnection httpConn) {
		if (true || logger.isDebugEnabled()) {
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
		if (true || logger.isDebugEnabled()) {
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
		httpConn.setConnectTimeout(5000);
		httpConn.setReadTimeout(5000);
		httpConn.setRequestProperty("Connection", "keep-alive");
		httpConn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
		httpConn.setRequestProperty("Accept", "text/mobilexml");
		httpConn.setRequestProperty(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.803.0 Safari/535.1");
		httpConn.setDoInput(true);
		httpConn.setDoOutput(true);
	}

	@Override
	public void releaseSession(HttpClient httpClient, IBusinessContext busiCtx)
			throws Exception {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		UrlClient4mb client = new UrlClient4mb();
		String filePath = "E:/temp/btt/APP.DSB.000010.xml";
		String reqXml = "";
		try {
			reqXml = FileUtil.readFileAsString(filePath, "utf-8").replaceAll(
					"\\n", "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BusinessContext busiCtx = new BusinessContext();
		busiCtx.setRequestEntry(reqXml);

		client.execute(busiCtx, "test");

		String rspXml = (String) busiCtx.getResponseEntry();
		System.out.println("rspXml:" + rspXml);
	}
}
