package cn.com.yitong.yantai.websocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PreDestroy;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.yitong.framework.servlet.ServerInit;

/**
 * @author luanyu
 * @date   2018年3月28日
 */
//@Component
public class WsServer extends WebSocketServer {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	private static Map<String, WebSocket> clients = new ConcurrentHashMap<String, WebSocket>();
	public WsServer(int port) {
		super(new InetSocketAddress(port));
		WebSocketImpl.DEBUG = false;
	}

	public WsServer(InetSocketAddress address) {
		super(address);
	}

	public WsServer() {
		this(ServerInit.getInt("WEBSOCKET_PORT"));
		this.start();
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		// ws连接的时候触发的代码，onOpen中我们不做任何操作
		String resourceDescriptor = handshake.getResourceDescriptor();
		String userId = resourceDescriptor.substring(resourceDescriptor.lastIndexOf("/") + 1);
		logger.info(userId + "建立连接");
		userJoin(conn, userId);

	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		userLeave(conn);

	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		logger.info("websocket收到消息:{}", message);
		conn.send("服务端收到消息:" + message);
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		userLeave(conn);
		logger.error(ex.getMessage(), ex);
	}

	/**
	 * 去除掉失效的websocket链接
	 * @param conn
	 */
	private void userLeave(WebSocket conn) {
		conn.close();
		Set<Entry<String, WebSocket>> entrySet = clients.entrySet();
		for (Entry<String, WebSocket> entry : entrySet) {
			if (entry.getValue() == conn) {
				clients.remove(entry.getKey());
				logger.info(entry.getKey() + "断开连接");
				break;
			}
		}
	}

	/**
	 * 将websocket加入用户池
	 * @param conn
	 * @param userName
	 */
	private void userJoin(WebSocket conn, String userName) {
		clients.put(userName, conn);
	}

	/**
	 * 发送消息给指定用户
	 * @param message
	 * @param To
	 * @throws IOException
	 */
	public static void sendMessageTo(String message, String To) throws IOException {
		if (To == null) {
			return;
		}
		WebSocket webSocket = clients.get(To);
		if (webSocket != null) {
			webSocket.send(message);
		}

	}

	/**
	 * 发送消息给所有用户
	 * @param message
	 * @throws IOException
	 */
	public static void sendMessageAll(String message) throws IOException {
		for (WebSocket item : clients.values()) {
			item.send(message);
		}
	}

	@PreDestroy
	public void destory() {
		clients.clear();
	}
	public static void main(String[] args) {
		/*	WebSocketImpl.DEBUG = false;
			int port = 8887; // 端口
			WsServer s = new WsServer(port);
			s.start();*/
		System.out.println(ServerInit.getInt("WEBSOCKET_PORT"));
	}
}
