package cn.com.yitong.modules.ares.fileBreakUpload.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5BigFileUtil {

	private final static Logger logger = Logger.getLogger(MD5BigFileUtil.class);

	/**
	 * 对一个文件求他的md5值
	 */
	public static String fileMd5(File f) {
		FileInputStream fis = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			fis = new FileInputStream(f);
			// 100KB each time
			byte[] buffer = new byte[102400];
			int length;
			long loopCount = 0;
			while ((length = fis.read(buffer)) != -1) {
				md.update(buffer, 0, length);
				loopCount++;
			}

			logger.debug("read file to buffer loopCount:" + loopCount);

			return new String(Hex.encodeHex(md.digest()));
		} catch (FileNotFoundException e) {
			logger.error("md5 file " + f.getAbsolutePath() + " failed:"
					+ e);
			return "";
		} catch (IOException e) {
			logger.error("md5 file " + f.getAbsolutePath() + " failed:"
					+ e);
			return "";
		} catch (NoSuchAlgorithmException ne) {
			logger.error("NoSuchAlgorithmException: md5", ne);
			return "";
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				logger.error("md5 file fis close failed:" + e);
				return "";
			}
		}
	}

	// 字符串
	public static String md5(String target) {
		return DigestUtils.md5Hex(target);
	}
}
