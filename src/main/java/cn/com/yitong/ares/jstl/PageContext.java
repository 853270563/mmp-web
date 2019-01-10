/**
 * 
 */
package cn.com.yitong.ares.jstl;

/**
 * 模拟 JSP-PageContext
 * 
 * @作者：yaoyimin
 * @邮箱：yaoyimin@yitong.com.cn
 * @创建时间：2016年9月10日 下午4:50:16
 * @版本信息：
 */
public interface PageContext {
	public int PARAM_SCOPE = 10;
	public int HEADER_SCOPE = 20;
	public int SESSION_SCOPE = 30;

	public Object findAttribute(String name);
}
