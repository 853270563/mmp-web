package cn.com.yitong.framework.net;

import javax.servlet.http.HttpServletRequest;

import cn.com.yitong.framework.base.IBusinessContext;

/**
 * 将参数转入Element
 * 
 * @author yaoym
 * 
 */
public interface IParamCover {

	/**
	 * 请求参数装入器
	 * @param context
	 * @param request
	 * @param signed 是否加密
	 * @param transCode
	 * @return
	 * @throws ParamCoverException
	 */
	public boolean cover(IBusinessContext context, HttpServletRequest request,
						 boolean signed, String transCode) throws Exception;
}
