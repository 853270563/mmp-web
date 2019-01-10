package cn.com.yitong.framework.util.security;

import org.apache.log4j.Logger;

import java.security.Key;

/**
 * 解密处理类
 * @author yaoym
 *
 */
public class SecurityUtil {
	private static Logger logger = Logger.getLogger(SecurityUtil.class);

	/**
	 * 解密处理，解密成功将返回
	 * 
	 * @param data
	 * @return
	 */
	public static String deEncrypt(String data) {
		String resData = null;
		long startDate = System.currentTimeMillis();
		try {
			String[] array = data.split((char) (29) + "");
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
		} catch (Exception e) {
			logger.debug("解密发生异常", e);
			e.printStackTrace();
			return null;
		}
		return resData;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
//		Key k = AESCoder.toKey("AAAAAAAAAAAAAAAA".getBytes());
//		String desData = "FFHiQrBeZN/pt5iYD7EQsot0aYKkq1zPwVkR9/V3iBY=";
//		byte[] encryptData = AESCoder.decrypt(Converts.strToBase64(desData), k);
//		desData = new String(encryptData);
//		System.out.println(desData);
		
		String key16 = "Wa1sCetUp9ibrunA";
		String content = "abc";
		
		byte[] b = AESCoder.encrypt(content.getBytes("UTF-8"), key16.getBytes("UTF-8"));
		System.out.println(Base64Coder.encryptBASE64(b));
		
		
		
		String eString = "GyhW685fA2TyGzsWmj5vnQ==";
		
	System.out.print(new String(AESCoder.decrypt(Base64Coder.decryptBASE64(eString), key16.getBytes("UTF-8")),"UTF-8"));
		
		
	}

}
