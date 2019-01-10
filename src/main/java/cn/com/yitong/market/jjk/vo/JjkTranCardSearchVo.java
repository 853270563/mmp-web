package cn.com.yitong.market.jjk.vo;

import java.util.Date;

public class JjkTranCardSearchVo {
	
	/**
	 * 0: 查询某机构下所有人员的办理的业务列表  1：根据个人查询办理的业务列表
	 */
	private String queryType;
	/**
	 * 根据个人查询时，不需要此输入此字段
	 */
	private String queryOrg;
	/**
	 * 经办客户经理（号）
	 */
	private String signUser;
	/**
	 * 经办开始时间
	 */
	private Date signStartTime;
	/**
	 * 经办结束时间
	 */
	private Date signEndTime;
	private String signState;
	public String getQueryType() {
		return queryType;
	}
	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}
	public String getQueryOrg() {
		return queryOrg;
	}
	public void setQueryOrg(String queryOrg) {
		this.queryOrg = queryOrg;
	}
	public String getSignUser() {
		return signUser;
	}
	public void setSignUser(String signUser) {
		this.signUser = signUser;
	}
	public Date getSignStartTime() {
		return signStartTime;
	}
	public void setSignStartTime(Date signStartTime) {
		this.signStartTime = signStartTime;
	}
	public Date getSignEndTime() {
		return signEndTime;
	}
	public void setSignEndTime(Date signEndTime) {
		this.signEndTime = signEndTime;
	}
	public String getSignState() {
		return signState;
	}
	public void setSignState(String signState) {
		this.signState = signState;
	}

}
