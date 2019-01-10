package cn.com.yitong.framework.service;

import java.util.Map;

import cn.com.yitong.framework.base.IBusinessContext;

/**
 * 第三方系统接口方法定义
 * @author ygh
 *
 */
public interface IThirdPatryMessageService{
	
	
	/**
	 * 信贷系统
	 * @param ctx       
	 * 		数据总线
	 * @param trancode
	 * 		调用接口编码
	 * @param params
	 * 		响应参数
	 * @return 返回通訊是否成功
	 */
	boolean creditLoanSystem(IBusinessContext busiCtx, String transCode, Map<?, ?> rst);
	
	/**
	 * 2.ESB联盟系统
	 * @param ctx       
	 * 		数据总线
	 * @param trancode
	 * 		调用接口编码
	 * @param params
	 * 		响应参数
	 * @return 返回通訊是否成功
	 */

	boolean esbUnionSystem(IBusinessContext busiCtx, String transCode, Map<String, Object> rst);
	
	
}
