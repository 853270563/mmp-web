package cn.com.yitong.ares.error;

import cn.com.yitong.ares.consts.AresR;

public class AresRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	// 消息码
	private String messageKey;

	// 已经用message-source处理的消息
	private String resolvedMessage;

	// 参数
	private Object args[];

	public AresRuntimeException() {
		super();
	}

	public AresRuntimeException(String messageKey) {
		super(messageKey);
		this.messageKey = messageKey;
		this.args = AresR.EMPTY_PARAMS;
	}

	public AresRuntimeException(String messageKey, Object... args) {
		super(messageKey);
		this.messageKey = messageKey;
		this.args = args;
	}

	public AresRuntimeException(String messageKey, Throwable cause) {
		super(cause);
		this.messageKey = messageKey;
		this.args = AresR.EMPTY_PARAMS;
	}

	public AresRuntimeException(String messageKey, Throwable cause, Object... args) {
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
