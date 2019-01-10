package cn.com.yitong.schedule.model;

/**
 * 渠道交易登录控制
 * 
 * @author liuqing
 * 
 */
public class ChannelBusiInfo {

	private boolean login;
	private String chanTyp;
	private String busiCode;
	private String busiName;
	private String busiLimCt;
	private String upDate;

	public boolean isLogin() {
		return login;
	}

	public void setLogin(boolean login) {
		this.login = login;
	}

	public String getChanTyp() {
		return chanTyp;
	}

	public void setChanTyp(String chanTyp) {
		this.chanTyp = chanTyp;
	}

	public String getBusiCode() {
		return busiCode;
	}

	public void setBusiCode(String busiCode) {
		this.busiCode = busiCode;
	}

	public String getBusiName() {
		return busiName;
	}

	public void setBusiName(String busiName) {
		this.busiName = busiName;
	}

	public String getBusiLimCt() {
		return busiLimCt;
	}

	public void setBusiLimCt(String busiLimCt) {
		this.busiLimCt = busiLimCt;
	}

	public String getUpDate() {
		return upDate;
	}

	public void setUpDate(String upDate) {
		this.upDate = upDate;
	}
	
	public String toString() {
		StringBuffer bf = new StringBuffer(100);
		bf.append(login).append(chanTyp).append(busiCode).append(busiLimCt);
		return bf.toString();
	}

}
