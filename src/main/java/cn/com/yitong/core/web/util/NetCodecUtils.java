package cn.com.yitong.core.web.util;

import cn.com.yitong.core.session.Session;
import cn.com.yitong.core.util.SecurityUtils;
import cn.com.yitong.tools.crypto.AESFactory;
import cn.com.yitong.util.StringUtil;

/**
 * 会话中通讯加解密工具类
 * @author lc3@yitong.com.cn
 */
public class NetCodecUtils {

    private NetCodecUtils() {}

    /**
     * 加密通讯数据
     * @param data 通讯数据
     * @return
     */
    public static String encode(String data) {
        String securityKey = SecurityUtils.getSecurityKey();
        if(null == securityKey || securityKey.isEmpty()) {
            return data;
        } else {
            return AESFactory.getInstance().encrypt(data, securityKey);
        }
    }

    /**
     * 解密通讯数据
     * @param data 通讯数据
     * @return
     */
    public static String decode(String data) {
        if(null == data || data.length() < 32) {
            return null;
        }
        Session session = SecurityUtils.getSessionRequired();
        session.setMsgId(data.substring(0, 16));
        if(SecurityUtils.isCheckMsgId()) {
            if(StringUtil.isEmpty(session.getMsgidSet()) || "null".equals(session.getMsgidSet())) {
                session.setMsgidSet(session.getMsgId());
            }else {
                session.setMsgidSet(session.getMsgidSet() + session.getMsgId());
            }
        }
        return AESFactory.getInstance().decrypt(data.substring(32), SecurityUtils.getSecurityKeyRequired());
    }

}
