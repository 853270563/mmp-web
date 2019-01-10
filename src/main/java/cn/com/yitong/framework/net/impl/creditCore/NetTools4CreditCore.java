package cn.com.yitong.framework.net.impl.creditCore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.INetTools;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;

/**
 * 信贷核心通讯组件
 * @author luanyu
 * @date   2018年8月9日
 */
@Component
public class NetTools4CreditCore implements INetTools {
	@Autowired
	@Qualifier("requestBuilder4CreditCore")
	IRequstBuilder requestBuilder;

	@Autowired
	@Qualifier("eBankConfParser4CreditCore")
	IEBankConfParser confParser;

	@Autowired(required = false)
	@Qualifier("netConnect4CreditCore")
	protected IClientFactory clientFactory;

	@Autowired
	@Qualifier("responsParse4CreditCore")
	protected IResponseParser responseParser;
	@Override
	public boolean execute(IBusinessContext busiCtx, String transCode) {
		// TODO Auto-generated method stub
		requestBuilder.buildSendMessage(busiCtx, confParser, transCode);
		clientFactory.execute(busiCtx, transCode);
		responseParser.parserResponseData(busiCtx, confParser, transCode);
		return false;
	}

}
