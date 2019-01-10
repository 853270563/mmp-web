package cn.com.yitong.util;


import java.io.IOException;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MqttAsyncPushMessage implements MqttCallback {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	boolean cleanSession = false;
	private static MqttAsyncClient mqttAsyncClient;
	private static MqttConnectOptions mqttConnectOptions;
	
	public MqttAsyncPushMessage(String url) {
		String clientId = String.format("%-23.23s", "HaierTuiSong" + "_" + System.getProperty("clientId", (UUID.randomUUID().toString())).trim()).replace('-',
				'_');
		String tmpDir = System.getProperty("java.io.tmpdir");
		MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);
		try {
			mqttConnectOptions = new MqttConnectOptions();
			mqttConnectOptions.setCleanSession(cleanSession);
			mqttAsyncClient = new MqttAsyncClient(url, clientId, dataStore);
			mqttAsyncClient.setCallback(this);
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	public boolean publish(String topic, int qos, boolean retained, byte[] payload) {
		boolean b = true;
		try {
			IMqttToken conToken = mqttAsyncClient.connect(mqttConnectOptions, null, null);
			conToken.waitForCompletion();
			MqttMessage message = new MqttMessage(payload);
			message.setQos(qos);
			IMqttDeliveryToken pubToken = mqttAsyncClient.publish(topic, payload, qos, retained);
			pubToken.waitForCompletion();
			boolean complete = pubToken.isComplete();
			b = complete;
			IMqttToken discToken = mqttAsyncClient.disconnect();
			discToken.waitForCompletion();
		} catch (MqttException e) {
			b = false;
			e.printStackTrace();
		}
		return b;
	}

	public void subscribe(String topic, int qos) {
		try {
			IMqttToken conToken = mqttAsyncClient.connect(mqttConnectOptions);
			conToken.waitForCompletion();
			IMqttToken subToken = mqttAsyncClient.subscribe(topic, qos);
			subToken.waitForCompletion();
			try {
				System.in.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
			IMqttToken discToken = mqttAsyncClient.disconnect();
			discToken.waitForCompletion();
		} catch (MqttException e) {
			e.printStackTrace();
		}

	}

	public void connectionLost(Throwable cause) {
		logger.info("[-------------------mqtt连接丢失了--------------------]");
		logger.info("[-------------------mqtt连接丢失了--------------------]");
		logger.info("[-------------------mqtt连接丢失了--------------------]");
	}

	public void deliveryComplete(IMqttDeliveryToken token) {
			logger.info("[------------------------ 异步推送消息完成----------------]");
			logger.info("[------------------------ 异步推送消息完成----------------]");
			logger.info("[------------------------ 异步推送消息完成----------------]");
	}

	public void messageArrived(String topic, MqttMessage message) {
		logger.info("[------------接收消息主题："+topic+" : "+"接收消息内容："+new String(message.getPayload())+"-------------]");
		logger.info("[------------接收消息主题："+topic+" : "+"接收消息内容："+new String(message.getPayload())+"-------------]");
		logger.info("[------------接收消息主题："+topic+" : "+"接收消息内容："+new String(message.getPayload())+"-------------]");
	}

}