package cn.com.yitong.core.session;

/**
 * Session 异常
 *
 * @author 李超（lc3@yitong.com.cn）
 */
public class SessionException extends RuntimeException {

    private static final long serialVersionUID = -5888560494686556011L;

    public SessionException() {
        super();
    }

    public SessionException(String message) {
        super(message);
    }

    public SessionException(String message, Throwable cause) {
        super(message, cause);
    }

    public SessionException(Throwable cause) {
        super(cause);
    }
}
