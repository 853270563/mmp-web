package cn.com.yitong.framework.util.security;

import com.sun.crypto.provider.SunJCE;

import java.security.Security;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

/**
 * 3Des加密解密工具类.
 * Created by lc3@yitong.com.cn on 2014/4/18.
 */
public class DesedeUtil {

    // 缓存.
    private static final Map<String, DesedeCoder> coderMap = new HashMap<String, DesedeCoder>();
    static {
        Security.addProvider(new SunJCE());
    }

    // 加解密辅助类.
    private static class DesedeCoder {
        // Cipher负责完成加密或解密工作
        private Cipher ec;
        private Cipher dc;

        private DesedeCoder(String key) throws Exception {
            byte[] keyData = CoderUtil.hexstr2ByteArr(key);
            byte[] keyByte = keyData;
            if(keyData.length == 16) {
                keyByte = new byte[24];
                System.arraycopy(keyData, 0, keyByte, 0, 16);
                System.arraycopy(keyData, 0, keyByte, 16, 8);
            }
            DESedeKeySpec keySpec = new DESedeKeySpec(keyByte);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
            SecretKey secretKey = keyFactory.generateSecret(keySpec);
            ec = Cipher.getInstance("DESede/ECB/NoPadding");
            ec.init(Cipher.ENCRYPT_MODE, secretKey);
            dc = Cipher.getInstance("DESede/ECB/NoPadding");
            dc.init(Cipher.DECRYPT_MODE, secretKey);
        }

        public byte[] encrypt(byte[] data) throws Exception {
            return ec.doFinal(data);
        }

        public String encrypt(String data) throws Exception {
            return CoderUtil.byteArr2HexStr(ec.doFinal(CoderUtil.hexstr2ByteArr(data)));
        }

        public byte[] decrypt(byte[] data) throws Exception {
            return dc.doFinal(data);
        }

        public String decrypt(String data) throws Exception {
            return CoderUtil.byteArr2HexStr(dc.doFinal(CoderUtil.hexstr2ByteArr(data)));
        }
    }

    /**
     * 3Des加密.
     * @param data 明文字节数组
     * @param key 密key
     * @return 密文字节数组
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, String key) throws Exception {
        return getDesedeCoder(key).encrypt(data);
    }

    /**
     * 3Des加密.
     * @param data 明文16进制字符串
     * @param key 密key
     * @return 密文16进制字符串
     * @throws Exception
     */
    public static String encrypt(String data, String key) throws Exception {
        return getDesedeCoder(key).encrypt(data);
    }

    /**
     * 3Des解密.
     * @param data 密文字节数组
     * @param key 密key
     * @return 明文字节数组
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, String key) throws Exception {
        return getDesedeCoder(key).decrypt(data);
    }

    /**
     * 3Des解密.
     * @param data 密文16进制字符串
     * @param key 密key
     * @return 明文16进制字符串
     * @throws Exception
     */
    public static String decrypt(String data, String key) throws Exception {
        return getDesedeCoder(key).decrypt(data);
    }

    private static DesedeCoder getDesedeCoder(String key) throws Exception {
        DesedeCoder desedeCoder = coderMap.get(key);
        if(null == desedeCoder) {
            synchronized (coderMap) {
                desedeCoder = coderMap.get(key);
                if(null == desedeCoder) {
                    desedeCoder = new DesedeCoder(key);
                    coderMap.put(key, desedeCoder);
                }
            }
        }
        return desedeCoder;
    }

    public static void main(String[] args) {
        String key = "6B93CA2C6F2F41AAE1D6BBF84AB36342";
        String data = "159DFB03941DB4A2";

        System.out.println("=============== 3DES 应用 ===============");
        // 密文：159DFB03941DB4A2 明文：111111 key:6B93CA2C6F2F41AAE1D6BBF84AB36342 账号：6223795317100000115
        System.out.println("密文：" + data);
        try {
            System.out.println("解密后：" + decrypt(data, key));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
