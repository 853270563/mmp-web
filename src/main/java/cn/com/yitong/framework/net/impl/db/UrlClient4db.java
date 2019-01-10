package cn.com.yitong.framework.net.impl.db;

import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.servlet.ServerInit;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.util.FileUtil;
import cn.com.yitong.util.YTLog;

@Component
public class UrlClient4db implements IClientFactory {
	private Logger logger = YTLog.getLogger(this.getClass());

	@Override
	public boolean execute(IBusinessContext busiCtx, String transCode) {
		busiCtx.getRequestContext();
		String filePath = ServerInit.getConfig("IBS_TEST_DATA_DIR") + "/db/"
				+ CtxUtil.transFullPath(transCode) + ".json";
		// 加载静态数据
		try {
			// 静态缓存
			// if (caches.containsKey(transCode)) {
			// String xmlRsp = caches.get(transCode);
			// busiCtx.setResponseEntry(xmlRsp);
			// return true;
			// }
			String responseStr = FileUtil.readFileAsString(filePath, "utf-8");
			// System.out.println(responseStr);
			Map temps = net.sf.json.JSONObject.fromObject(responseStr);
			busiCtx.setErrorInfo(AppConstants.STATUS_OK, AppConstants.MSG_SUCC,
					transCode);
			logger.info("response map: \n" + temps);
			busiCtx.setResponseEntry(temps);
			return true;
		} catch (Exception e) {
			logger.error("crud db response status error! transCode is :"
					+ transCode, e);
			busiCtx.setErrorInfo(AppConstants.STATUS_FAIL,
					"crud db rsponse status error!", transCode);
		}
		return false;
	}

	@Override
	public void releaseSession(HttpClient httpClient, IBusinessContext busiCtx)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
