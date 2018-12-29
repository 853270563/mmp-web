package cn.com.yitong.core.util;

import cn.com.yitong.common.utils.ConfigUtils;
import cn.com.yitong.common.utils.StringUtils;
import cn.com.yitong.core.session.Session;
import cn.com.yitong.core.session.util.SessionManagerUtils;
import cn.com.yitong.core.web.util.LoginCodecUtils;
import cn.com.yitong.core.web.util.NetCodecUtils;
import cn.com.yitong.framework.util.security.SecurityUtil;
import cn.com.yitong.tools.crypto.ICodec;
import cn.com.yitong.tools.crypto.RSAFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Token 生成工具类
 * @author lc3@yitong.com.cn
 */
public class SecurityUtils {

    /**
     * 加解密类型：
     *  0   不加解密
     *  1   默认提供加解密
     *  其他数字    扩展加解密，需要注册后使用
     */
    private static int CODEC_TYPE = 0;
    /**
     * 注册的加解密
     */
    private static final Map<Integer, ICodec> registerCodecs = new HashMap<Integer, ICodec>();

    /**
     * 请求是否解密标识
     */
    private static final String HAS_CODED_KEY = "HAS_CODED";

    protected static final Logger logger = LoggerFactory.getLogger(SecurityUtils.class);

    /**
     * 注册扩展的加解密类型，不允许注册系统类型：0和1
     * @param codecType 不为0和1的整形值
     * @param codec 加解密编码器
     */
    public static void registerCodec(int codecType, ICodec codec) {
        if(isSysCodecType(codecType)) {
            throw new IllegalArgumentException("0和1为系统加解密类型，不允许注册");
        }
        if(null == getCodecByType(codecType)) {
            synchronized (registerCodecs) {
                if(null == getCodecByType(codecType)) {
                    registerCodecs.put(codecType, codec);
                    if (logger.isInfoEnabled()) {
                        logger.info("成功注册加解密类型[" + codecType + "]:" + codec.getClass().getName());
                    }
                    return;
                }
            }
        }
        if(logger.isWarnEnabled()) {
            logger.warn("些加解密类型已经注册，无需重复注册:" + codecType);
        }
    }

    /**
     * 删除现有注册的加解密类型
     * @param codecType 加解密类型
     * @return
     */
    public static ICodec removeCodec(int codecType) {
        ICodec remove = null;
        if(null != getCodecByType(codecType)) {
            synchronized (registerCodecs) {
                remove = registerCodecs.remove(codecType);
            }
        }
        if(logger.isInfoEnabled()) {
            logger.info("成功取消注册加解密类型:" + codecType);
        }
        return remove;
    }

    /**
     * 判断是否为系统加解密类型
     * @param codecType 加解密类型
     * @return
     */
    private static boolean isSysCodecType(int codecType) {
        return 0 == codecType || 1 == codecType || 2 == codecType;
    }

    /**
     * 设置当前系统的加解密类型
     * @param codecType 加解密类型
     */
    public static void setCodecType(int codecType) {
        if(isSysCodecType(codecType) || null != getCodecByType(codecType)) {
            CODEC_TYPE = codecType;
        } else {
            throw new IllegalArgumentException("设置的加解密类型不能识别，请注册后再重试:" + codecType);
        }
    }

    /**
     * 通过加解密类型获得加解密类
     * @param codecType 加解密类型
     * @return
     */
    public static ICodec getCodecByType(int codecType) {
        return registerCodecs.get(codecType);
    }

    /**
     * 生成一个新的随机Token
     * @return Token串
     */
    public static String genToken() {
        String token = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
        while (null != SessionManagerUtils.getDefaultManager().getSession(token)) {
            token = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
        }
        return token;
    }

    /**
     * 格式化Token，格式化成标准长度
     * @param token token
     * @return 格式化后的token
     */
    public static String formatToken(String token) {
        int tokenLen = ConfigUtils.getValue(ConfigName.SESSION_TOKEN_LENGTH,
                ConfigName.SESSION_TOKEN_LENGTH_DEFVAL);
        if(null == token) {
            return StringUtils.leftPad("", tokenLen, '0');
        }
        if(tokenLen != token.length()) {
            if(token.length() < tokenLen) {
                token = StringUtils.leftPad(token, tokenLen, '0');
            } else {
                token = token.substring(0, tokenLen);
            }
        }
        return token;
    }

    /**
     * 得到当前会话的Token
     * @return
     */
    public static String getToken() {
        return (String) getSessionRequired().getId();
    }

    /**
     * 生成一个新的密Key
     * @return 密Key
     */
    public static String genSecurityKey() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 得到当前会话密Key，不存在会报异常
     * @return
     */
    public static String getSecurityKeyRequired() {
        String skey = getSecurityKey();
        if(null == skey) {
            throw new IllegalArgumentException("当前会话没有正常登录");
        }
        return skey;
    }

    /**
     * 得到当前会话密Key
     * @return
     */
    public static String getSecurityKey() {
        String skey = getSessionRequired().getSkey();
        return null == skey ? null : RSAFactory.getDefaultCodec().decrypt(skey);
    }

    /**
     * 得到当前Session，不存在会报异常
     * @return
     */
    public static Session getSessionRequired() {
        Session session = ThreadContext.getSession(true);
        if(null == session) {
            throw new IllegalArgumentException("当前线程没有上下文环境");
        }
        return session;
    }

    /**
     * 得到当前Session
     * @return
     */
    public static Session getSession() {
        return ThreadContext.getSession(false);
    }

    /**
     * 判断当前操作是否为登录
     * @return
     */
    public static boolean isLogining() {
        return ThreadContext.isLogining();
    }

    /**
     * 判断当前是否检查消息Id
     * @return
     */
    public static boolean isCheckMsgId() {
        return ThreadContext.isCheckMsgId();
    }

    /**
     * 判断是否需要加解密
     * @return
     */
    public static boolean canCodec() {
        return 0 != getCurrentCodecType();
    }

    /**
     * 得到当前操作的加解密类型
     * @return
     */
    public static int getCurrentCodecType() {
        Integer codecType = ThreadContext.getCodecType();
        if(null != codecType) {
            return codecType;
        } else {
            return CODEC_TYPE;
        }
    }

    /**
     * 得到当前请求的msgId
     * @return
     */
    public static String getRequestMessageId() {
        return ThreadContext.getRequestMessageId();
    }

    /**
     * 根据秘钥长度配置获取key的长度
     * @return
     */
    public static int getAesKeyLength() {
        int keyLength = ConfigUtils.getValue(ConfigName.AES_KEY_LENGTH, Integer.class, ConfigName.AES_KEY_LENGTH_DEFAULT);
        return keyLength/8;
    }

    /**
     * 加密通讯数据
     * @param data 通讯数据
     * @return
     */
    public static String encode(String data) {
        if(!canCodec() || null == ThreadContext.get(HAS_CODED_KEY)) {
            return data;
        }
        if(logger.isInfoEnabled()) {
            logger.info("加密前的返回报文为：" + data);
        }
        int codecType = getCurrentCodecType();
        if(1 == codecType) {
            //ARES 平台加密
            if (isLogining()) {
                data = LoginCodecUtils.loginEncode(data);
            } else {
                data = NetCodecUtils.encode(data);
            }
        }else if(2 == codecType) {
            // 手机银行 三段式加密
//            data = SecurityUtil.encrypt(data);

            // 移动营销 三段式加密
            data = SecurityUtil.encryptAres(data);
        } else {
            data = getCodecByType(codecType).encrypt(data);
        }
        if(logger.isDebugEnabled()) {
            logger.debug("加密后的返回报文为：" + data);
        }
        return data;
    }

    /**
     * 解密通讯数据
     * @param data 通讯数据
     * @return
     */
    public static String decode(String data) {
        if(!canCodec()) {
            return data;
        }
        if(logger.isDebugEnabled()) {
            logger.debug("解密前的请求报文为：" + data);
        }
        ThreadContext.put(HAS_CODED_KEY, true);
        int codecType = getCurrentCodecType();
        if(1 == codecType) {
            //Ares 平台解密
            if (isLogining()) {
                data = LoginCodecUtils.loginDecode(data);
            } else {
                data = NetCodecUtils.decode(data);
            }
        }else if(2 == codecType) {
            //手机银行 三段式解密
//            data = SecurityUtil.deEncrypt(data);

            //移动营销 三段式解密
            data = SecurityUtil.deEncryptAres(data);
        } else {
            data = getCodecByType(codecType).decrypt(data);
        }
        if(logger.isInfoEnabled()) {
            logger.info("解密后的请求报文为：" + data);
        }
        return data;
    }
}
