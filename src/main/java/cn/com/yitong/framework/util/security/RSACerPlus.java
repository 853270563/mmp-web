package cn.com.yitong.framework.util.security;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

/**
 * 用户服务器端对客户端提交的加密数据使用私钥进行解密 公钥加密--客户端 私钥解密--服务端
 * 
 * @author yym
 * 
 */
public class RSACerPlus {
	private static Logger logger = Logger.getLogger(RSACerPlus.class);
	private Cipher cipher;
	// 数字证书
	private static final String keystoreFilePath = "pnc_mmp.jks";
	// 公钥--客户端加密码时使用
	private static final String publickeyFilePath = "pnc_mmp.cer";
	// 证书密码
	private static final String storepass = "server_rsa";
	// 私钥密码
	private static final String keypass = "server_rsa";
	// 私钥别名
	private static final String keyalias = "yitong";

	private static RSACerPlus rsaPlus = null;

	String rootpath = null;

	private RSACerPlus() {
		rootpath = RSACerPlus.class.getResource("RSACerPlus.class").getPath();
		// rootpath = rootpath.substring(0, rootpath.indexOf("cn"));
		rootpath = rootpath.substring(0, rootpath.indexOf("WEB-INF") + 8);
	}

	public static RSACerPlus getInstance() {
		if (null == rsaPlus) {
			rsaPlus = new RSACerPlus();
			try {
				rsaPlus.initCer();
			} catch (Exception e) {
				logger.error("init the cer ERROR!", e);
			}
		}
		return rsaPlus;
	}

	/**
	 * 初始化加载cer证书
	 * 
	 * @throws Exception
	 */
	private void initCer() throws Exception {
		String filePath = rootpath + "config/" + keystoreFilePath;
		//String filePath = "c:/" + keystoreFilePath;
		FileInputStream fis2 = new FileInputStream(filePath);
		KeyStore ks = KeyStore.getInstance("JKS"); // 加载证书库
		char[] kspwd = storepass.toCharArray(); // 证书库密码
		char[] keypwd = keypass.toCharArray(); // 证书密码
		ks.load(fis2, kspwd); // 加载证书
		PrivateKey pk2 = (PrivateKey) ks.getKey(keyalias, keypwd); // 获取证书私钥
		fis2.close();
		// cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher = Cipher.getInstance("RSA");
		// 解密模式
		cipher.init(Cipher.DECRYPT_MODE, pk2);
	}

	/**
	 * 使用初始化的公钥对数据加密
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 *             : IllegalBlockSizeException, BadPaddingException,
	 *             UnsupportedEncodingException
	 */
	public String doEncrypt(String str) throws Exception {
		CertificateFactory cff = CertificateFactory.getInstance("X.509");

		String filePath = rootpath + "config/" + publickeyFilePath;
		InputStream in = new FileInputStream(filePath);// 证书文件
		Certificate cf = cff.generateCertificate(in);
		PublicKey pk1 = cf.getPublicKey(); // 得到证书文件携带的公钥
		Cipher cipher = Cipher.getInstance("RSA");
		// 加密模式
		cipher.init(Cipher.ENCRYPT_MODE, pk1);
		byte[] msg1 = cipher.doFinal(str.getBytes("UTF-8")); // 加密后的数据
		return new BASE64Encoder().encode(msg1);
	}

	/**
	 * 解密字符串
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 *             :IllegalStateException, IllegalBlockSizeException,
	 *             BadPaddingException, IOException
	 */
	public String doDecrypt(String str) throws Exception {
		byte[] msg = new BASE64Decoder().decodeBuffer(str);
		StringBuffer sb = new StringBuffer();
		try {
			for (int i = 0; i < msg.length; i += 128) {
				byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(msg, i,
						i + 128));
				sb.append(new String(doFinal, "UTF-8"));
			}
		} catch (Exception e) {
			initCer();
			logger.debug("解密数据失败!", e);
			throw e;
		}
		return sb.toString();
	}

	public static void main(String[] args) throws Exception {
		RSACerPlus rcp = RSACerPlus.getInstance();
		String oldStr = "----中国-----";
		System.out.println("oldStr:" + oldStr);
		String chgStr = rcp.doEncrypt(oldStr);
		System.out.println("chgStr:" + chgStr);
		String newStr = rcp.doDecrypt(chgStr);
		System.out.println("newStr:" + newStr);
	}
}
