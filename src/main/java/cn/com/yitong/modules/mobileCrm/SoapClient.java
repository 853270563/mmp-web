package cn.com.yitong.modules.mobileCrm;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * WebService客户端调用通用方法，通过HttpClient发送SOAP报文请求，获取请求响应报文，
 * 用户可以获取响应报文Docment和字符串两种表示形式。
 * @version 
 */
public class SoapClient {
	private HttpClient client;
	
	public SoapClient(HttpClient client) {
        this.client = client;
	}
	
	/**
	 * WebSerice客户端发送SOAP报文请求，返回请求响应报文，报文格式为字符串
	 * 
	 * @param address           WebSerice请求服务端地址
	 * @param soapRequestMsg    SOAP请求报文
	 * @return                  返回SOAP响应报文
	 * @throws HttpException
	 * @throws IOException
	 */
	public String sendRequest(String address, String soapRequestMsg) throws HttpException, IOException{
		return sendRequest(address, soapRequestMsg, "");
    }
	
	/**
	 * WebSerice客户端发送SOAP报文请求，返回请求响应报文，报文格式为字符串
	 * 
	 * @param address           WebSerice请求服务端地址
	 * @param soapRequestMsg    SOAP请求报文
	 * @param action            WebSerice 请求参数SOAPAction值
	 * @return                  返回SOAP响应报文
	 * @throws HttpException
	 * @throws IOException
	 */
	public String sendRequest(String address, String soapRequestMsg, String action) throws HttpException, IOException{
    	String responseBodyAsString;
		PostMethod postMethod = new PostMethod(address);
		try {
			postMethod.setRequestHeader("SOAPAction", null);
			postMethod.setRequestEntity(new InputStreamRequestEntity(
					new ByteArrayInputStream(soapRequestMsg.getBytes("UTF-8")), "text/xml")

			);
			client.executeMethod(postMethod);
			responseBodyAsString = postMethod.getResponseBodyAsString();
		} finally {
			postMethod.releaseConnection();
		}
    	
		return responseBodyAsString;
    }
}
