package cn.com.yitong.framework.net.impl.db;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.util.YTLog;

@Component
public class UrlClient4db implements IClientFactory {

	private Logger logger = YTLog.getLogger(this.getClass());

	@Override
	public boolean execute(IBusinessContext busiCtx, String transCode) {
		busiCtx.getRequestContext();
		String resPath = "classpath:META-INF/board/data" + CtxUtil.transFullPath(transCode) + ".json";
		// 加载静态数据
		try {
			File file = ResourceUtils.getFile(resPath);
			String responseStr = FileUtils.readFileToString(file, "utf-8");
			
			Map temps = net.sf.json.JSONObject.fromObject(responseStr);
			busiCtx.setErrorInfo(AppConstants.STATUS_OK, AppConstants.MSG_SUCC, transCode);
			logger.info("response map: \n" + temps);
			busiCtx.setResponseEntry(temps);
			return true;
		} catch (Exception e) {
			logger.error("crud db response status error! transCode is :" + transCode, e);
			busiCtx.setErrorInfo(AppConstants.STATUS_FAIL, "crud db rsponse status error!", transCode);
		}
		return false;
	}

	@Override
	public void releaseSession(HttpClient httpClient, IBusinessContext busiCtx) throws Exception {
		// TODO Auto-generated method stub
	}
}
