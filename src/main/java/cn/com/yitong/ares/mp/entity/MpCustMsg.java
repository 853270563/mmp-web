package cn.com.yitong.ares.mp.entity;

import cn.com.yitong.common.persistence.BaseEntity;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 客户消息表
 *
 * @author 孙伟(sunw@yitong.com.cn)
 */
public class MpCustMsg extends BaseEntity {

    /**
     * 消息ID
     */
    private String msgId;
    /**
     * 任务ID
     */
    private String taskId;
    /**
     * 通道类型，数据字典：0-手机银行，2-微信
     */
    private String chanType;
    /**
     * 推送类型，数据字典：0-消息，1-扩展
     */
    private String pushType;
    /**
     * 客户端应用标识
     */
    private String clientAppId;
    /**
     * 终端类型，数据字典：0-iOS，1-Android，2-WP
     */
    private String clientType;
    /**
     * 设备标识
     */
    private String deviceId;
    /**
     * 终端标识(令牌)
     */
    private String clientId;
    /**
     * 签约客户号
     */
    private String custId;
    /**
     * 消息标题
     */
    private String msgTitle;
    /**
     * 消息内容
     */
    private String msgBody;
    /**
     * 扩展内容
     */
    private String extBody;
    /**
     * 干扰值
     */
    private BigDecimal distVal;
    /**
     * 优先级
     */
    private BigDecimal prioVal;
    /**
     * 点击通知动作类型，0-打开应用，1-打开网址，2-自定义
     */
    private String clickActType;
    /**
     * 点击通知动作内容
     */
    private String clickActBody;
    /**
     * 发送开始时间
     */
    private Date sendBeginTime;
    /**
     * 发送结束时间
     */
    private Date sendEndTime;
    /**
     * 发送时间
     */
    private Date sendTime;
    /**
     * 发送次数
     */
    private BigDecimal sendTimes;
    /**
     * 耗时(毫秒)
     */
    private BigDecimal takeTime;
    /**
     * 状态，0-未发送，1-发送中，2-发送成功，3-发送失败
     */
    private String status;
    /**
     * 删除标记，0-未删除，1-已删除
     */
    private String delFlag;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 错误信息
     */
    private String errMsg;
    /**
     * 消息类型
     */
    private String msgType;
    
    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    public String getChanType() {
        return chanType;
    }

    public void setChanType(String chanType) {
        this.chanType = chanType;
    }
    public String getPushType() {
        return pushType;
    }

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }
    public String getClientAppId() {
        return clientAppId;
    }

    public void setClientAppId(String clientAppId) {
        this.clientAppId = clientAppId;
    }
    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }
    public String getMsgTitle() {
        return msgTitle;
    }

    public void setMsgTitle(String msgTitle) {
        this.msgTitle = msgTitle;
    }
    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }
    public String getExtBody() {
        return extBody;
    }

    public void setExtBody(String extBody) {
        this.extBody = extBody;
    }
    public BigDecimal getDistVal() {
        return distVal;
    }

    public void setDistVal(BigDecimal distVal) {
        this.distVal = distVal;
    }
    public BigDecimal getPrioVal() {
        return prioVal;
    }

    public void setPrioVal(BigDecimal prioVal) {
        this.prioVal = prioVal;
    }
    public String getClickActType() {
        return clickActType;
    }

    public void setClickActType(String clickActType) {
        this.clickActType = clickActType;
    }
    public String getClickActBody() {
        return clickActBody;
    }

    public void setClickActBody(String clickActBody) {
        this.clickActBody = clickActBody;
    }
    public Date getSendBeginTime() {
        return sendBeginTime;
    }

    public void setSendBeginTime(Date sendBeginTime) {
        this.sendBeginTime = sendBeginTime;
    }
    public Date getSendEndTime() {
        return sendEndTime;
    }

    public void setSendEndTime(Date sendEndTime) {
        this.sendEndTime = sendEndTime;
    }
    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }
    public BigDecimal getSendTimes() {
        return sendTimes;
    }

    public void setSendTimes(BigDecimal sendTimes) {
        this.sendTimes = sendTimes;
    }
    public BigDecimal getTakeTime() {
        return takeTime;
    }

    public void setTakeTime(BigDecimal takeTime) {
        this.takeTime = takeTime;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public static class TF {

        public static String TABLE_NAME = "MP_CUST_MSG";   // 表名

        public static String TABLE_SCHEMA = "MM";   // 库名

        public static String msgId = "MSG_ID";  // 消息ID
        public static String taskId = "TASK_ID";  // 任务ID
        public static String chanType = "CHAN_TYPE";  // 通道类型，数据字典：0-手机银行，2-微信
        public static String pushType = "PUSH_TYPE";  // 推送类型，数据字典：0-消息，1-扩展
        public static String clientAppId = "CLIENT_APP_ID";  // 客户端应用标识
        public static String clientType = "CLIENT_TYPE";  // 终端类型，数据字典：0-iOS，1-Android，2-WP
        public static String deviceId = "DEVICE_ID";  // 设备标识
        public static String clientId = "CLIENT_ID";  // 终端标识(令牌)
        public static String custId = "CUST_ID";  // 签约客户号
        public static String msgTitle = "MSG_TITLE";  // 消息标题
        public static String msgBody = "MSG_BODY";  // 消息内容
        public static String extBody = "EXT_BODY";  // 扩展内容
        public static String distVal = "DIST_VAL";  // 干扰值
        public static String prioVal = "PRIO_VAL";  // 优先级
        public static String clickActType = "CLICK_ACT_TYPE";  // 点击通知动作类型，0-打开应用，1-打开网址，2-自定义
        public static String clickActBody = "CLICK_ACT_BODY";  // 点击通知动作内容
        public static String sendBeginTime = "SEND_BEGIN_TIME";  // 发送开始时间
        public static String sendEndTime = "SEND_END_TIME";  // 发送结束时间
        public static String sendTime = "SEND_TIME";  // 发送时间
        public static String sendTimes = "SEND_TIMES";  // 发送次数
        public static String takeTime = "TAKE_TIME";  // 耗时(毫秒)
        public static String status = "STATUS";  // 状态，0-未发送，1-发送中，2-发送成功，3-发送失败
        public static String delFlag = "DEL_FLAG";  // 删除标记，0-未删除，1-已删除
        public static String createTime = "CREATE_TIME";  // 创建时间
        public static String updateTime = "UPDATE_TIME";  // 修改时间
        public static String errMsg = "ERR_MSG";  // 错误信息

    }
}
