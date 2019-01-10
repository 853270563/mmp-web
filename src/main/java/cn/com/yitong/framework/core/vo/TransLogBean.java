package cn.com.yitong.framework.core.vo;

import java.util.HashMap;
import java.util.Map;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.consts.NS;
import cn.com.yitong.consts.Properties;
import cn.com.yitong.framework.core.bean.DbLogProperties;

/**
 * 公共交易日志信息
 * 
 * @author yaoym
 * 
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

	public void setPropery(String key, String value) {
		properties.put(key, value);
	}

	public String getProperty(String key) {
		return properties.get(key);
	}

	public String getCifNo() {
		return getProperty(NS.CIF_NO);
	}

	public void setCifNo(String cifNo) {
		setPropery(NS.CIF_NO, cifNo);
	}

	public String getUserSessId() {
		return getProperty(NS.USER_SESS_ID);
	}

	public void setUserSessId(String userSessId) {
		setPropery(NS.USER_SESS_ID, userSessId);
	}

	public String getTransSeqNo() {
		return getProperty(NS.TRANS_LOG_SEQ);
	}

	public void setTransSeqNo(String transSeqNo) {
		setPropery(NS.TRANS_LOG_SEQ, transSeqNo);
	}

	public String getRspCode() {
		return getProperty(AppConstants.STATUS);
	}

	public void setRspCode(String rspCode) {
		setPropery(AppConstants.STATUS, rspCode);
	}

	public String getRspMsg() {
		return getProperty(AppConstants.MSG);
	}

	public void setRspMsg(String rspMsg) {
		this.setPropery(AppConstants.MSG, rspMsg);
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

	public String getIbsLgnId() {
		return getProperty(NS.IBS_LGN_ID);
	}

	public void setIbsLgnId(String ibsLgnId) {
		setPropery(NS.IBS_LGN_ID, ibsLgnId);
	}

	/**
	 * 是事需要保存数据库日志
	 * 
	 * @return
	 */
	public boolean isNeedSaveLog() {

		return DbLogProperties.isNeedSaveLog(transCode);
	}

	/**
	 * 获取SQLMAP
	 * 
	 * @return
	 */
	public String getSqlMap() {
		return DbLogProperties.getSqlMapConfig(transCode);
	}

	/**
	 * 获取需要的前端上下文请求字段
	 * 
	 * @return
	 */
	public String[] getBaseParams() {
		return DbLogProperties.getBaseCtxParams(transCode);
	}

	/**
	 * 获取需要的请求上下文请求字段
	 * 
	 * @return
	 */
	public String[] getRequestCtxParams() {
		return DbLogProperties.getRequestCtxParams(transCode);
	}

	/**
	 * 获取需要提取的响应上下文参数
	 * 
	 * @return
	 */
	public String[] getRsponseCtxParams() {
		return DbLogProperties.getResponseCtxParams(transCode);
	}

	/**
	 * 获取交易中参数
	 * 
	 * @return
	 */
	public String[] getMessageParams() {
		return DbLogProperties.getMessageParams(transCode);
	}

	/**
	 * 獲取交易模板
	 * 
	 * @return
	 */
	public String getMessageTemplate() {
		return DbLogProperties.getMessageTemplate(transCode);
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	// 动账相关交易
	private static final String TRANS_ANSY = Properties
			.getString(NS.TRANS_FLG_ANSY);
	// 登录相关交易
	private static final String TRANS_LOGIN = Properties
			.getString(NS.TRANS_FLG_LOGIN);

	/**
	 * 获取交易类型
	 * 
	 * @param transCode
	 * @return
	 */
	public String getTransFlag(String transCode) {
		if (TRANS_LOGIN.contains(transCode)) {
			return NS.TRANS_FLG_0;
		}
		if (TRANS_ANSY.contains(transCode)) {
			return NS.TRANS_FLG_1;
		}
		return NS.TRANS_FLG_2;
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
