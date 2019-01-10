package cn.com.yitong.framework.net.impl.btt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.INetTools;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.servlet.ServerInit;

/**
 * MB通讯组件
 * 
 * @author yaoym
 * 
 */
@Component
public class NetTools4btt implements INetTools {

	@Autowired
	@Qualifier("requestBuilder4btt")
	IRequstBuilder requestBuilder;

	@Autowired
	@Qualifier("EBankConfParser4btt")
	IEBankConfParser confParser;

	@Autowired
	@Qualifier("urlClient4btt")
	protected IClientFactory clientFactory;

	@Autowired
	@Qualifier("clientFactory4bttest")
	protected IClientFactory clientTest;

	@Autowired
	@Qualifier("responseParser4btt")
	protected IResponseParser responseParser;

	@Override
	public boolean execute(IBusinessContext busiCtx, String transCode) {
		if (!requestBuilder.buildSendMessage(busiCtx, confParser, transCode)) {
			return false;
		}
		String tranCodes = ServerInit.getString("BTT_TRAANS");
		// 接口通讯
		if (tranCodes.contains(transCode)) {
			// 动态数据
			if (!clientFactory.execute(busiCtx, transCode)) {
				return false;
			}
		} else {
			// 静态数据
			if (!clientTest.execute(busiCtx, transCode)) {
				return false;
			}
		}
		// 解析响应报文
		return responseParser
				.parserResponseData(busiCtx, confParser, transCode);
	}

}
