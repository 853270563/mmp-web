package cn.com.yitong.framework.util.security;

/**
 * 密码键盘加解密工具类.
 * Created by lc3@yitong.com.cn on 2014/4/18.
 */
public class CodeKeyboardSecurityUtil {

    /**
     * 密码键盘加密算法.
     * @param pin 6位明文密码
     * @param accno 账号
     * @param key 密key
     * @return
     * @throws Exception
     */
    public static String encrypt(String pin, String accno, String key) throws Exception {
        byte[] encrypt = PinBlockUtil.encrypt(pin, accno);
        return CoderUtil.byteArr2HexStr(DesedeUtil.encrypt(encrypt, key));
    }

    /**
     * 密码键盘解密算法.
     * @param scrData 16进制密文
     * @param accno 账号
     * @param key 密key
     * @return
     * @throws Exception
     */
    public static String decrypt(String scrData, String accno, String key) throws Exception {
        String decrypt = DesedeUtil.decrypt(scrData, key);
        return PinBlockUtil.decrypt(decrypt, accno);
    }

    public static void main(String[] args) {
        System.out.println("============== 加密 ==============");
        String accno = "6223795310100011463";
        String pin = "709103";
        String key = "6B93CA2C6F2F41AAE1D6BBF84AB363426B93CA2C6F2F41AA";
        try {
            System.out.println("明文:" + pin);
            String encrypt = encrypt(pin, accno, key);
            System.out.println("密文:" + encrypt);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("============== 解密 ==============");
        String scr = "9A1C0ECB6AAEE79F";
        try {
            System.out.println("密文:" + scr);
            String encrypt = decrypt(scr, accno, key);
            System.out.println("明文:" + encrypt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
