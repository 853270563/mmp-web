package cn.com.yitong.modules.session.model;

import java.util.Date;

public class AresSession {
    /**
     * SESSION_ID
     */
    private String sessionId;

    /**
     * CREATE_TIME
     */
    private Date createTime;

    /**
     * VISIT_TIME
     */
    private Date visitTime;

    /**
     * INVALID_TIME
     */
    private Date invalidTime;

    /**
     * MSG_ID
     */
    private String msgId;

    /**
     * KEY
     */
    private String key;

    /**
     * EVENT_ID
     */
    private String eventId;

    /**
     * USER_ID
     */
    private String userId;

    /**
     * DEVICE_ID
     */
    private String deviceId;

    /**
     * SERVER_IP
     */
    private String serverIp;

    /**
     * AUTH_STATUS
     */
    private String authStatus;

    /**
     * DATA
     */
    private String data;

    /**
     * VERSION
     */
    private Long version;

    /**
     * 
     *
     * @return 
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * 
     *
     * @param 
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * 
     *
     * @return 
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 
     *
     * @param 
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 
     *
     * @return 
     */
    public Date getVisitTime() {
        return visitTime;
    }

    /**
     * 
     *
     * @param 
     */
    public void setVisitTime(Date visitTime) {
        this.visitTime = visitTime;
    }

    /**
     * 
     *
     * @return 
     */
    public Date getInvalidTime() {
        return invalidTime;
    }

    /**
     * 
     *
     * @param 
     */
    public void setInvalidTime(Date invalidTime) {
        this.invalidTime = invalidTime;
    }

    /**
     * 
     *
     * @return 
     */
    public String getMsgId() {
        return msgId;
    }

    /**
     * 
     *
     * @param 
     */
    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    /**
     * 
     *
     * @return 
     */
    public String getKey() {
        return key;
    }

    /**
     * 
     *
     * @param 
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 
     *
     * @return 
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * 
     *
     * @param 
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * 
     *
     * @return 
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 
     *
     * @param 
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 
     *
     * @return 
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * 
     *
     * @param 
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * 
     *
     * @return 
     */
    public String getServerIp() {
        return serverIp;
    }

    /**
     * 
     *
     * @param 
     */
    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    /**
     * 
     *
     * @return 
     */
    public String getAuthStatus() {
        return authStatus;
    }

    /**
     * 
     *
     * @param 
     */
    public void setAuthStatus(String authStatus) {
        this.authStatus = authStatus;
    }

    /**
     * 
     *
     * @return 
     */
    public String getData() {
        return data;
    }

    /**
     * 
     *
     * @param 
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * 
     *
     * @return 
     */
    public Long getVersion() {
        return null != version ? version : 0;
    }

    /**
     * 
     *
     * @param 
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    public static final class FL {
        public static final String sessionId = "SESSION_ID";

        public static final String createTime = "CREATE_TIME";

        public static final String visitTime = "VISIT_TIME";

        public static final String invalidTime = "INVALID_TIME";

        public static final String msgId = "MSG_ID";

        public static final String key = "KEY";

        public static final String eventId = "EVENT_ID";

        public static final String userId = "USER_ID";

        public static final String deviceId = "DEVICE_ID";

        public static final String serverIp = "SERVER_IP";

        public static final String authStatus = "AUTH_STATUS";

        public static final String data = "DATA";

        public static final String version = "VERSION";
    }
}