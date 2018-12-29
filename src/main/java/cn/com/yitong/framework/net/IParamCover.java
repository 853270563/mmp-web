package cn.com.yitong.framework.net;

import cn.com.yitong.framework.base.IBusinessContext;

import javax.servlet.http.HttpServletRequest;

/**
 * 将参数转入Element
 * 
 * @author yaoym
 * 
 */
public interface IParamCover {

	/**
	 * 请求参数装入器
	 */
	public boolean cover(IBusinessContext context, HttpServletRequest request,
						 boolean signed, String transCode) throws Exception;
}
