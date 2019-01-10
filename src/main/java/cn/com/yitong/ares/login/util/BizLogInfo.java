package cn.com.yitong.ares.login.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 日志信息类
 *
 * @author hry@yitong.com.cn
 * @date 2015年1月9日
 */
public class BizLogInfo {
	/*消息ID*/
	private static final String msg_id = "MSG_ID";
	/*事件ID*/
	private static final String event_id = "EVENT_ID";
	/*由用户系统定义的用户ID*/
	private static final String user_id = "USER_ID";
	/*终端设备ID*/
	private static final String device_id = "DEVICE_ID";
	/*服务器标识*/
	private static final String server_id = "SERVER_ID";
	/*程序标识，服务器可以是被访问的 URL，客户端可以程序名等*/
	private static final String url = "URL";
	/*应用标签，上层应用层自定义的标签串，用于应用内的日志分类*/
	private static final String app_tag = "APP_TAG";
	/*日志级别*/
	private static final String log_level = "LOG_LEVEL";
	/*记录日志的时间*/
	private static final String log_time = "LOG_TIME";
	/*日志数据*/
	private static final String log_data = "LOG_DATA";
	/*sessionId*/
	private static final String session_id = "SESSION_ID";
	/*应用下载渠道*/
	private static final String channel_id = "CHANNEL_ID";
	/*应用标识*/
	private static final String app_id = "APP_ID";
	/*版本号*/
	private static final String app_vers = "APP_VERS";
	/*Ip地址*/
	private static final String ip_addr = "IP_ADDR";
	/*GPS数据*/
	private static final String gps = "GPS";
	/*位置*/
	private static final String place = "PLACE";

	private final Map<String, String> data = new HashMap<String, String>();

	public String getMsg_id() {
		return data.get(BizLogInfo.msg_id);
	}
	public void setMsg_id(String msg_id) {
		data.put(BizLogInfo.msg_id, msg_id);
	}
	public String getEvent_id() {
		return data.get(BizLogInfo.event_id);
	}
	public void setEvent_id(String event_id) {
		data.put(BizLogInfo.event_id, event_id);
	}
	public String getUser_id() {
		return data.get(BizLogInfo.user_id);
	}
	public void setUser_id(String user_id) {
		data.put(BizLogInfo.user_id, user_id);
	}
	public String getDevice_id() {
		return data.get(BizLogInfo.device_id);
	}
	public void setDevice_id(String device_id) {
		data.put(BizLogInfo.device_id, device_id);
	}
	public String getServer_id() {
		return data.get(BizLogInfo.server_id);
	}
	public void setServer_id(String server_id) {
		data.put(BizLogInfo.server_id, server_id);
	}
	public String getUrl() {
		return data.get(BizLogInfo.url);
	}
	public void setUrl(String url) {
		data.put(BizLogInfo.url, url);
	}
	public String getApp_tag() {
		return data.get(BizLogInfo.app_tag);
	}
	public void setApp_tag(String app_tag) {
		data.put(BizLogInfo.app_tag, app_tag);
	}
	public String getLog_level() {
		return data.get(BizLogInfo.log_level);
	}
	public void setLog_level(String log_level) {
		data.put(BizLogInfo.log_level, log_level);
	}
	public String getLog_time() {
		return data.get(BizLogInfo.log_time);
	}
	public void setLog_time(String log_time) {
		data.put(BizLogInfo.log_time, log_time);
	}
	public String getLog_data() {
		return data.get(BizLogInfo.log_data);
	}
	public void setLog_data(String log_data) {
		data.put(BizLogInfo.log_data, log_data);
	}
	public String getSession_id() {
		return data.get(BizLogInfo.session_id);
	}
	public void setSession_id(String session_id) {
		data.put(BizLogInfo.session_id, session_id);
	}

	public String getPlace() {
		return data.get(BizLogInfo.place);
	}

	public void SetPlace(String place) {
		data.put(BizLogInfo.place, place);
	}

	public String getGps() {
		return data.get(BizLogInfo.gps);
	}

	public void setGps(String gps) {
		data.put(BizLogInfo.gps, gps);
	}

	public String getIp_addr() {
		return data.get(BizLogInfo.ip_addr);
	}

	public void setIp_addr(String ip_addr) {
		data.put(BizLogInfo.ip_addr, ip_addr);
	}

	public String getApp_id() {
		return data.get(BizLogInfo.app_id);
	}

	public void setApp_id(String app_id) {
		data.put(BizLogInfo.app_id, app_id);
	}

	public String getApp_vers() {
		return data.get(BizLogInfo.app_vers);
	}

	public void setApp_vers(String app_vers) {
		data.put(BizLogInfo.app_vers, app_vers);
	}

	public String getChannel_id() {
		return data.get(BizLogInfo.channel_id);
	}

	public void setChannel_id(String channel_id) {
		data.put(BizLogInfo.channel_id, channel_id);
	}

	public Map<String, String> toMap() {
		return data;
	}

}
