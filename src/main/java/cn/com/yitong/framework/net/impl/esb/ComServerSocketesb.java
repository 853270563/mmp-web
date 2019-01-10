package cn.com.yitong.framework.net.impl.esb;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import cn.com.yitong.util.ByteWriter;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

/**
 * 
 * @author Administrator
 * 
 */
public class ComServerSocketesb{

	private static Logger logger = YTLog.getLogger(ComServerSocketesb.class.getName());

	private Socket serverSocket = null;

	private DataOutputStream outputStream = null;

	private DataInputStream inputStream = null;

	private int soTimeout = 60*1000;

	public ComServerSocketesb() {
	}

	public boolean init(String serverIP, int serverPort) {
		try {
			logger.info("---connectToHost-->建立socket连接--> host：" + serverIP + "   port:：" + serverPort+"......");
			serverSocket = new Socket(serverIP, serverPort);
			serverSocket.setSoTimeout(soTimeout);
			inputStream = new DataInputStream(serverSocket.getInputStream());
			outputStream = new DataOutputStream(serverSocket.getOutputStream());
			logger.info("---connectToHost-->建立socket连接--> host：" + serverIP + "   port:：" + serverPort+" success!");
			return true;
		} catch (UnknownHostException ex) {
			logger.info("无法找到该服务器: " + serverIP);
		} catch (IOException ex) {
			logger.info("无法和服务器建立socket连接：" + serverIP + ":" + serverPort);
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
	public void close() {
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
		try {
			String lengthStr=""+message.getBytes(ESBConst.UTF_8).length;
			String headNumSre=StringUtil.lpadString(lengthStr, ESBConst.LENGTH, "0");
			message=headNumSre+message;
			int len = message.getBytes(ESBConst.UTF_8).length;
			logger.info("send string message:\n " + message + "$");
			logger.info("报文总长度: " + len);
			outputStream.write(message.getBytes(ESBConst.UTF_8), 0, len);
			outputStream.flush();
			return true;
		} catch (IOException ex) {
			logger.info(ex.getMessage());
		}finally{
			try {
				serverSocket.shutdownOutput();
			} catch (IOException e) {
				logger.warn("ComServerSocket output close",e);
			}
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
	public String receiveMessage() {
		String rspLn = "";
		try {
			byte[] rsp = new byte[1];
			ByteWriter readBuf = new ByteWriter();
			while (inputStream.read(rsp, 0, rsp.length) != -1) {
				readBuf.append(rsp);
			}
			rspLn = new String(readBuf.getByte(),ESBConst.UTF_8); // , "Cp937");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return rspLn;
	}

	public static void main(String[] args) {
		ComServerSocketesb socket = new ComServerSocketesb();
		String msg = "<?xml version='1.0' encoding='UTF-8'?><Service><Service_Header><service_sn>1600970000009000281</service_sn><service_time>20160727164937</service_time><service_id>00010000397304</service_id><requester_id>0097</requester_id><branch_id>817018000</branch_id><channel_id>39</channel_id><version_id>01</version_id></Service_Header><Service_Body><ext_attributes><INM-TELLER-ID>81701934    </INM-TELLER-ID><INM-BRANCH-ID>817018000</INM-BRANCH-ID></ext_attributes><request><CER-TYP>G</CER-TYP></request></Service_Body></Service>";
		socket.init("36.0.12.112", 9052);
		socket.sendMessage(msg);
		String rsp = socket.receiveMessage();
		System.out.println(rsp);
		socket.close();
	}

}