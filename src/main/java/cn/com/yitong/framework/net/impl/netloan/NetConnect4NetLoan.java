package cn.com.yitong.framework.net.impl.netloan;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.error.AresCoreException;
import cn.com.yitong.ares.error.AresRuntimeException;
import cn.com.yitong.ares.error.OtherRuntimeException;
import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.MBTransConfBean;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.util.JsonFormat;
import cn.com.yitong.util.common.StringUtil;

@Component
public class NetConnect4NetLoan implements IClientFactory{

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${NETLOAN_SERVER_URL}")
	private String serverURL;
	
	@Autowired
	@Qualifier("EBankConfParserAs")
	IEBankConfParser confParser;
	
	// 并发线程计数
	private AtomicInteger cnt = new AtomicInteger(0);

	@Value("${LIMIT_INTE_THREADS}")
	private int LIMIT;
	private boolean running = false;

	@Override
	public boolean execute(IBusinessContext ctx, String transCode) {
		if (cnt.get() > LIMIT && running) {
			// 激活线程锁
			logger.info("inte.service.busy {}", cnt);
			// 抛出异常，服务正忙
			throw new AresRuntimeException("inte.service.busy");
		}
		MBTransConfBean conf = confParser.findTransConfById(transCode);
		if (conf == null) {
			logger.debug("net.config_not_definied {}", transCode);
			throw new AresCoreException("net.config_not_definied", transCode);
		}
		String excode = conf.getExcode();
		// 测试请求报文生成
		long start = System.currentTimeMillis();
		HttpURLConnection httpConn = null;
		DataOutputStream out = null;
		BufferedReader in = null;
		String req = null;
		String rsp = null;
		String url = StringUtil.message(serverURL, excode);
		try {
			cnt.getAndIncrement();
			running = true;

			URL urlClient = new URL(url);
			httpConn = (HttpURLConnection) urlClient.openConnection();
			setHttpConnection(httpConn);
			showHttpRequestHeaders(httpConn);
			req = (String) ctx.getRequestEntry();
			// 获取URLConnection对象对应的输出流
			out = new DataOutputStream(httpConn.getOutputStream());
			// 发送请求参数
			out.write(req.getBytes("UTF-8"));
			out.flush();

			in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
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
			rsp = sb.toString();

			showHttpResponseHeaders(httpConn);
			ctx.setResponseEntry(rsp);
			logger.info("\n返回报文为:"+JsonFormat.formatJson(rsp));
			return true;
		} catch (MalformedURLException e) {
			logger.error(serverURL, e);
			throw new OtherRuntimeException(AppConstants.STATUS_FAIL, "网贷审批服务异常");
		} catch (IOException e) {
			logger.error(serverURL, e);
			throw new OtherRuntimeException(AppConstants.STATUS_FAIL, "网贷审批服务异常");
		} finally {
			if (running) {
				running = false;// 单线程锁
			}
			cnt.getAndAdd(-2);// 减法大于加法，可修正计数错误
			if(cnt.get() < 0){
				cnt = new AtomicInteger(0);// 并发线程结束
			}
			logger.debug("============================================================");
			long end = System.currentTimeMillis();
			logger.info("url:{} times:{} ms \n req:{},\nrsp:{}", url, (end - start), req, rsp);
			logger.debug("============================================================");

			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
					logger.error("流关闭异常", e);
				}
			}

			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					logger.error("流关闭异常", e);
				}
			}
		}
	}

	/**
	 * 测试请求头
	 * 
	 * @param httpConn
	 */
	private void showHttpResponseHeaders(HttpURLConnection httpConn) {
		if (logger.isDebugEnabled()) {
			logger.debug("response headers:{}", httpConn.getHeaderFields());
		}
	}

	/**
	 * 请求头
	 * 
	 * @param httpConn
	 */
	private void showHttpRequestHeaders(HttpURLConnection httpConn) {
		if (logger.isDebugEnabled()) {
			logger.debug("request headers:{}", httpConn.getRequestProperties());
		}
	}

	/**
	 * 设置请求属性信息
	 * 
	 * @param httpConn
	 * @throws ProtocolException
	 */
	private void setHttpConnection(HttpURLConnection httpConn) throws ProtocolException {
		httpConn.setRequestMethod("POST");
		httpConn.setConnectTimeout(5000);
		//渠道为75s超时，此处要大于渠道超时时间
		httpConn.setReadTimeout(90000);
		httpConn.setRequestProperty("Connection", "keep-alive");
		httpConn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
		httpConn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
		httpConn.setRequestProperty("Accept", "application/json");
		httpConn.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.803.0 Safari/535.1");
		httpConn.setDoInput(true);
		httpConn.setDoOutput(true);
	}

	@Override
	public void releaseSession(HttpClient httpClient, IBusinessContext busiCtx)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
}
