package cn.com.yitong.util;

/*
 * setCleanSession设置为 false，那么客户机创建的任何预订都会被添加至客户机在连接之前就已存在的所有预订。
 * 当客户机断开连接时，所有预订仍保持活动状态。 设置为 true，那么在客户机建立连接时，将除去客户机的任何旧预订。
 * 当客户机断开连接时，会除去客户机在会话期间创建的任何新预订。 在连接之前，您必须设置 cleanSession
 * 方式；在整个会话期间都将保持此方式。要更改此属性的设置， 必须将客户机断开连接，然后再重新连接客户机。 如果您将方式从使用
 * cleanSession=false 更改为 cleanSession=true， 那么此客户机先前的所有预订以及尚未接收到的任何发布都将被废弃
 * 连接mqtt并返回一个MqttClient对象 参数说明：serverURI服务端地址，例如：tcp://127.0.0.1:1883
 * clientId：会话标示，随机数，但是长度不能超过23位
 * 
 * 返回值true发布成功，false发布失败
 */

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

public class MqttUtil {
	public static MqttClient connect(String url) {
		MqttConnectOptions options = new MqttConnectOptions();
		options.setCleanSession(false);
		options.setConnectionTimeout(MqttConnectOptions.CONNECTION_TIMEOUT_DEFAULT);// 设置连接超时时间
		options.setKeepAliveInterval(MqttConnectOptions.KEEP_ALIVE_INTERVAL_DEFAULT);// 设置会话保存时间
		MqttClient mqttClient=null;
		try {

			String clientId = String.format("%-23.23s","HaierTuiSong"+ "_"+ System.getProperty("clientId",(UUID.randomUUID().toString())).trim()).replace('-', '_');
			mqttClient = new MqttClient(url, clientId);
			mqttClient.setCallback(new MqttServiceCallback());// 设置回调函数
			mqttClient.connect(options);// 连接mqtt
		} catch (MqttException e) {
			e.printStackTrace();
		}
		return mqttClient;
	}

	// 发送消息
	public static boolean pushMessage(MqttClient mqttClient, String topic,
			String content, int qos) {
		boolean b = false;
		try {
			if (mqttClient.isConnected()) {
				// mqttClient.setCallback(new MqttServiceCallback());
				MqttMessage message = new MqttMessage();// 消息对象
				message.setPayload(content.getBytes("UTF-8"));
				// 添加消息内容
				message.setRetained(false);
				message.setQos(qos);// 设置消息质量
				MqttTopic mqttTopic = mqttClient.getTopic(topic);// 异步发送消息
				MqttDeliveryToken token = mqttTopic.publish(message);
				boolean complete = token.isComplete();// true，说明已废弃此消息,false完成了传递
				if (!complete) {
					System.out.println("推送主题： " + topic + " 内容: " + content
							+ "成功");
					b = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("推送消息异常......");
		}finally{
			try {
				mqttClient.disconnect();
				mqttClient.close();
				b = true;
				return b;
			} catch (MqttException e) {
				e.printStackTrace();
			}
		}
		return b;
	}

	/*
	 * 接收消息方法 参数： MqttClient:连接mqtt对象 ;返回值：true订阅成功，false订阅失败
	 */
	public static boolean reciveMessage(MqttClient mqttClient, String topic) {
		boolean b = false;
		if (mqttClient != null) {
			try {
				// 接收消息方法，消息会在MqttPublishCallback中的messageArrived方法处理
				mqttClient.subscribe(topic);
				b = true;
				return b;
			} catch (MqttException e) {
				e.printStackTrace();
				System.out.println("接收消息异常。。。。。");
			}
		}
		return b;
	}

	public static boolean disconnect(MqttClient mqttClient) {
		boolean b = false;
		try {
			mqttClient.disconnect();
			mqttClient.close();
			b = true;
			return b;
		} catch (MqttException e) {
			e.printStackTrace();
			return b;
		}
	}
}
