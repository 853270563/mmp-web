package cn.com.yitong.framework.net;

import org.apache.http.client.HttpClient;

public interface IHttpClientCaches {

	public abstract void putHttpClient(String sid, HttpClient client);

	public abstract HttpClient getClientBySid(String sid);

	public abstract void removeClient(String sid);

	public abstract boolean hasHttpClient(String sid);

}