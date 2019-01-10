package cn.com.yitong.framework.net.impl.bankcs;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.servlet.ServerInit;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

@Component
public class SocketFactory4bankcs implements IClientFactory {
	private Logger logger = YTLog.getLogger(this.getClass());
	
	public final String SERVER_IP = ServerInit.getString("BANKCS_SERVER_IP");
	public final int SERVER_PORT = ServerInit.getInt("BANKCS_SERVER_PORT");

	@Override
	public boolean execute(IBusinessContext busiCtx, String transCode) {
		if(logger.isDebugEnabled()){
			logger.info("SocketFactory4stock.execute.........start......");
		}
		ComServerSocket client = new ComServerSocket();
		try {
			if (!client.init(SERVER_IP, SERVER_PORT)) {
				return false;
			}
			if (!client.sendMessage((String) busiCtx.getRequestEntry())) {
				return false;
			}
			String response = client.receiveMessage();
			if (StringUtil.isNotEmpty(response)) {
				if(logger.isDebugEnabled()){
					logger.debug("====response===="+response);
				}
				busiCtx.setResponseEntry(response);
			} else {
				busiCtx.setErrorInfo(AppConstants.STATUS_FAIL, "交易接收消息为空",
						transCode);
				return false;
			}
		} catch (Exception e) {
			busiCtx.setErrorInfo(AppConstants.STATUS_FAIL, "交易通讯异常", transCode);
			logger.warn("主机交易通讯异常", e);
			return false;
		} finally {
			client.close();
		}
		if(logger.isDebugEnabled()){
			logger.debug("SocketFactory4stock.execute.......success........");
		}
		return true;
	}

	@Override
	public void releaseSession(HttpClient httpClient, IBusinessContext busiCtx)
			throws Exception {
		// TODO Auto-generated method stub
	}

	public static void main(String[] args) {
		SocketFactory4bankcs factory = new SocketFactory4bankcs();
		IBusinessContext busiCtx = new BusinessContext();
		busiCtx.setRequestEntry("----------------");
		factory.execute(busiCtx, "");
		String reponse = (String) busiCtx.getResponseEntry();
		System.out.println("reponse:" + reponse);
	}

}
