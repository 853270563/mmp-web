package cn.com.yitong.framework.net.impl.netloan;

import cn.com.yitong.framework.net.*;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.util.YTLog;

/**
 * 网贷平台通讯组件
 *
 * @author huangqiang@yitong.com.cn
 * @date   20180720
 *
 */
@Component
public class NetToolsNetLoan implements INetTools {

	static Logger logger = YTLog.getLogger(NetToolsNetLoan.class.getName());
	@Autowired
	@Qualifier("requestBuilderNetLoan")
	IRequstBuilder requestBuilder;

	@Autowired
	@Qualifier("EBankConfParserNetLoan")
	IEBankConfParser confParser;

	@Autowired
	@Qualifier("responseParserNetLoan")
	protected IResponseParser responseParser;

	@Autowired
	@Qualifier("netConnect4NetLoan")
	protected IClientFactory clientFactory;

	@Override
	public boolean execute(IBusinessContext busiCtx, String transCode) {
		if (!requestBuilder.buildSendMessage(busiCtx, confParser, transCode)) {
			return false;
		}
		// 接口通讯
		if (!clientFactory.execute(busiCtx,transCode)) {
			return false;
		}
		// 解析响应报文
		return responseParser.parserResponseData(busiCtx, confParser, transCode);
	}

}
