package cn.com.yitong.framework.net.impl.bankcs;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import cn.com.yitong.util.ByteWriter;
import cn.com.yitong.util.YTLog;

/**
 * 
 * @author Administrator
 * 
 */
public class ComServerSocket{

	static Logger logger = YTLog.getLogger(ComServerSocket.class.getName());

	private Socket serverSocket = null;

	private DataOutputStream outputStream = null;

	private DataInputStream inputStream = null;

	private int soTimeout = 0;

	private final int LENGTH = EBankConst.LENGTH;

	public ComServerSocket() {
	}

	public boolean init(String serverIP, int serverPort) {
		logger.info("服务器地址:\t " + serverIP + " : " + serverPort);
		try {
			serverSocket = new Socket(serverIP, serverPort);
			serverSocket.setSoTimeout(soTimeout);
			inputStream = new DataInputStream(serverSocket.getInputStream());
			outputStream = new DataOutputStream(serverSocket.getOutputStream());
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
		logger.info("send string message:\n " + message + "$");
		try {
			byte[] endChars = { 0x0D, 0x0A };
			message += new String(endChars);
			message += EBankConst.FIX_END;
			byte[] bytes = message.getBytes();
			int len = message.getBytes().length;
			logger.info("报文总长度: " + len);
			outputStream.write(bytes, 0, len);
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
//			byte[] rsp = new byte[1];
//			ByteWriter readBuf = new ByteWriter();
//			while (inputStream.read(rsp, 0, 1) != -1) {
//				if (rsp[0] == EBankConst.FIX_END) {
//					break;
//				}
//				readBuf.append(rsp[0]);
//			}
//			rspLn = new String(readBuf.getByte(),"utf-8"); // , "Cp937");
//			System.out.println("===rspLn===="+rspLn);
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
			rspLn = new String(contentbyte,"UTF-8");
			logger.info("获取 返回报文\n" + rspLn);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rspLn;
	}

	public static void main(String[] args) {
		ComServerSocket socket = new ComServerSocket();
		socket.init("10.111.27.5", 8888);
//		String msg = "2910010002 0N291001     OJIB00 EB                                                                                                                                                                                                                                        N0214love                            TFB BOCM                       00021710049416          344021010010196       344+0000000010000+0000000000000+00000000000+00000000000+0000000000000+00000000000+00000000000BOCM              Y1         +0000000000000       +0000000000000       +0000000000000       +0000000000000       +0000000000000       +0000000000000 ";
		String msg = "2910010000 0N291001     OJIB00 EB                                                                                                                                                                                                                                        Y0214love                            TFB BOCM                       00021710049416          344021010010196       344+0000000000200+0000000000000+00000000000+00000000000+0000000000000+00000000000+00000000000ab                Y1         +0000000000000       +0000000000000       +0000000000000       +0000000000000       +0000000000000       +0000000000000 ";
		socket.sendMessage(msg);
		String rsp = socket.receiveMessage();
	//	String rsp = "PRD_STATIC_ENQ_RES	2.0.24	20130313115846	123	SESSION	I	AS71055	test0123	1	PROCESSING_BRANCH~89	00		HK;H;18	ORIENTAL PRESS	東方報業集團		N		MAIN	2000	0.91000		";
		socket.close();
		System.out.println("response message length:\n" + rsp);
		//System.out.println("response message:\n" + rsp);
	}

}