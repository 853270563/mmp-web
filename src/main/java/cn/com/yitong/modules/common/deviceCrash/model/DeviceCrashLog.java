package cn.com.yitong.modules.common.deviceCrash.model;

import cn.com.yitong.common.utils.ConfigUtils;
import cn.com.yitong.common.persistence.BaseEntity;

/**
 * 
 *
 * @author zhanglong@yitong.com.cn
 */
public class DeviceCrashLog extends BaseEntity {

    /**
     * 主键标识
     */
    private String logId;
    /**
     * 设备Id
     */
    private String deviceId;
    /**
     * 设备型号
     */
    private String deviceModel;
    /**
     * 系统类型+版本
     */
    private String deviceSystem;
    /**
     * 应用ID
     */
    private String appId;
    /**
     * 版本编号
     */
    private String appVersNo;
    /**
     * 用户Id
     */
    private String userId;
    /**
     * 会话ID
     */
    private String sessionId;
    /**
     * 日志级别
     */
    private String logLevel;
    /**
     * 日志记录时间
     */
    private String logTime;
    /**
     * 错误堆栈
     */
    private String errorInfo;
    /**
     * 入库时间
     */
    private String createTime;

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }
    public String getDeviceSystem() {
        return deviceSystem;
    }

    public void setDeviceSystem(String deviceSystem) {
        this.deviceSystem = deviceSystem;
    }
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
    public String getAppVersNo() {
        return appVersNo;
    }

    public void setAppVersNo(String appVersNo) {
        this.appVersNo = appVersNo;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }
    public String getLogTime() {
        return logTime;
    }

    public void setLogTime(String logTime) {
        this.logTime = logTime;
    }
    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public static class TF {

        public static String TABLE_NAME = "DEVICE_CRASH_LOG";   // 表名

        public static String TABLE_SCHEMA = ConfigUtils.getValue("schema.interPlat");   // 库名

        public static String logId = "LOG_ID";  // 主键标识
        public static String deviceId = "DEVICE_ID";  // 设备Id
        public static String deviceModel = "DEVICE_MODEL";  // 设备型号
        public static String deviceSystem = "DEVICE_SYSTEM";  // 系统类型+版本
        public static String appId = "APP_ID";  // 应用ID
        public static String appVersNo = "APP_VERS_NO";  // 版本编号
        public static String userId = "USER_ID";  // 用户Id
        public static String sessionId = "SESSION_ID";  // 会话ID
        public static String logLevel = "LOG_LEVEL";  // 日志级别
        public static String logTime = "LOG_TIME";  // 日志记录时间
        public static String errorInfo = "ERROR_INFO";  // 错误堆栈
        public static String createTime = "CREATE_TIME";  // 入库时间

    }
}
