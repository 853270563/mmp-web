package cn.com.yitong.portal.thirdApp.model;

import cn.com.yitong.common.utils.ConfigUtils;
import cn.com.yitong.common.persistence.BaseEntity;
import cn.com.yitong.tools.crypto.RSAFactory;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * 授权许可表
 *
 * @author lc3@yitong.com.cn
 */
public class AresAuthority extends BaseEntity {

    /**
     * 授权许可码
     */
    private String authCode;
    /**
     * 授权许可码状态
     */
    private String codeStatus;
    /**
     * 授权许可码创建时间
     */
    private Date codeCreateTime;
    /**
     * 通信秘钥
     */
    private String secretKey;
    /**
     * 门户会话标识
     */
    private String portalSessionId;
    /**
     * 用户标识
     */
    private String userId;
    /**
     * 应用标识
     */
    private String appId;
    /**
     * 访问令牌与刷新令牌创建时间
     */
    private Date createTime;
    /**
     * 访问令牌
     */
    private String accessToken;
    /**
     * 令牌上次访问时间
     */
    private Date lastAccessTime;
    /**
     * 刷新令牌
     */
    private String freshToken;
    /**
     * 刷新令牌上次访问时间
     */
    private Date freshLastAccessTime;

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
    public String getCodeStatus() {
        return codeStatus;
    }

    public void setCodeStatus(String codeStatus) {
        this.codeStatus = codeStatus;
    }
    public Date getCodeCreateTime() {
        return codeCreateTime;
    }

    public void setCodeCreateTime(Date codeCreateTime) {
        this.codeCreateTime = codeCreateTime;
    }
    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
    public String getSecretKeyWithNoCodec() {
        return null == secretKey ? null : RSAFactory.getDefaultCodec().decrypt(secretKey);
    }

    public void setSecretKeyWithNoCodec(String secretKey) {
        Assert.notNull(secretKey, "secretKey不能为空");
        this.secretKey = RSAFactory.getDefaultCodec().encrypt(secretKey);
    }
    public String getPortalSessionId() {
        return portalSessionId;
    }

    public void setPortalSessionId(String portalSessionId) {
        this.portalSessionId = portalSessionId;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(Date lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }
    public String getFreshToken() {
        return freshToken;
    }

    public void setFreshToken(String freshToken) {
        this.freshToken = freshToken;
    }
    public Date getFreshLastAccessTime() {
        return freshLastAccessTime;
    }

    public void setFreshLastAccessTime(Date freshLastAccessTime) {
        this.freshLastAccessTime = freshLastAccessTime;
    }

    public static class TF {

        public static String TABLE_NAME = "ARES_AUTHORITY";   // 表名

        public static String TABLE_SCHEMA = ConfigUtils.getValue("schema.configPlat");   // 库名

        public static String authCode = "AUTH_CODE";  // 授权许可码
        public static String codeStatus = "CODE_STATUS";  // 授权许可码状态
        public static String codeCreateTime = "CODE_CREATE_TIME";  // 授权许可码创建时间
        public static String secretKey = "SECRET_KEY";  // 通信秘钥
        public static String portalSessionId = "PORTAL_SESSION_ID";  // 门户会话标识
        public static String userId = "USER_ID";  // 用户标识
        public static String appId = "APP_ID";  // 应用标识
        public static String createTime = "CREATE_TIME";  // 创建时间
        public static String accessToken = "ACCESS_TOKEN";  // 访问令牌
        public static String lastAccessTime = "LAST_ACCESS_TIME";  // 令牌上次访问时间
        public static String freshToken = "FRESH_TOKEN";  // 刷新令牌
        public static String freshLastAccessTime = "FRESH_LAST_ACCESS_TIME";  // 刷新令牌上次访问时间

    }
}
