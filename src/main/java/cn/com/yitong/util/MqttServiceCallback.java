package cn.com.yitong.util;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttServiceCallback implements MqttCallback {
	// 丢失连接触发事方法
	public void connectionLost(Throwable cause) {
		System.out.println("连接丢失。。。。");
	}

	// 接收消息触发的方法
	public void messageArrived(String topic, MqttMessage mqttMessage)
			throws Exception {
		byte[] payload = mqttMessage.getPayload();
		String message = new String(payload, "UTF-8");
		System.out.println("接收到的消息："+message.toString());
	}
	
//异步消息推送完成调用接口
	public void deliveryComplete(IMqttDeliveryToken token) {
	
	}

}
