package cn.com.yitong.framework.core.vo;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.consts.NS;

import java.util.HashMap;
import java.util.Map;

/**
 * 公共交易日志信息
 * @author yaoym
 */
public class TransLogBean {

	private long startTime;
	private long endTime;
	private String transCode;
	private boolean saved;

	private Map<String, String> properties = new HashMap<String, String>();

	public TransLogBean(String transCode) {
		this.transCode = transCode;
		this.setPropery(NS.TRANS_CODE, transCode);
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	private void setPropery(String key, String value) {
		properties.put(key, value);
	}

	public String getProperty(String key) {
		return properties.get(key);
	}

	public String getTransSeqNo() {
		return getProperty(NS.TRANS_LOG_SEQ);
	}

	public void setTransSeqNo(String transSeqNo) {
		setPropery(NS.TRANS_LOG_SEQ, transSeqNo);
	}

	public String getSessionId() {
		return getProperty(NS.TRANS_SESSION_ID);
	}

	public void setSessionId(String sessionId) {
		setPropery(NS.TRANS_SESSION_ID, sessionId);
	}

	public String getLgnI() {
		return getProperty(NS.LOGIN_ID);
	}

	public void setLgnId(String lgnId) {
		setPropery(NS.LOGIN_ID, lgnId);
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public String getTransCode() {
		return getProperty(NS.TRANS_CODE);
	}

	public String getRspStatus() {
		return getProperty(AppConstants.STATUS);
	}

	public void setRspStatus(String status) {
		setPropery(AppConstants.STATUS, status);
	}

	public String getRspMsg() {
		return getProperty(AppConstants.MSG);
	}

	public void setRspMsg(String rspMsg) {
		this.setPropery(AppConstants.MSG, rspMsg);
	}

	public String getTransCost() {
		return getProperty(NS.TRANS_LOG_SEQ);
	}

	public void setTransCost(String cost) {
		this.setPropery(NS.TRANS_COST, cost);
	}

	public String getDeviceId() {
		return getProperty(NS.DEVICE_ID);
	}

	public void setDeviceId(String deviceId) {
		this.setPropery(NS.DEVICE_ID, deviceId);
	}

	public void setTransDate(String date) {
		this.setPropery(NS.TRAN_DATE, date);
	}

	public String getTransDate() {
		return getProperty(NS.TRAN_DATE);
	}

	public void setTransTime(String time) {
		this.setPropery(NS.TRAN_TIME, time);
	}

	public String getTransTime() {
		return getProperty(NS.TRAN_TIME);
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public boolean isSaved() {
		return saved;
	}

	public void setSaved(boolean saved) {
		this.saved = saved;
	}

	@Override
	public String toString() {
		return properties.toString();
	}
}
