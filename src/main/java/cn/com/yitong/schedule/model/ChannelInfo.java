package cn.com.yitong.schedule.model;

/**
 * 渠道服务开启控制
 * 
 * @author yaoym
 * 
 */
public class ChannelInfo {

	private boolean open;
	private String startDate;
	private String startTime;
	private String endDate;
	private String endTime;
	private String code;

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String toString() {
		StringBuffer bf = new StringBuffer(100);
		bf.append(open).append(startDate).append(startTime);
		bf.append(endDate).append(endTime).append(code);
		return bf.toString();
	}

}
