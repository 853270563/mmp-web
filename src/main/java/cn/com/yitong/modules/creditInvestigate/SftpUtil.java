package cn.com.yitong.modules.creditInvestigate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.servlet.ServerInit;

public class SftpUtil {
	private static Logger logger = LoggerFactory.getLogger(SftpUtil.class);
	public static final String FTP_DOWN_HOST = ServerInit
			.getString("FTP_DOWN_HOST");
	public static final String FTP_DOWN_PORT = ServerInit
			.getString("FTP_DOWN_PORT");
	public static final String FTP_DOWN_USER_NAME = ServerInit
			.getString("FTP_DOWN_USER_NAME");
	public static final int FTP_DOWN_USER_PW = ServerInit
			.getInt("FTP_DOWN_USER_PW");
	static String DOWNLOAD_URL = ServerInit.getString("DOWNLOAD_URL");
	private String username;
	private String identity;
	private String host;
	private int port;

	private Session sshSession = null;
	private ChannelSftp sftp = null;

	public ChannelSftp getSftp() {
		return sftp;
	}

	public SftpUtil(String host, int sftpPort, String username, String identity) {
		this.username = username;
		this.identity = identity;
		this.host = host;
		this.port = sftpPort;
	}

	public void connect() throws Exception {
		JSch jsch = new JSch();

		sshSession = jsch.getSession(username, host, port);
		if (null == sshSession) {
			throw new Exception("session is null");
		}
		sshSession.setPassword(identity);
		sshSession.setConfig("StrictHostKeyChecking", "no");
		sshSession.connect(30000);

		Channel channel = sshSession.openChannel("sftp");
		channel.connect();
		sftp = (ChannelSftp) channel;
		logger.info("Connected to " + host + ".");
	}

	public void disconnect() {
		if (sftp != null) {
			sftp.disconnect();
		}
		if (sshSession != null) {
			sshSession.disconnect();
		}
	}

	/*
	 * 从SFTP服务器下载文件
	 * 
	 * @param ftpHost SFTP IP地址
	 * 
	 * @param ftpUserName SFTP 用户名
	 * 
	 * @param ftpPassword SFTP用户名密码
	 * 
	 * @param ftpPort SFTP端口
	 * 
	 * @param ftpPath SFTP服务器中文件所在路径 格式： ftptest/aa
	 * 
	 * @param localPath 下载到本地的位置 格式：H:/download
	 * 
	 * @param fileName 文件名称
	 */
	public static void downloadSftpFile(String ftpHost, String ftpUserName,
			String ftpPassword, int ftpPort, String ftpPath, String localPath,
			Map<String, Object> map) throws JSchException {
		Session session = null;
		Channel channel = null;
		JSch jsch = new JSch();
		session = jsch.getSession(ftpUserName, ftpHost, ftpPort);
		session.setPassword(ftpPassword);
		session.setTimeout(100000);
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.connect();
		channel = session.openChannel("sftp");
		channel.connect();
		ChannelSftp chSftp = (ChannelSftp) channel;
		// String ftpFilePath = ftpPath + File.separatorChar +fileName;
		// String localFilePath = localPath + File.separatorChar + fileName;
		try {
			logger.info("从ftp下载的为路径为:{},本地的路径为:{}", ftpPath, localPath);
			chSftp.get(ftpPath, localPath);
		} catch (Exception e) {
			try {
				logger.info("尝试GBK编码路径");
				logger.info("从ftp下载的为路径GBK编码为:{},本地的路径GBK编码为:{}", new String(ftpPath.getBytes("UTF-8"), "GBK"), new String(localPath.getBytes("UTF-8")), "GBK");
				chSftp.get(new String(ftpPath.getBytes("UTF-8"), "GBK"), new String(localPath.getBytes("UTF-8"), "GBK"));
			} catch (Exception e1) {
				logger.error(e.getMessage());
				map.put("fullPath", "error");
			}
		} finally {
			chSftp.quit();
			channel.disconnect();
			session.disconnect();
		}

	}

	/**
	 * 下载单个文件
	 * 
	 * @param codeString
	 *            下载目录
	 * 
	 * @param downloadFile
	 *            下载的文件
	 * 
	 * @throws Exception
	 */
	public String download(String downloadFile, String codeString)
				throws Exception {
			// 存在本地服务器的路径
			// String localServerPath = AppConstants.UPLOAD_FILES_PATH +
			// "/download_files/";
			// 存在本地调试路径
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String str = sdf.format(date);
			String localServerPath = AppConstants.upload_files_path
					+ File.separator + "yxPic" + File.separator + codeString
					+ File.separator + str + File.separator;
			File file = new File(localServerPath);
			// 目标目录不存在时，创建该文件夹
			if (!file.exists()) {
				file.mkdirs();
			}
			this.sftp.get(downloadFile, "C:\\Users\\hasee");
			return localServerPath;
	}

	public static int downloadFileTolocal(String fullPath, String docNo, String attachmentNo) {
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;
		FileOutputStream fileOutputStream = null;
		int statusCode = 0;
		try {
			httpclient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(DOWNLOAD_URL + "?docNo=" + docNo + "&attachmentNo=" + attachmentNo);
			logger.info("从信贷系统下载文件：{}", httpget.getURI());
			// 执行get请求.      
			response = httpclient.execute(httpget);
			// 获取响应实体      
			HttpEntity entity = response.getEntity();
			statusCode = response.getStatusLine().getStatusCode();
			// 打印响应状态      
			if (entity != null && statusCode == HttpStatus.SC_OK) {
				fileOutputStream = new FileOutputStream(fullPath);
				entity.writeTo(fileOutputStream);
			} else {
				logger.error("从信贷系统下载文件错误，响应状态码:{}", statusCode);
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				if (fileOutputStream != null) {

					fileOutputStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage(), e);
			}
			}
		try {
			if (response != null) {

				response.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
			try {
			if (httpclient != null) {
				httpclient.close();
			}
			} catch (IOException e) {
			logger.error(e.getMessage(), e);
			}

		return statusCode;
	}

	public static void main(String[] args) throws Exception {
		SftpUtil sftpUtil = new SftpUtil("198.198.200.117", 22, "root", "root123");
		sftpUtil.connect();
		sftpUtil.disconnect();
		//sftpUtil.download("/opt/weblogic/upload-files/file/jiE5lg32eR5Zbkph.jpg", "luanyu");
	}
}
