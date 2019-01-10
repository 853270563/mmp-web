package cn.com.yitong.framework.net;

import org.apache.http.client.HttpClient;

import cn.com.yitong.framework.base.IBusinessContext;

/**
 * 通讯处理接口
 * 
 * @author yaoym
 * 
 */
public interface IClientFactory {

	/**
	 * 交易执行
	 * 
	 * @param rst
	 * @return
	 * @throws Exception
	 */
	public boolean execute(IBusinessContext businessContext, String transCode);

	/**
	 * 释放连接
	 * 
	 * @param httpClient
	 * @param busiCtx
	 * @throws Exception
	 */
	public void releaseSession(HttpClient httpClient, IBusinessContext busiCtx)
			throws Exception;
}
