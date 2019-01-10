package cn.com.yitong.framework.net.impl.bankcs;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.servlet.ServerInit;
import cn.com.yitong.util.FileUtil;
import cn.com.yitong.util.YTLog;

@Component
public class SocketFactory4bankcsTest implements IClientFactory {
	private Logger logger = YTLog.getLogger(this.getClass());
	private Map<String, String> caches = new HashMap();

	@Override
	public boolean execute(IBusinessContext busiCtx, String transCode) {
		logger.info("SocketFactory4stock.execute.........start......");
		// TODO Auto-generated method stub 
		String folder = "";
		if (transCode.startsWith("P")) {
			folder = transCode.substring(2, 4);
		}		// 加载静态数据
		try {

			String filePath = System.getProperty("mmp.root") + "/WEB-INF/data//bocm/"+transCode + ".txt";
//			String filePath = (ServerInit.getConfig("IBS_TEST_DATA_DIR") + "/bocm/"+transCode + ".txt");
//			String filePath = "D:/works/tfb_space/ibanking/WebContent/WEB-INF/data/bocm/BO01000Op.txt";

			logger.info("----bankcsTest response test data filepath---:" + filePath);
			if (caches.containsKey(transCode)) {
				String xmlRsp = caches.get(transCode); 
				busiCtx.setResponseEntry(xmlRsp);
			}else{
				String responseStr = FileUtil.readFileAsString(filePath, "utf-8"); 
				busiCtx.setResponseEntry(responseStr);
				caches.put(transCode, responseStr);
			}
			return true;
		} catch (Exception e) {
			logger.error("bankcsTest reponse status error!", e);
			busiCtx.setErrorInfo(AppConstants.STATUS_FAIL,
					"bankcsTest rsponse status error!", transCode);
		}
		return false;
	}

	@Override
	public void releaseSession(HttpClient httpClient, IBusinessContext busiCtx)
			throws Exception {
		// TODO Auto-generated method stub
	}

	public static void main(String[] args) {
		SocketFactory4bankcsTest factory = new SocketFactory4bankcsTest();
		IBusinessContext busiCtx = new BusinessContext();
		busiCtx.setRequestEntry("2910010000 0N291001     OJIB00 EB                                                                                                                                                                                                                                        N0214love                            TFB BOCM                       00021710049416          344021010010196       344+0000000010000+0000000000000+00000000000+00000000000+0000000000000+00000000000+00000000000BOCM              Y1         +0000000000000       +0000000000000       +0000000000000       +0000000000000       +0000000000000       +0000000000000");
		factory.execute(busiCtx, "01000");
		String reponse = (String) busiCtx.getResponseEntry();
		System.out.println("reponse:" + reponse);
	}

}
