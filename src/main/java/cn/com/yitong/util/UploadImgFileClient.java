package cn.com.yitong.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

public class UploadImgFileClient {

	private static Log log = LogFactory.getLog(UploadImgFileClient.class);
	protected static int BUFFER = 2048;
	protected Socket client;
	protected DataInputStream dis;
	protected DataOutputStream dos;
	protected volatile static String hostIp;
	protected volatile static int hostPort;

	public UploadImgFileClient(String aHostIp, int aHostPort) {
		hostIp = aHostIp;
		hostPort = aHostPort;
	}

	public UploadImgFileClient() {
	}

	/**
	 * 连接Socket服务器
	 * @return 连接成功返回true 失败返回false
	 */
	public boolean setUpConnection() {
		try {
			boolean isConnected = false;
			client = new Socket(hostIp, hostPort);
			isConnected = client.isConnected();
			if (isConnected) {
				log.info("已成功连接到服务器" + hostIp);
				dis = new DataInputStream(new BufferedInputStream(client.getInputStream()));
				dos = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
				return true;
			}
		} catch (UnknownHostException e) {
			log.error("无法确定主机的IP地址:" + hostIp + e.getMessage());
		} catch (IOException e) {
			log.error("创建套接字时发生I/O错误:" + hostPort + e.getMessage());
		}
		return false;
	}

	/**
	 * 清理资源
	 */
	public void tearDownConnection() {
		try {
			dis.close();
			dos.close();
			client.close();
		} catch (IOException e) {
			log.error("关闭Socket时出现异常 " + e.getMessage());
		}
	}

	/**
	 * 传输影像文件
	 * @return 传输成功返回true 失败返回false
	 * @throws Exception
	 * @throws java.io.IOException
	 */
	public Document transmitFile(String path) throws Exception {
		log.info("开始传输文件...");
		File file = new File(path);
		try {
			DataInputStream fdis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
			Long length=file.length();
			String fileLength=StringUtil.rpadString(length+"", 10);
			log.info("上传到服务器的文件长度:" + fileLength);
			dos.write(fileLength.getBytes());
			dos.flush();

			String fileName=file.getName();
			fileName=StringUtil.rpadString(fileName+"", 30);
			log.info("上传到服务器的文件名:" + fileName);
			dos.write(fileName.getBytes());
			dos.flush();
			int count = -1;
			byte[] data = new byte[BUFFER];
			while ((count = fdis.read(data)) != -1) {
				dos.write(data, 0, count);
			}

			dos.flush();

			SAXReader reader = new SAXReader();
			//System.out.println("-----------" + reader.read(dis).asXML());
			Document doc = reader.read(dis).getDocument();
			dos.close();
			fdis.close(); // add by hp date:09.6.5
			log.info("文件传输完成...");
			//log.info("删除文件:" + file.delete());
			return doc;
		} catch (FileNotFoundException e) {
			log.error("文件上传失败:" + e.getMessage());
		} catch (IOException ioe) {
			ioe.printStackTrace();
			log.error("文件上传失败:" + ioe.getMessage());
		}
		return null;
	}

	/**
	 * 向服务器传输打包后的影像文件
	 * @return 传输成功返回true 失败返回false
	 * @throws Exception
	 */
	public static Document startSocketClient(String zipFilePath) throws Exception {
		System.out.format("[%tc %<tp] %s %n", System.currentTimeMillis(), "初始化Socket上传服务客户端");
		initConfig(); // 初使化
		Document result = null;
		UploadImgFileClient remoteFileClient = new UploadImgFileClient(hostIp, hostPort);
		// 连接服务器成功
		if (remoteFileClient.setUpConnection()) {
			result = remoteFileClient.transmitFile(zipFilePath);
			remoteFileClient.tearDownConnection();
		}
		return result;
	}

	public static void initConfig() {
		hostIp = "10.2.47.211";
		hostPort = 8602;
		System.out.format("[%tc %<tp] %s %n", System.currentTimeMillis(), "Socket服务器IP" + hostIp + ",端口:" + hostPort);
	}
}