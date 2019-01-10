package cn.com.yitong.framework.core.bean;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.HttpClient;

import cn.com.yitong.framework.net.IHttpClientCaches;

/**
 * httpclient会话缓存容器
 * 
 * @author yaoym
 * 
 */
public class HttpClientCaches implements IHttpClientCaches {

	private Map<String, HttpClient> clientMap = new HashMap<String, HttpClient>();

	public HttpClientCaches() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yitong.app.bean.IHttpClientCaches#putHttpClient(java.lang.String,
	 * org.apache.commons.httpclient.HttpClient)
	 */
	
	public void putHttpClient(String sid, HttpClient client) {
		clientMap.put(sid, client);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yitong.app.bean.IHttpClientCaches#getClientBySid(java.lang.String)
	 */
	
	public HttpClient getClientBySid(String sid) {
		return clientMap.get(sid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yitong.app.bean.IHttpClientCaches#removeClient(java.lang.String)
	 */
	
	public void removeClient(String sid) {
		clientMap.remove(sid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yitong.app.bean.IHttpClientCaches#hasHttpClient(java.lang.String)
	 */
	
	public boolean hasHttpClient(String sid) {
		return clientMap.containsKey(sid);
	}
}
