package cn.com.yitong.ares.error;

/**
 * 第三方系统异常
 * 
 * @author yaoym
 * 
 */
public class OtherRuntimeException extends AresRuntimeException {
	private static final long serialVersionUID = 2L;

	private String errorCode;
	private String errorMessage;

	public OtherRuntimeException(String errorCode, String errorMsg) {
		super(String.format("错误码转译【errorCode=%s,errorMsg=%s】", errorCode, errorMsg));
		this.errorCode = errorCode;
		this.errorMessage = errorMsg;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
