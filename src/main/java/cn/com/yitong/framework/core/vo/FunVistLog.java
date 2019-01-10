package cn.com.yitong.framework.core.vo;

/**
 * 系统功能访问日志
 * 
 * @author yaoym
 * 
 */
public class FunVistLog {
	private String tranSeq;
	private String tranDate;
	private String sessId;
	private String inTime;
	private String cifNo;
	private String clientIp;
	private String vistMenuId;
	private String vistDes;
	private String outTime;
	private String infoStat;
	private String userSessSeq;

	private String language;

	public FunVistLog() {

	}

	public String getSessId() {
		return sessId;
	}

	public void setSessId(String sessId) {
		this.sessId = sessId;
	}

	public String getInfoStat() {
		return infoStat;
	}

	public void setInfoStat(String infoStat) {
		this.infoStat = infoStat;
	}

	public String getTranSeq() {
		return tranSeq;
	}

	public void setTranSeq(String tranSeq) {
		this.tranSeq = tranSeq;
	}

	public String getTranDate() {
		return tranDate;
	}

	public void setTranDate(String tranDate) {
		this.tranDate = tranDate;
	}

	public String getInTime() {
		return inTime;
	}

	public void setInTime(String inTime) {
		this.inTime = inTime;
	}

	public String getCifNo() {
		return cifNo;
	}

	public void setCifNo(String cifNo) {
		this.cifNo = cifNo;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getVistMenuId() {
		return vistMenuId;
	}

	public void setVistMenuId(String vistMenuId) {
		this.vistMenuId = vistMenuId;
	}

	public String getVistDes() {
		return vistDes;
	}

	public void setVistDes(String vistDes) {
		this.vistDes = vistDes;
	}

	public String getOutTime() {
		return outTime;
	}

	public void setOutTime(String outTime) {
		this.outTime = outTime;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getUserSessSeq() {
		return userSessSeq;
	}

	public void setUserSessSeq(String userSessSeq) {
		this.userSessSeq = userSessSeq;
	}

}
