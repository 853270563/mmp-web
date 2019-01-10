/**
 * 
 */
package cn.com.yitong.ares.jstl;

/**
 * TODO(这里用一句话描述这个类的作用)
 * 
 * @作者：yaoyimin
 * @邮箱：yaoyimin@yitong.com.cn
 * @创建时间：2016年9月10日 下午4:52:18
 * @版本信息：
 */
public class JspException extends RuntimeException {

	/**
	 * @param string
	 * @param ex
	 */
	public JspException(String msg, Exception ex) {
		super(msg);
	}

	/**
	 * @param ex
	 */
	public JspException(Exception ex) {
		super(ex.getMessage());
	}

	/**
	 * @param format
	 */
	public JspException(String msg) {
	}

	/**
	 * @param format
	 * @param rootCause
	 */
	public JspException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

}
