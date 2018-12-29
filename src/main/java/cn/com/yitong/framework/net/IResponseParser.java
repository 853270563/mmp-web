package cn.com.yitong.framework.net;

import cn.com.yitong.framework.base.IBusinessContext;

public interface IResponseParser {

	/**
	 * 响应报文解析
	 * 
	 * @param busiContext
	 * @param transCode
	 * @return
	 */
	public boolean parserResponseData(IBusinessContext busiContext,
									  IEBankConfParser confParser, String transCode);

}
