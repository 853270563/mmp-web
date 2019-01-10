package cn.com.yitong.framework.net.impl.as;

import cn.com.yitong.framework.net.*;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.util.YTLog;

/**
 * 外部数据采集系统通讯组件
 *
 * @author huangqiang@yitong.com.cn
 * @date   20180720
 *
 */
@Component
public class NetToolsAs implements INetTools {

	static Logger logger = YTLog.getLogger(NetToolsAs.class.getName());
	@Autowired
	@Qualifier("requestBuilderAs")
	IRequstBuilder requestBuilder;

	@Autowired
	@Qualifier("EBankConfParserAs")
	IEBankConfParser confParser;

	@Autowired
	@Qualifier("responseParserAs")
	protected IResponseParser responseParser;

	@Autowired
	@Qualifier("netConnect4As")
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
