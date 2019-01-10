package cn.com.yitong.modules.creditInvestigate;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import cn.com.yitong.util.YTLog;

public class YtClient {
	private Logger logger = YTLog.getLogger(this.getClass());

	private Socket serverSocket = null;

	private DataOutputStream outputStream = null;

	private DataInputStream inputStream = null;

	private int soTimeout = 60000;

	public YtClient(String BANKCS_SERVER_IP, int BANKCS_SERVER_PORT) {
		init(BANKCS_SERVER_IP, BANKCS_SERVER_PORT);
	}

	public void init(String BANKCS_SERVER_IP, int BANKCS_SERVER_PORT) {
		try {
			serverSocket = new Socket(BANKCS_SERVER_IP, BANKCS_SERVER_PORT);
			serverSocket.setSoTimeout(soTimeout);
			inputStream = new DataInputStream(serverSocket.getInputStream());
			outputStream = new DataOutputStream(serverSocket.getOutputStream());
		} catch (UnknownHostException ex) {
			logger.info("无法找到该服务器: " + BANKCS_SERVER_IP + ":"
					+ BANKCS_SERVER_PORT);
			logger.error("无法找到该服务器: " + ex);
		} catch (IOException ex) {
			logger.info("无法和服务器建立连接：" + BANKCS_SERVER_IP + ":"
					+ BANKCS_SERVER_PORT);
			logger.error("无法和服务器建立连接：" + ex);
		}
	}

	// 发送报文取回报文
	public String sendMassage(String sendString) {
		String fsqBapWei = "";
		try {
			outputStream.write(sendString.getBytes("UTF-8"));
			outputStream.flush();
			byte[] byt = new byte[7];
			inputStream.read(byt);
			int length = Integer.parseInt(new String(byt, "UTF-8"));
			byte[] contentbyte = new byte[length];
			int nIdx =0;
			int nReadLen =0;
			while (nIdx < length) {
				nReadLen = inputStream.read(contentbyte, nIdx, length - nIdx);
				if (nReadLen > 0) {
					nIdx = nIdx + nReadLen;
				} else {
					break;
				}
			}
			fsqBapWei = new String(contentbyte,"UTF-8");
			logger.info("获取 返回报文\n" + fsqBapWei);
		} catch (IOException ex) {
			logger.info(ex.getMessage());
		}finally{
			try {
				inputStream.close();
			} catch (Exception e) {
				logger.info(e.getMessage());
			}
			try {
				outputStream.close();
			} catch (Exception e) {
				logger.info(e.getMessage());
			}
			try {
				serverSocket.close();
			} catch (Exception e) {
				logger.info(e.getMessage());
			}
		}
		return fsqBapWei;
	}

	public DataOutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(DataOutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public DataInputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(DataInputStream inputStream) {
		this.inputStream = inputStream;
	}
}
