package cn.com.yitong.yantai.webservice;

/**
 * @author luanyu
 * @date   2018年1月25日
 */
public class PushMessageImpl implements PushMessage {

	@Override
	public String sendMessage(String jsonObject) throws Exception {
		// TODO Auto-generated method stub
		System.err.println(jsonObject);
		return "{\"STSTUS\":1,\"MSG\":\"ok\"}";
	}


}
