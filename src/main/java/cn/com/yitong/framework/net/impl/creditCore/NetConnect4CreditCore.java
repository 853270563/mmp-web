package cn.com.yitong.framework.net.impl.creditCore;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.error.AresRuntimeException;
import cn.com.yitong.ares.error.OtherRuntimeException;
import cn.com.yitong.common.utils.JsonUtils;
import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.servlet.ServerInit;
import cn.com.yitong.util.JsonFormat;

/**
 * @author luanyu
 * @date   2018年8月9日
 */
@Component
public class NetConnect4CreditCore implements IClientFactory {
	private Logger logger = LoggerFactory.getLogger(getClass());
	public final String SERVER_IP = ServerInit.getString("CORE_IP");
	public final String ROOT_PATH = ServerInit.getString("ROOT_PATH");
	public final int SERVER_PORT = ServerInit.getInt("CORE_PORT");
	@Override
	public boolean execute(IBusinessContext businessContext, String transCode) {
		//创建默认的httpClient实例.   
		CloseableHttpClient httpclient = null;
		//接收响应结果  
		CloseableHttpResponse response = null;
		try {
			//创建httppost  
			httpclient = HttpClients.createDefault();
			String url = "http://" + SERVER_IP + ":" + SERVER_PORT + ROOT_PATH + "/" + transCode;
			logger.info("网贷核心请求url:{}", url);
			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
			//参数  
			String json = JsonUtils.objectToJson(businessContext.getRequestEntry());
			StringEntity se = new StringEntity(json,"UTF-8");
			se.setContentEncoding("UTF-8");
			se.setContentType("application/json;charset=UTF-8");//发送json需要设置contentType  
			httpPost.setEntity(se);
			response = httpclient.execute(httpPost);
			//解析返结果  
			HttpEntity entity = response.getEntity();
			if (entity != null && response.getStatusLine().getStatusCode() == 200) {
				String resStr = EntityUtils.toString(entity, "UTF-8");
				logger.debug("网贷核心返回报文：\n{}", JsonFormat.formatJson(resStr));
				businessContext.setResponseEntry(JsonUtils.jsonToMap(resStr));
			} else {
				logger.debug("网贷核心返回报文：\n{}", response);
				throw new OtherRuntimeException(AppConstants.STATUS_FAIL, "网贷核心服务异常");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new OtherRuntimeException(AppConstants.STATUS_FAIL, "网贷核心服务异常");
		} finally {
			try {
				if (response != null) {

					response.close();
				}
				httpclient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage(), e);
				throw new AresRuntimeException(AppConstants.STATUS_FAIL, e);
			}
		}
		return true;
	}

	@Override
	public void releaseSession(HttpClient httpClient, IBusinessContext busiCtx) throws Exception {
		// TODO Auto-generated method stub

	}

}
