package cn.com.yitong.market.mxd.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Map;

/**
 * 文件上传工具类
 * @author hry@yitong.com.cn
 * @date 2015年4月24日
 */
public class FileUploaderUtil {
	/**
	 * 资源关闭
	 */
	private static void closeResources(RandomAccessFile randomAccessFile, InputStream inputStream) {
		try {
			if (randomAccessFile != null) {
				randomAccessFile.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if (inputStream != null) {
				inputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void closeResources(RandomAccessFile randomAccessFile) {
		closeResources(randomAccessFile, null);
	}

	public static void closeResources(InputStream inputStream) {
		closeResources(null, inputStream);
	}

}