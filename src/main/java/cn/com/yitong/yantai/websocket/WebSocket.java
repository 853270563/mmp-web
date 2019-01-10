/*package cn.com.yitong.yantai.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

*//**
	* @author luanyu
	* @date   2018年3月28日
	*/
/*
@ServerEndpoint("/websocket/{userId}") //.do不要和变量放在一起,导致服务启动不了。 也会被过滤器拦截变成http请求
public class WebSocket {
protected Logger logger = LoggerFactory.getLogger(getClass());
private static int onlineCount = 0;
private static Map<String, WebSocket> clients = new ConcurrentHashMap<String, WebSocket>();
private Session session;
private String username;

@OnOpen
public void onOpen(@PathParam("userId") String username, Session session) throws IOException {

	this.username = username;
	this.session = session;

	addOnlineCount();
	clients.put(username, this);
	logger.info("websocket-----" + this.username + "已经连接");
}

@OnClose
public void onClose(CloseReason reason) throws IOException {
	clients.remove(username);
	subOnlineCount();
	logger.info(username + "断开websocket连接,原因:{}", reason.getReasonPhrase());

}

@OnMessage
public void onMessage(String message) throws IOException {

	sendMessageTo("服务端收到消息" + message, this.username);
	logger.info("服务端收到消息:{}", message);
}

@OnError
public void onError(Session session, Throwable error) {
	logger.error(error.getMessage(), error);
}

*//**
	* 发送给指定用户
	* @param message 消息
	* @param To who？
	* @throws IOException
	*//*
	
	public static void sendMessageTo(String message, String To) throws IOException {
	// session.getBasicRemote().sendText(message);  
	//session.getAsyncRemote().sendText(message);  
	WebSocket webSocket = clients.get(To);
	if (webSocket != null) {
		webSocket.session.getAsyncRemote().sendText(message);
	}
	
	}
	
	public static void sendMessageAll(String message) throws IOException {
	for (WebSocket item : clients.values()) {
		item.session.getAsyncRemote().sendText(message);
	}
	}
	
	public static synchronized int getOnlineCount() {
	return onlineCount;
	}
	
	private static synchronized void addOnlineCount() {
	WebSocket.onlineCount++;
	}
	
	private static synchronized void subOnlineCount() {
	WebSocket.onlineCount--;
	}
	
	public static synchronized Map<String, WebSocket> getClients() {
	return clients;
	}
	
	}
	*/