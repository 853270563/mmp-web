package cn.com.yitong.framework.util.webservice;

import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.params.HttpClientParams;

/**
 * HttpClient工厂类
 */
public class HttpClientFactory {

	/**
	 * 创建HttpClient实例。
	 * HttpClient连接超时时间为构造函数传入，每个主机最大连接为2
	 * @param timeout TODO
	 * 
	 */
	public static HttpClient createHttpClient(Integer timeout) {
		HttpClientParams clientParams = new HttpClientParams();
		HttpConnectionManagerParams managerParams = new HttpConnectionManagerParams();
		managerParams.setConnectionTimeout(timeout); //设置连接超时时间
		managerParams.setDefaultMaxConnectionsPerHost(2);
		managerParams.setSoTimeout(timeout); //设置读取数据超时时间
		HttpConnectionManager httpConnectionManager = new MultiThreadedHttpConnectionManager();
		httpConnectionManager.setParams(managerParams);

		HttpClient answer = new HttpClient(clientParams);
		answer.setHttpConnectionManager(httpConnectionManager);

		return answer;
	}
	
	/**
	 * 创建HttpClient实例。
	 * 连接超时时长与读取数据超时时长为构造函数注入
	 * 每个主机最大连接为2
	 * @param timeout TODO
	 * 
	 */
	public static HttpClient createHttpClient(Integer connectionTimeout, Integer soTimeOut) {
		HttpClientParams clientParams = new HttpClientParams();
		HttpConnectionManagerParams managerParams = new HttpConnectionManagerParams();
		managerParams.setConnectionTimeout(connectionTimeout); //设置连接超时时间
		managerParams.setDefaultMaxConnectionsPerHost(2);
		managerParams.setSoTimeout(soTimeOut); //设置读取数据超时时间
		HttpConnectionManager httpConnectionManager = new MultiThreadedHttpConnectionManager();
		httpConnectionManager.setParams(managerParams);

		HttpClient answer = new HttpClient(clientParams);
		answer.setHttpConnectionManager(httpConnectionManager);

		return answer;
	}
}
