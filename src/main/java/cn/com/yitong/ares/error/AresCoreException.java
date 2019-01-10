package cn.com.yitong.ares.error;

import cn.com.yitong.ares.consts.AresR;

public class AresCoreException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	// 消息码
	private final String messageKey;

	// 已经用message-source处理的消息
	private String resolvedMessage;

	// 参数
	private Object args[];

	public AresCoreException(String messageKey) {
		super();
		this.messageKey = messageKey;
		this.args = AresR.EMPTY_PARAMS;
	}

	public AresCoreException(String messageKey, Object... args) {
		super();
		this.messageKey = messageKey;
		this.args = args;
	}

	public AresCoreException(String messageKey, Throwable cause) {
		super(cause);
		this.messageKey = messageKey;
		this.args = AresR.EMPTY_PARAMS;
	}

	public AresCoreException(String messageKey, Throwable cause, Object... args) {
		super(cause);
		this.messageKey = messageKey;
		this.args = args;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public String getResolvedMessage() {
		return resolvedMessage;
	}

	public void setResolvedMessage(String resolvedMessage) {
		this.resolvedMessage = resolvedMessage;
	}

	public Object[] getArgs() {
		return args;
	}
}
