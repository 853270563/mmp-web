package cn.com.yitong.modules.ares.fileBreakUpload.util;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * 文件上传工具类
 */
public class FileUploaderUtil {

	private static Logger logger = Logger.getLogger("FileUploaderUtil");

	/**
	 * 资源关闭
	 */
	private static void closeResources(RandomAccessFile randomAccessFile, InputStream inputStream) {
		try {
			if (randomAccessFile != null) {
				randomAccessFile.close();
			}
		} catch (IOException e) {
			logger.error("FileUploaderUtil randomAccessFile IOException:"+e);
		}
		try {
			if (inputStream != null) {
				inputStream.close();
			}
		} catch (IOException e) {
			logger.error("FileUploaderUtil inputStream IOException:"+e);
		}
	}

	public static void closeResources(RandomAccessFile randomAccessFile) {
		closeResources(randomAccessFile, null);
	}

	public static void closeResources(InputStream inputStream) {
		closeResources(null, inputStream);
	}
}