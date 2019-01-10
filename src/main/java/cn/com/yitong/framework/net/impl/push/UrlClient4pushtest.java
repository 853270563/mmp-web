package cn.com.yitong.framework.net.impl.push;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.dom4j.Element;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.servlet.ServerInit;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.XmlUtil;
import cn.com.yitong.util.YTLog;

public class UrlClient4pushtest implements IClientFactory {
	private Logger logger = YTLog.getLogger(this.getClass());

	@Override
	public boolean execute(IBusinessContext busiCtx, String transCode) {
		busiCtx.getRequestContext();
		String folder = transCode.substring(0, 2);
		String filePath = ServerInit.getConfig("IBS_TEST_DATA_DIR") + "/push/"
				+ folder + "/" + transCode + ".xml";
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
