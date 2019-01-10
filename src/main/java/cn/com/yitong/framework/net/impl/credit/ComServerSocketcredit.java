package cn.com.yitong.framework.net.impl.credit;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import cn.com.yitong.common.utils.ConfigEnum;
import cn.com.yitong.core.util.DictUtils;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

/**
 * 
 * @author Administrator
 * 
 */
public class ComServerSocketcredit{

	private static Logger logger = YTLog.getLogger(ComServerSocketcredit.class.getName());

	private Socket serverSocket = null;

	private DataOutputStream outputStream = null;

	private DataInputStream inputStream = null;

	private int soTimeout = Integer.parseInt(DictUtils.getDictValue(ConfigEnum.DICT_SYS_PARAMS, "socketTimeout", "60000"));

	public ComServerSocketcredit() {
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
			throw new RuntimeException("无法找到该服务器:信贷服务器");
		} catch (IOException ex) {
			logger.warn("无法和服务器建立socket连接：" + serverIP + ":" + serverPort);
			throw new RuntimeException("无法和服务器建立socket连接：信贷服务器");
		}
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
			String lengthStr=""+message.getBytes(CreditConst.UTF_8).length;
			String headNumSre=StringUtil.lpadString(lengthStr, CreditConst.LENGTH, "0");
			message=headNumSre+message;
			int len = message.getBytes(CreditConst.UTF_8).length;
			logger.info("send string message:\n " + message + "$");
			logger.info("报文总长度: " + len);
			outputStream.write(message.getBytes(CreditConst.UTF_8), 0, len);
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
	 * @throws IOException 
	 */
	public String receiveMessage() throws IOException {
		byte[] rspLength = new byte[CreditConst.LENGTH];
		inputStream.read(rspLength);
		int length = Integer.parseInt(new String(rspLength));
		byte[] rspContent = new byte[length];
		logger.info("响应报文总长度" + length);
		int nIdx = 0;
		int nReadLen = 0;
		while (nIdx < length) {
			nReadLen = inputStream.read(rspContent, nIdx, length - nIdx);
			if (nReadLen > 0) {
				nIdx = nIdx + nReadLen;
				logger.debug("已读字节数" + nIdx);
			} else {
				break;
			}
		}
		return new String(rspContent, CreditConst.UTF_8); // , "Cp937");

	}


	public void downloadFile2Local(String localPath) {
		// TODO Auto-generated method stub
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(localPath);
			byte[] rsp = new byte[1024];
			int len = 0;
			while ((len = inputStream.read(rsp)) != -1) {
				fileOutputStream.write(rsp, 0, len);
			}
		} catch (IOException e) {
			logger.error(e.getMessage());

		} finally {
			try {
				if (fileOutputStream != null) {

					fileOutputStream.close();
				}
			} catch (IOException e1) {
				logger.error(e1.getMessage());
			}
			close();
			logger.info("文件" + localPath + "下载完成");
		}

	}

	public static void main(String[] args) throws IOException {
		ComServerSocketcredit socket = new ComServerSocketcredit();
		String msg = "<?xml version='1.0' encoding='UTF-8'?><Service><Service_Header><service_sn>1600970000009000281</service_sn><service_time>20160727164937</service_time><service_id>00010000397304</service_id><requester_id>0097</requester_id><branch_id>817018000</branch_id><channel_id>39</channel_id><version_id>01</version_id></Service_Header><Service_Body><ext_attributes><INM-TELLER-ID>81701934    </INM-TELLER-ID><INM-BRANCH-ID>817018000</INM-BRANCH-ID></ext_attributes><request><CER-TYP>G</CER-TYP></request></Service_Body></Service>";
		socket.init("36.0.1.130", 9052);
		socket.sendMessage(msg);
		String rsp = socket.receiveMessage();
		socket.close();
	}

}