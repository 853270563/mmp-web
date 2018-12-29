package cn.com.yitong.util;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpClientUtil {

	public static String htmlstr(String url) {
		String response = null;
		HttpGet post = new HttpGet(url);
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpResponse result;
		try {
			result = httpclient.execute(post);
			HttpEntity entry = result.getEntity();
			if (entry != null) {
				response = EntityUtils.toString(entry);
			}
		} catch (SocketTimeoutException e) {
			System.out.println("http to ebank timeout!");
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}
}