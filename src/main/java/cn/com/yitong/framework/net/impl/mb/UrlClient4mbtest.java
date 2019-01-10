package cn.com.yitong.framework.net.impl.mb;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.servlet.ServerInit;
import cn.com.yitong.util.XmlUtil;
import cn.com.yitong.util.YTLog;

@Component
public class UrlClient4mbtest implements IClientFactory {
	private Logger logger = YTLog.getLogger(this.getClass());

	@Override
	public boolean execute(IBusinessContext busiCtx, String transCode) {
		busiCtx.getRequestContext();
		String folder = transCode.substring(0, 2); 
		String filePath = (ServerInit.getConfig("IBS_TEST_DATA_DIR") + "/mb/"
				+ folder + "/" + transCode + ".xml");
		// 加载静态数据
		try {
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
