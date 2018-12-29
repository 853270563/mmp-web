package cn.com.yitong.core.session.model;

import cn.com.yitong.common.persistence.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 会话数据表
 *
 * @author kwang@yitong.com.cn
 */
public class MhhSession extends BaseEntity {

    /**
     * 会话标识
     */
    private String sessionId;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 最后访问时间
     */
    private Date visitTime;
    /**
     * 失效时间
     */
    private Date invalidTime;
    /**
     * 消息ID
     */
    private String msgId;
    /**
     * 加密后的密钥
     */
    private String key;
    /**
     * 事件ID
     */
    private String eventId;
    /**
     * 用户标识
     */
    private String userId;
    /**
     * 设备标识
     */
    private String deviceId;
    /**
     * 服务器标识
     */
    private String serverIp;
    /**
     * 用户认证状态
     */
    private String authStatus;
    /**
     * 会话数据
     */
    private String data;
    /**
     * 乐观锁
     */
    private Long version;

    /**
     * 消息Id串
     */
    private String msgidSet;

    /**
     * 错误Id
     */
    private String errorId;

    /**
     * 错误信息
     */
    private String errorMsg;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public Date getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(Date visitTime) {
        this.visitTime = visitTime;
    }
    public Date getInvalidTime() {
        return invalidTime;
    }

    public void setInvalidTime(Date invalidTime) {
        this.invalidTime = invalidTime;
    }
    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }
    public String getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(String authStatus) {
        this.authStatus = authStatus;
    }
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorId() {
        return errorId;
    }

    public void setErrorId(String errorId) {
        this.errorId = errorId;
    }

    public String getMsgidSet() {
        return msgidSet;
    }

    public void setMsgidSet(String msgidSet) {
        this.msgidSet = msgidSet;
    }

    public static class TF {

        public static String TABLE_NAME = "ARES_SESSION";   // 表名

        public static String TABLE_SCHEMA = "MMP";   // 库名

        public static String sessionId = "SESSION_ID";  // 会话标识
        public static String createTime = "CREATE_TIME";  // 创建时间
        public static String visitTime = "VISIT_TIME";  // 最后访问时间
        public static String invalidTime = "INVALID_TIME";  // 失效时间
        public static String msgId = "MSG_ID";  // 消息ID
        public static String key = "KEY";  // 加密后的密钥
        public static String eventId = "EVENT_ID";  // 事件ID
        public static String userId = "USER_ID";  // 用户标识
        public static String deviceId = "DEVICE_ID";  // 设备标识
        public static String serverIp = "SERVER_IP";  // 服务器标识
        public static String authStatus = "AUTH_STATUS";  // 用户认证状态
        public static String data = "DATA";  // 会话数据
        public static String version = "VERSION";  //
        public static String msgidSet = "MSGID_SET";
        public static String errorId = "ERROR_ID";
        public static String errorMsg = "ERROR_MSG";
    }
}
