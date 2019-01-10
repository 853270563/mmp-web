package cn.com.yitong.core.web.util;

import java.util.HashMap;
import java.util.Map;

import cn.com.yitong.core.session.Session;
import cn.com.yitong.core.util.SecurityUtils;
import cn.com.yitong.tools.crypto.AESFactory;
import cn.com.yitong.tools.crypto.RSAFactory;
import cn.com.yitong.util.StringUtil;

/**
 * 登陆加解密工具类
 * @author lc3@yitong.com.cn
 */
public class LoginCodecUtils {

    /**
     * 返回客户端的token对应的键
     */
    public static final String CLIENT_TOKEN_KEY = "token";
    /**
     * 返回客户端的密钥对应的键
     */
    public static final String CLIENT_SECURITY_KEY = "skey";

    private LoginCodecUtils(){}

    /**
     * 登陆请求数据解密
     * @param loginData 登陆数据
     * @return
     */
    public static String loginDecode(String loginData) {
        if(null == loginData || 48 > loginData.length()) {
            throw new IllegalArgumentException("登陆请求数据为空，或格式不合法");
        }
        loginData = RSAFactory.getDefaultCodec().decrypt(loginData);
        String msgId = loginData.substring(0, 16);  // 消息ID
        String uuid = loginData.substring(16, 32);  // 设备UUID
        String data = loginData.substring(32);      // Json数据

        Session session = SecurityUtils.getSessionRequired();
        session.setMsgId(msgId);
        if(SecurityUtils.isCheckMsgId()) {
            session.setMsgidSet(msgId);
        }
        session.setDeviceCode(uuid);
        return data;
    }

    /**
     * 补充登陆返回相关的数据
     * @param rst 登陆返回的Map数据，可为空
     * @return
     */
    public static Map<String, Object> returnLoginData(Map<String, Object> rst) {
        if(null == rst) {
            rst = new HashMap<String, Object>();
        }
        String skey = SecurityUtils.genSecurityKey();
        SecurityUtils.getSessionRequired().setSkey(RSAFactory.getDefaultCodec().encrypt(skey));
        rst.put(CLIENT_SECURITY_KEY, skey);
        if(null == rst.get(CLIENT_TOKEN_KEY)) {
            rst.put(CLIENT_TOKEN_KEY, SecurityUtils.getToken());
        }
        return rst;
    }

    /**
     * 登陆返回结果加密
     * @param rsData 待加密数据
     * @return
     */
    public static String loginEncode(String rsData) {
        String key = getLoginKey();
        return AESFactory.getInstance().encrypt(rsData, key);
    }

    /**
     * 获取密Key
     * @return
     */
    private static String getLoginKey() {
        Session session = SecurityUtils.getSessionRequired();
        String msgId = session.getMsgId();
        String uuid = session.getDeviceCode();
        if(null == msgId || null == uuid) {
            throw new IllegalArgumentException("会话没有正常安全加密登陆");
        }
        return msgId + uuid;
    }
}
