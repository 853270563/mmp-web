package cn.com.yitong.framework.util.security;

import cn.com.yitong.core.util.SecurityUtils;
import cn.com.yitong.tools.crypto.AESFactory;
import cn.com.yitong.tools.crypto.RSAFactory;
import org.apache.log4j.Logger;

import java.security.Key;

/**
 * 解密处理类
 * @author yaoym
 *
 */
public class SecurityUtil {

	private static Logger logger = Logger.getLogger(SecurityUtil.class);

	private static String split = String.valueOf((char) 29);

	/**
	 * 解密处理，解密成功将返回
	 */
	public static String deEncrypt(String data) {
		logger.info("解密前数据：" + data);
		String resData = null;
		long startDate = System.currentTimeMillis();
		try {
			String[] array = data.split(split);
			if (array.length != 3) {
				logger.debug("内容结构不正确!");
				return null;
			}
			// 数据摘要
			String md5Str = array[0];
			// 加密数据
			resData = array[1];
			// 数字信封
			String keyStr = array[2];

			// 使用私钥解密数字信封
			keyStr = RSACerPlus.getInstance().doDecrypt(keyStr);
			Key k = AESCoder.toKey(keyStr.getBytes());
			// 交易报文解密
			byte[] dataArr = Converts.strToBase64(resData);
			byte[] encryptData = AESCoder.decrypt(dataArr, k);
			resData = new String(encryptData, "utf-8");

			// 对数据摘要生产的报文进行md5校验
			String strRemark = keyStr + resData;
			if (!md5Str.equals(MD5Encrypt.MD5(strRemark))) {
				logger.debug("数据摘要不正确，报文非法!");
				return null;
			}
			long endDate = System.currentTimeMillis();
			logger.debug("解密耗时：" + (endDate - startDate));
			logger.info("解密后数据：" + resData);
		} catch (Exception e) {
			logger.debug("解密发生异常", e);
			e.printStackTrace();
			return null;
		}
		return resData;
	}

	/**
	 * 加密处理
	 */
	public static String encrypt(String data) {
		logger.info("三段式加密---------------------------");
		logger.info("加密前数据：" + data);
		try {
			String key = SecurityUtils.genSecurityKey().substring(0, 16);
			String encryptKey = RSACerPlus.getInstance().doEncrypt(key);

			byte[] dataBytes = data.getBytes("utf-8");
			Key k = AESCoder.toKey(key.getBytes());
			byte[] encriyptDataByte = AESCoder.encrypt(dataBytes, k);
			String dataEncrypt = Base64Coder.encryptBASE64(encriyptDataByte);

			String md5 = MD5Encrypt.MD5(key + data);

			String encriyptData = md5 + split + dataEncrypt + split + encryptKey;
			logger.info("加密后数据：" + encriyptData);
			return encriyptData;
		}catch (Exception e) {
			logger.error("加密异常：" + e.getMessage());
			return "";
		}
	}

	/**
	 * 解密处理，解密成功将返回，三段式，使用ares移动营销RSA秘钥解密
	 */
	public static String deEncryptAres(String data) {
		logger.info("解密前数据：" + data);
		String resData = null;
		long startDate = System.currentTimeMillis();
		try {
			String[] array = data.split(split);
			if (array.length != 3) {
				logger.debug("内容结构不正确!");
				return null;
			}
			// 数据摘要
			String md5Str = array[0];
			// 加密数据
			resData = array[1];
			// 数字信封
			String keyStr = array[2];

			// 使用私钥解密数字信封
			keyStr = RSAFactory.getInstance().decrypt(keyStr, "key");
			// 交易报文解密
			resData = AESFactory.getInstance().decrypt(resData, keyStr);

			// 对数据摘要生产的报文进行md5校验
			String strRemark = keyStr + resData;
			if (!md5Str.equalsIgnoreCase(MD5Encrypt.MD5(strRemark))) {
				logger.debug("数据摘要不正确，报文非法!");
				return null;
			}
			long endDate = System.currentTimeMillis();
			logger.debug("解密耗时：" + (endDate - startDate));
			logger.info("解密后数据：" + resData);
		} catch (Exception e) {
			logger.debug("解密发生异常", e);
			e.printStackTrace();
			return null;
		}
		return resData;
	}

	/**
	 * 加密处理，三段式，使用ares移动营销RSA秘钥加密
	 */
	public static String encryptAres(String data) {
		logger.info("三段式加密---------------------------");
		logger.info("加密前数据：" + data);
		try {
			String key = SecurityUtils.genSecurityKey().substring(0, 16);
			String encryptKey = RSAFactory.getInstance().encrypt(key, "key");

			String dataEncrypt = AESFactory.getInstance().encrypt(data, key);

			String md5 = MD5Encrypt.MD5(key + data);

			String encriyptData = md5.toUpperCase() + split + dataEncrypt + split + encryptKey;
			logger.info("加密后数据：" + encriyptData);
			return encriyptData;
		}catch (Exception e) {
			logger.error("加密异常：" + e.getMessage());
			return "";
		}
	}
}
