package cn.com.yitong.yantai;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.huawei.mqtt.client.domain.MQTTClient;
import com.huawei.mqtt.client.domain.MQTTServer;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.util.DateUtil;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

//@Service
public class MQTTClientService {
	/**
	 * 注释内容
	 */
	private Logger logger = YTLog.getLogger(this.getClass());

	private MQTTClient client;

	private static int qos = 0;

	public static final String WEB_APP_PARA_PRIFIX = "appurl;";
	//安卓参数
	public static final String AND_APP_PARA_PRIFIX = "packageName;";
	public static final String WEB_APP_PARAMETERS = "com.yantaibank.mbk|com.yitong.ares.app.android.activity.splash.IsLoginActivity";
	public static final String MESSAGE_TEMPLATE    = "appname=%s&message=%s&parameter=%s&target=%d&userlist=%s&validTime=%s";
	//苹果参数
	public static final String AND_APP_PARA_PRIFIX11 = "appschema;";
	public static final String WEB_APP_PARAMETERS11 ="cn.com.bankalliance.mdm.yt-crm";
	/**
	 * 建立连接 <功能详细描述>
	 * 
	 * @param evt
	 * @throws Exception 
	 */
	public void connetActionPerformed() throws Exception {
		if (null != client && client.isConnected()) {
			return;
		}
		MQTTServer server = getMQTTServerFromPage();
		try {
			connectServer(server);
			logger.debug("MQTT 服务器链接成功");
		} catch (Exception e) {
			logger.debug("MQTT 服务器链接失败");
			client.setConnStatus(false);
			throw new Exception(e);
		}
	}

	/**
	 * 连接服务器
	 * 
	 * @param mqttServer
	 * @throws IOException
	 * @throws MqttException
	 * @throws FileNotFoundException
	 * @throws CertificateException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws KeyManagementException
	 */
	private void connectServer(MQTTServer mqttServer) throws KeyManagementException, KeyStoreException,
			NoSuchAlgorithmException, CertificateException, FileNotFoundException, MqttException, IOException {
		client = new MQTTClient("MQTT", mqttServer);
		client.creatConnetion();
		client.setConnStatus(true);
	}

	/**
	 * 
	 * 获取服务参数
	 * 
	 * @return
	 */
	private MQTTServer getMQTTServerFromPage() {
		MQTTServer server = new MQTTServer();
		// 设置用户名
		server.setUserName(AppConstants.MQTT_NAME);
		// 设置用户密码
		server.setPassword(AppConstants.MQTT_PASSWD);
		// 设置IP地址
		server.setServerIp(AppConstants.MQTT_IP);
		// 设置端口号
		server.setServerPort(Integer.valueOf(AppConstants.MQTT_PORT));
		logger.debug("连接MDM MQTT消息推送服务[UserName="+AppConstants.MQTT_NAME+",Password="+AppConstants.MQTT_PASSWD+",ServerIp="+AppConstants.MQTT_IP+",ServerPort="+AppConstants.MQTT_PORT+"]");
		server.setSSL(false);
		return server;
	}

	/**
	 * 消息的拼接 <功能详细描述>
	 * 
	 * @param appName
	 * @param messageContent
	 * @param parameters
	 * @return
	 */
	public String buildMessageBody(Map params) {
		StringBuilder parameterSb = new StringBuilder();
		String appName = StringUtil.getString(params, "appName", "");
		String messageContent = StringUtil.getString(params, "messageContent", "");
		String targeUser = StringUtil.getString(params, "targeUser", "");
		if (StringUtil.isEmpty(appName) || StringUtil.isEmpty(messageContent)) {
			logger.warn("应用名/消息内容/消息参数不能为空!");
			return null;
		}
		//安卓参数
				
		parameterSb.append(AND_APP_PARA_PRIFIX);
		parameterSb.append(WEB_APP_PARAMETERS);
		//苹果参数
		parameterSb.append("#");
		parameterSb.append(AND_APP_PARA_PRIFIX11);
		parameterSb.append(WEB_APP_PARAMETERS11);
		// 选择目标用户
		int userTarget = 1;
		// 目标用户    
		String targeUserList = targeUser;
		// 特殊用户
/*		String specialUser ="";*/
		// 所用目标用户
		String userlist = targeUserList;
		// 有效时间+1天
		String validTime = DateUtil.format(DateUtil.getNextDay(1), "yyyy-MM-dd HH:mm:ss");
		// 发送消息内容
		String message = null;
		try {
			message = String.format(MESSAGE_TEMPLATE, URLEncoder.encode(appName, "UTF-8"),
					URLEncoder.encode(messageContent, "UTF-8"), URLEncoder.encode(parameterSb.toString(), "UTF-8"),
					userTarget, URLEncoder.encode(userlist, "UTF-8"), URLEncoder.encode(validTime, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return message;

	}

	/**
	 * 发送消息
	 * 
	 * @param params
	 */
	public boolean sendButtonActionPerformed(Map<?, ?> params) {
		if (null == client) {
			logger.warn("MDM消息推送MQTT服务器未连接,请先连接服务器");
			return false;
		}
		String topic = "HelloWorld";
		//组长发送消息
		String message = buildMessageBody(params);
		if (null == message) {
			return false;
		}
		try {
			client.publish(topic, message.getBytes("GBK"), qos, false);
			logger.info("发送成功,消息内容: " + message);
		} catch (Exception e) {
			logger.info("发送失败:" + e.getMessage());
			return false;
		}
		return true;
	}

	public static void main(String[] args) throws Exception {
		MQTTClientService mqttClientService = new MQTTClientService();
		mqttClientService.connetActionPerformed();
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("appName", "1");
		hashMap.put("messageContent", "hello");
		hashMap.put("targeUser", "wanghui");
		mqttClientService.sendButtonActionPerformed(hashMap);
	}
}
