package cn.com.yitong.yantai.webservice;

import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * @author luanyu
 * @date   2018年1月25日
 */
@WebService(serviceName = "PublishMessage")
public interface PushMessage {
	public String sendMessage(@WebParam(name = "jsonObject") String jsonObject) throws Exception;
}
