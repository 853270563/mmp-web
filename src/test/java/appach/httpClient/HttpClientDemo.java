package appach.httpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

/**
 * @author luanyu
 * @date   2018年4月18日
 */
public class HttpClientDemo {

	/** 
	 * post方式提交json代码 
	 * @throws Exception  
	 */
	@Test
	public void postJson() throws Exception {
		//创建默认的httpClient实例.   
		CloseableHttpClient httpclient = null;
		//接收响应结果  
		CloseableHttpResponse response = null;
		try {
			//创建httppost  
			httpclient = HttpClients.createDefault();
			String url = "http://127.0.0.1:8080/jeesite/responseJson2";
			//String url = "http://127.0.0.1:8080/jeesite/a/test/testData/responseJson";
			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
			//参数  
			String json = "{'ids':['html1','html2']}";
			StringEntity se = new StringEntity(json);
			se.setContentEncoding("UTF-8");
			se.setContentType("application/json");//发送json需要设置contentType  
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

	/** 
	 * post方式提交xml代码 
	 * @throws Exception  
	 */
	@Test
	public void postXml() throws Exception {
		//创建默认的httpClient实例.   
		CloseableHttpClient httpclient = null;
		//接收响应结果  
		CloseableHttpResponse response = null;
		try {
			//创建httppost  
			httpclient = HttpClients.createDefault();
			//	String url = "http://36.32.192.115:7011/mmp/webservice/pushMessage";
			String url2 = "http://36.10.56.113:7000/mmp/webservice/pushMessage";
			String url = "http://36.32.193.12:7001/crm/service/custInfo";
			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader(HTTP.CONTENT_TYPE, "text/xml");
			//参数  
			String json = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><ns2:sendMessage xmlns:ns2=\"http://webservice.yantai.yitong.com.cn/\"><arg0>{\"appType\":\"\",\"content\":\"\",\"msgId\":\"\",\"peripheralType\":\"0\",\"title\":\"\",\"totalNum\":\"\",\"userIds\":[\"8162201\"]}</arg0></ns2:sendMessage></soap:Body></soap:Envelope>";
			String json2 = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><ns2:sendMessage xmlns:ns2=\"http://webservice.yantai.yitong.com.cn/\"><arg0>{\"appType\":\"\",\"content\":\"\",\"msgId\":\"\",\"peripheralType\":\"0\",\"title\":\"\",\"totalNum\":\"\",\"userIds\":[\"8162201\"]}</arg0></ns2:sendMessage></soap:Body></soap:Envelope>";
			File file = ResourceUtils.getFile("classpath:META-INF/debug/data.xml");
			String jsonString = FileUtils.readFileToString(file, "utf-8");
			StringEntity se = new StringEntity(jsonString);

			se.setContentEncoding("UTF-8");
			se.setContentType("application/json");//发送json需要设置contentType  
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

	/**  
	* post方式提交表单（模拟用户登录请求）  
	* @throws Exception  
	*/
	@Test
	public void postForm() throws Exception {
		// 创建默认的httpClient实例.      
		CloseableHttpClient httpclient = null;
		//发送请求  
		CloseableHttpResponse response = null;
		try {
			httpclient = HttpClients.createDefault();
			// 创建httppost      
			//	String url = "http://127.0.0.1:8080/jeesite/a/test/testData/responseJson";
			String url = "http://36.32.192.115:7001/mmc/a/login";
			HttpPost httppost = new HttpPost(url);
			// 创建参数队列      
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			formparams.add(new BasicNameValuePair("username", "ytbank"));
			formparams.add(new BasicNameValuePair("password", "e10adc3949ba59abbe56e057f20f883e"));
			//参数转码  
			UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
			httppost.setEntity(uefEntity);
			response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				System.out.println(EntityUtils.toString(entity, "UTF-8"));
			}
			//释放连接  
		} catch (Exception e) {
			throw e;
		} finally {
			httpclient.close();
			response.close();
		}
	}

	/**  
	* 发送 get请求  
	* @throws Exception  
	*/
	@Test
	public void get() throws Exception {
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;
		FileOutputStream fileOutputStream = null;
		try {
			httpclient = HttpClients.createDefault();
			// 创建httpget.      
			HttpGet httpget = new HttpGet("http://198.198.200.2:8080/ALS4Mobile/fileServlet?docId=2018031400000001");
			//HttpGet httpget = new HttpGet("http://36.32.192.115:7011/mmp/webservice/pushMessage");
			// 执行get请求.      
			response = httpclient.execute(httpget);
			// 获取响应实体      
			HttpEntity entity = response.getEntity();
			Header lastHeader = response.getLastHeader("Content-Type");
			System.out.println(lastHeader.getValue());
			// 打印响应状态      
			System.out.println(response.getStatusLine().getStatusCode());
			if (entity != null) {
				// 打印响应内容      
				//	System.out.println(entity.getContentType().getValue());
				//System.out.println("Response content: " + EntityUtils.toString(entity));
				fileOutputStream = new FileOutputStream("D://docid2.docx");
				entity.writeTo(fileOutputStream);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			fileOutputStream.close();
			httpclient.close();
			response.close();
		}
	}
}
