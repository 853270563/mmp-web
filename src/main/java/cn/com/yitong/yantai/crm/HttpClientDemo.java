package cn.com.yitong.yantai.crm;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.util.ResourceUtils;

/**
 * @author luanyu
 * @date   2018年4月18日
 */
public class HttpClientDemo {


	/** 
	 * post方式提交xml代码 
	 * @throws Exception  
	 */
	public void postXml() throws Exception {
		//创建默认的httpClient实例.   
		CloseableHttpClient httpclient = null;
		//接收响应结果  
		CloseableHttpResponse response = null;
		try {
			//创建httppost  
			httpclient = HttpClients.createDefault();
			String url = "http://36.32.193.12:7001/crm/service/custInfo";
			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader(HTTP.CONTENT_TYPE, "text/xml");
			//参数  
			File file = ResourceUtils.getFile("classpath:META-INF/debug/data.xml");
			String xml = FileUtils.readFileToString(file, "utf-8");
			StringEntity se = new StringEntity(xml);
			se.setContentEncoding("UTF-8");
			httpPost.setEntity(se);
			response = httpclient.execute(httpPost);
			//解析返结果  
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String resStr = EntityUtils.toString(entity, "UTF-8");
				System.out.println(resStr);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			httpclient.close();
			response.close();
		}
	}



}
