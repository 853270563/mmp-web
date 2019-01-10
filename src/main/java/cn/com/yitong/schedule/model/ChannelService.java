package cn.com.yitong.schedule.model;

/**
 * 服务开启控制的配置信息
 * 
 * @author yaoym
 * 
 */
public class ChannelService {

	private String channel;
	private String serviceCode;
	private boolean open;
	private String startDate;
	private String startTime;
	private String endDate;
	private String endTime;
	private boolean loginCheck;

	public String toString() {
		StringBuffer bf = new StringBuffer(100);
		bf.append(serviceCode);
		bf.append(open).append(startDate).append(startTime);
		bf.append(endDate).append(endTime).append(channel);
		bf.append(loginCheck);
		return bf.toString();
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

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

	public boolean isLoginCheck() {
		return loginCheck;
	}

	public void setLoginCheck(boolean loginCheck) {
		this.loginCheck = loginCheck;
	}

}
