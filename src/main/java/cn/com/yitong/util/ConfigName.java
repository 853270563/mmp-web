package cn.com.yitong.util;

import cn.com.yitong.common.utils.ConfigUtils;

/**
 * @author lc3@yitong.com.cn
 */
public class ConfigName {

    /**
     * 授权码有效时间
     */
    public static final String SESSION_THIRD_AUTH_CODE_TIMEOUT = "session.third_auth_code_timeout";
    public static final int SESSION_THIRD_AUTH_CODE_TIMEOUT_DEFVAL = 600;   // 单位秒

    /**
     * 第三方会话刷新令牌有效时间
     */
    public static final String SESSION_THIRD_REFRESH_TOKEN_TIMEOUT = "session.third_refresh_token_timeout";
    public static int SESSION_THIRD_REFRESH_TOKEN_TIMEOUT_DEFVAL() {
        return ConfigUtils.getValue(cn.com.yitong.core.util.ConfigName.SESSION_TIMEOUT_SECOND,
                cn.com.yitong.core.util.ConfigName.SESSION_TIMEOUT_SECOND_DEFVAL);
    }

    /**
     * 第三方会话访问令牌有效时间
     */
    public static final String SESSION_THIRD_ACCESS_TOKEN_TIMEOUT = "session.third_access_token_timeout";
    public static int SESSION_THIRD_ACCESS_TOKEN_TIMEOUT_DEFVAL() {
        return ConfigUtils.getValue(SESSION_THIRD_REFRESH_TOKEN_TIMEOUT,
                SESSION_THIRD_REFRESH_TOKEN_TIMEOUT_DEFVAL());
    }

}
