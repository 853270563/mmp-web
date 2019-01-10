package cn.com.yitong.framework.net.impl.btt;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.servlet.ServerInit;
import cn.com.yitong.util.FileUtil;
import cn.com.yitong.util.JsonXml;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

/**
 * 档板测试器
 * 
 * @author yaoym
 * 
 */
@Component
public class ClientFactory4bttest implements IClientFactory {
	private Logger logger = YTLog.getLogger(this.getClass());
	private Map<String, String> caches = new HashMap();

	@Override
	public boolean execute(IBusinessContext busiCtx, String transCode) {
		busiCtx.getRequestContext();
		String folder = "";
		if (transCode.startsWith("P") || transCode.startsWith("C")) {
			folder = transCode.substring(2, 4);
		}
		String filePath = ServerInit.getConfig("IBS_TEST_DATA_DIR") + "/btt/"
				+ folder + "/" + transCode + ".json";
		// 加载静态数据
		try {
			// 测试请求报文生成
			Element rqctx = busiCtx.getRequestContext(transCode);
			if (logger.isDebugEnabled()) {
				String xmlStr = StringUtil.formatXmlStr(rqctx.asXML());
				logger.debug("send msg: \n" + xmlStr);
			}
			// XML to JSON
			String sendData = JsonXml.xmlStrToJsonStr(rqctx.asXML());
			if (logger.isDebugEnabled()) {
				logger.debug("send msg: \n" + StringUtil.replacePwd(sendData));
				logger.debug("----btt static data---:" + filePath);
			}
			// 静态缓存
			// if (caches.containsKey(transCode)) {
			// String xmlRsp = caches.get(transCode);
			// busiCtx.setResponseEntry(xmlRsp);
			// return true;
			// }
			String responseStr = FileUtil.readFileAsString(filePath, "utf-8");
			// System.out.println(responseStr);
			String xmlRsp = JsonXml.jsonStrToXmlStr(responseStr);
			// logger.info("response xml: \n" + xmlRsp);
			caches.put(transCode, xmlRsp);
			busiCtx.setResponseEntry(xmlRsp);
			return true;
		} catch (Exception e) {
			logger.error("btttest response status error! transCode is :"
					+ transCode, e);
			busiCtx.setErrorInfo(AppConstants.STATUS_FAIL,
					"btttest rsponse status error!", transCode);
		}
		return false;
	}

	@Override
	public void releaseSession(HttpClient httpClient, IBusinessContext busiCtx)
			throws Exception {

	}

}
