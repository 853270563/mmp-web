package cn.com.yitong.framework.net.impl.mq;

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
 * MB通讯组件
 * 
 * @author yaoym
 * 
 */
@Component
public class NetTools4mqtest implements INetTools {

	@Autowired
	@Qualifier("requestBuilder4mqTest")
	IRequstBuilder requestBuilder;

	@Autowired
	@Qualifier("EBankConfParser4mq")
	IEBankConfParser confParser;

	@Autowired
	@Qualifier("urlClient4mqTest")
	protected IClientFactory clientFactory;

	@Autowired
	@Qualifier("responseParser4mqTest")
	protected IResponseParser responseParser;

	@Override
	public boolean execute(IBusinessContext busiCtx, String transCode) {
		if (!requestBuilder.buildSendMessage(busiCtx, confParser, transCode)) {
			return false;
		}
		// 接口通讯
		if (!clientFactory.execute(busiCtx, transCode)) {
			return false;
		}
		// 解析响应报文
		return responseParser.parserResponseData(busiCtx, confParser, transCode);
	}

}
