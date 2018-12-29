package cn.com.yitong.framework.core.vo;

import cn.com.yitong.consts.NS;

public class DeviceCkeck {
	
	private String deviceUuid;
	
	private String eraseSign = NS.DEVICE_ERASE_STATUS_NO;
	
	private String lockSign = NS.DEVICE_LOCK_STATUS_NO;
	
	private String result;
	
	private Boolean isSuccess = true;
	
	private String message;

	public String getDeviceUuid() {
		return deviceUuid;
	}

	public void setDeviceUuid(String deviceUuid) {
		this.deviceUuid = deviceUuid;
	}

	public String getEraseSign() {
		return eraseSign;
	}

	public void setEraseSign(String eraseSign) {
		this.eraseSign = eraseSign;
	}

	public String getLockSign() {
		return lockSign;
	}

	public void setLockSign(String lockSign) {
		this.lockSign = lockSign;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Boolean getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "DeviceInfo [deviceUuid=" + deviceUuid + ", eraseSign="
				+ eraseSign + ", isSuccess=" + isSuccess + ", lockSign="
				+ lockSign + ", message=" + message + ", result=" + result
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((deviceUuid == null) ? 0 : deviceUuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DeviceCkeck other = (DeviceCkeck) obj;
		if (deviceUuid == null) {
			if (other.deviceUuid != null)
				return false;
		} else if (!deviceUuid.equals(other.deviceUuid))
			return false;
		return true;
	}
}
