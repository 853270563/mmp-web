package cn.com.yitong.framework.net;

import cn.com.yitong.framework.base.IBusinessContext;

/**
 * 报文构建器接口
 * 
 * @author yaoym
 * 
 */
public interface IRequstBuilder {
	/**
	 * 生成请求报文
	 * 
	 * @param busiContext
	 * @param transCode
	 * @return
	 */
	public boolean buildSendMessage(IBusinessContext busiContext,
									IEBankConfParser confParser, String transCode);
}
