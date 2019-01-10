package cn.com.yitong.framework.net.impl.esb;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import cn.com.yitong.util.YTLog;

/**
 * 定长通讯
 * 
 * @author Administrator
 * 
 */
public class SocketClientesb {

	static Logger logger = YTLog.getLogger(SocketClientesb.class.getName());

	private Socket serverSocket = null;

	private OutputStream outputStream = null;

	private InputStream inputStream = null;

	private int soTimeout = 0;

	private final int LENGTH = ESBConst.LENGTH;

	public SocketClientesb() {
	}

	public boolean init(String serverIP, int serverPort) {
		logger.info("服务器地址:\t " + serverIP + " : " + serverPort);
		try {
			serverSocket = new Socket(serverIP, serverPort);
			serverSocket.setSoTimeout(soTimeout);
			outputStream = serverSocket.getOutputStream();
			inputStream = serverSocket.getInputStream();
			return true;
		} catch (UnknownHostException ex) {
			logger.info("无法找到该服务器: " + serverIP);
		} catch (IOException ex) {
			logger.info("无法和服务器建立连接：" + serverIP + ":" + serverPort);
		}
		return false;
	}

	/**
	 * Get the output stream of the connection to the server, so client can send
	 * message to it.
	 * 
	 * @param
	 * @return OutputStream
	 */
	public OutputStream getOutputStream() {
		return outputStream;

	}

	/**
	 * Get the input stream of the connection to the server, so client can read
	 * message from it.
	 * 
	 * @param
	 * @return InputStream
	 */
	public InputStream getInputStream() {
		return inputStream;

	}

	/**
	 * Close the connection to the server
	 * 
	 * @param
	 * @return void
	 */
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

	/**
	 * send message by String,message will be added 4 bytes' length
	 * 
	 * @param stream
	 *            output stream of the connection to the server
	 * @param message
	 *            message will be sended
	 * 
	 * @return void
	 */
	public boolean sendMessage(String message) {
		logger.info("send string message:\n " + message + "$");
		OutputStreamWriter writer = new OutputStreamWriter(outputStream);
		try {
			int len = message.getBytes().length;
			String msg_length = fillString(String.valueOf(len), '0', LENGTH,
					false);
			logger.info("报文总长度: " + msg_length + message);
			writer.write(msg_length + message, 0, LENGTH + message.length());
			writer.flush();
			return true;
		} catch (IOException ex) {
			logger.info(ex.getMessage());
		}finally{
			try {
				writer.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return false;
	}

	/**
	 * send message by byte array,message will be added 4 bytes' length
	 * 
	 * @param stream
	 *            output stream of the connection to the server
	 * @param message
	 *            message will be sended
	 * 
	 * @return void
	 */
	public boolean sendMessage(OutputStream stream, byte[] message) {
		logger.info("send byte message:" + new String(message) + "$");
		DataOutputStream writer = new DataOutputStream(stream);
		try {
			int len = message.length;
			byte[] str = new byte[len + LENGTH];
			String msg_length = fillString(String.valueOf(len), '0', LENGTH,
					false);
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
			logger.info(ex.getMessage());
		}
		return false;
	}

	/**
	 * get return message
	 * 
	 * @param reader
	 *            input stream of the connection to the server
	 * 
	 * @return byte[]
	 */
	public byte[] receiveMessage() {
		logger.info("----receiveMessage-----1------");
		byte[] messageLength = new byte[LENGTH];
		try {
			inputStream.read(messageLength, 0, LENGTH);
		} catch (IOException ex) {
			ex.printStackTrace();
			logger.info("接受消息长度时出现网络错误." + ex.getMessage());
			return null;
		}
		logger.info("----receiveMessage-----2------");
		int msgLength = 0;
		try {
			msgLength = Integer.parseInt(new String(messageLength));
			logger.info("receive message byte length is:\t" + msgLength);
		} catch (NumberFormatException ex) {
			logger.info("消息接受长度为非数字！");
			return null;
		}

		logger.info("----receiveMessage-----3------");
		if (msgLength <= 0) {
			logger.info("消息长度为零");
			return null;
		}
		byte[] message = new byte[msgLength];
		try {
			inputStream.read(message, 0, msgLength);
			logger.info("----receiveMessage-----4------");
			return message;
		} catch (IOException ex) {
			logger.warn("接受消息体时出现网络错误.");
		}
		logger.info("----receiveMessage-----5------");
		return null;
	}

	/**
	 * 填充字符串
	 * 
	 * @param string
	 *            原来填充字符串
	 * @param filler
	 *            填充字符
	 * @param totalLength
	 *            填充后总长度
	 * @param atEnd
	 *            true为后面填充 ，false为前面填充
	 * @return new String
	 */
	private String fillString(String string, char filler, int totalLength,
			boolean atEnd) {
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