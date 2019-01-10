package cn.com.yitong.framework.net.impl.mq;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.XmlUtil;
import cn.com.yitong.util.YTLog;

@Component
public class UrlClient4mqTest implements IClientFactory {
	private Logger logger = YTLog.getLogger(this.getClass());

	@Override
	public boolean execute(IBusinessContext busiCtx, String transCode) {
		busiCtx.getRequestContext();
		String folder = transCode.substring(0, 2);
		String filePath = "D:/最新大豐IBS/ibanking/WebContent/WEB-INF/data/mq/07/"
				+ transCode + ".xml";
		// 加载静态数据
		try {
			// 测试请求报文生成
			Element rqctx = busiCtx.getRequestContext(transCode);
			String xmlStr = StringUtil.formatXmlStr(rqctx.asXML());
			if (logger.isDebugEnabled()) {
				logger.debug(transCode + " send xml msg: \n" + xmlStr);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("----btt static data---:" + filePath);
			}
			String responseStr = XmlUtil.readXml(filePath);
			busiCtx.setResponseEntry(responseStr);
			return true;
		} catch (Exception e) {
			logger.error("btttest reponse status error!", e);
			busiCtx.setErrorInfo(AppConstants.STATUS_FAIL,
					"btttest rsponse status error!", transCode);
		}
		return false;
	}

	@Override
	public void releaseSession(HttpClient httpClient, IBusinessContext busiCtx)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
