package cn.com.yitong.modules.common.thirdServer.exec.socket;

import cn.com.yitong.util.YTLog;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketClient {

	static Logger logger = YTLog.getLogger(SocketClient.class.getName());

	private Socket serverSocket = null;

	private OutputStream outputStream = null;

	private InputStream inputStream = null;

	private final int defaultTimeout = 30;

	private final int LENGTH = 6;

	public SocketClient() {
	}

	public boolean init(String serverIP, int serverPort, int timeout) {
		logger.info("服务器地址:\t " + serverIP + " : " + serverPort);
		try {
			serverSocket = new Socket(serverIP, serverPort);
			if(timeout <= 0) {
				timeout = defaultTimeout;
			}
			serverSocket.setSoTimeout(timeout);
			outputStream = serverSocket.getOutputStream();
			inputStream = serverSocket.getInputStream();
			return true;
		} catch (UnknownHostException ex) {
			logger.error("无法找到该服务器: " + serverIP);
		} catch (IOException ex) {
			logger.error("无法和服务器建立连接：" + serverIP + ":" + serverPort);
		}
		return false;
	}

	public OutputStream getOutputStream() {
		return outputStream;

	}

	public InputStream getInputStream() {
		return inputStream;

	}

	public void closeServer() {
		if (outputStream != null) {
			try {
				outputStream.close();
			} catch (IOException e) {
			}
		}
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
			}
		}
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException ex) {
			}
		}
	}

	public boolean sendMessage(String message) {
		logger.info("send string message:\n " + message + "$");
		OutputStreamWriter writer = new OutputStreamWriter(outputStream);
		try {
			int len = message.getBytes().length;
			String msg_length = fillString(String.valueOf(len), '0', LENGTH, false);
			logger.info("发送报文: " + msg_length + message);
			writer.write(msg_length + message, 0, LENGTH + message.length());
			writer.flush();
			return true;
		} catch (IOException ex) {
			logger.error("发送接口失败：" + ex);
		}
		return false;
	}

	public boolean sendMessage(OutputStream stream, byte[] message) {
		logger.info("send byte message:" + new String(message) + "$");
		DataOutputStream writer = new DataOutputStream(stream);
		try {
			int len = message.length;
			byte[] str = new byte[len + LENGTH];
			String msg_length = fillString(String.valueOf(len), '0', LENGTH, false);
			for (int i = 0; i < LENGTH; i++) {
				str[i] = msg_length.getBytes()[i];
			}
			for (int i = 0; i < len; i++) {
				str[i + LENGTH] = message[i];
			}
			writer.write(str, 0, len + LENGTH);
			writer.flush();
			return true;
		} catch (IOException ex) {
			logger.error("发送接口失败：" + ex);
		}
		return false;
	}

	public byte[] receiveMessage() {
		logger.info("----receiveMessage-----1------");
		byte[] messageLength = new byte[LENGTH];
		try {
			inputStream.read(messageLength, 0, LENGTH);
		} catch (IOException ex) {
			logger.error("接受消息长度时出现网络错误.", ex);
			return null;
		}
		logger.info("----receiveMessage-----2------");
		int msgLength = 0;
		try {
			msgLength = Integer.parseInt(new String(messageLength));
			logger.info("receive message byte length is:\t" + msgLength);
		} catch (NumberFormatException ex) {
			logger.error("消息接受长度为非数字！", ex);
			return null;
		}

		logger.info("----receiveMessage-----3------");
		if (msgLength <= 0) {
			logger.error("消息长度为零");
			return null;
		}
		byte[] message = new byte[msgLength];
		try {
			inputStream.read(message, 0, msgLength);
			logger.info("----receiveMessage-----4------");
			return message;
		} catch (IOException ex) {
			logger.error("接受消息体时出现网络错误.", ex);
		}
		logger.info("----receiveMessage-----5------");
		return null;
	}

	/**
	 * 填充字符串
	 * @param string 原来填充字符串
	 * @param filler 填充字符
	 * @param totalLength 填充后总长度
	 * @param atEnd  true为后面填充 ，false为前面填充
	 */
	private String fillString(String string, char filler, int totalLength, boolean atEnd) {
		byte[] tempbyte = string.getBytes();
		int currentLength = 0;
		if (tempbyte != null)
			currentLength = tempbyte.length;
		int delta = totalLength - currentLength;
		for (int i = 0; i < delta; i++) {
			if (atEnd) {
				string += filler;
			} else {
				string = filler + string;
			}
		}
		return string;
	}
}