package cn.com.yitong.framework.net.impl.crm;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.error.AresRuntimeException;
import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.servlet.ServerInit;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

@Component
public class SocketFactory4crm implements IClientFactory {
	private Logger logger = YTLog.getLogger(this.getClass());
	
	public final String SERVER_IP = ServerInit.getString("ESB_SERVER_IP");
	public final int SERVER_PORT = ServerInit.getInt("ESB_SERVER_PORT");

	@Override
	public boolean execute(IBusinessContext busiCtx, String transCode) {
		if(logger.isDebugEnabled()){
			logger.info("SocketFactory4stock.execute.........start......");
		}
		ComServerSocketcredit client = new ComServerSocketcredit();
		try {
			if (!client.init(SERVER_IP, SERVER_PORT)) {
				return false;
			}
			long startTime = System.currentTimeMillis();
			if (!client.sendMessage((String) busiCtx.getRequestEntry())) {
				return false;
			}
			String response = client.receiveMessage();
			long useTime = System.currentTimeMillis() - startTime;
			
			logger.info("交易用时! spend time:" + useTime + "ms");
			if (StringUtil.isNotEmpty(response)) {
				busiCtx.setResponseEntry(response);
			} else {
				throw new AresRuntimeException(AppConstants.STATUS_FAIL, "交易接收消息为空");
			}
		} catch (RuntimeException e) {
			// TODO: handle exception
			logger.warn("主机交易通讯异常", e);
			throw e;
		} catch (Exception e) {
			return false;
		} finally {
			client.close();
		}
		return true;
	}

	@Override
	public void releaseSession(HttpClient httpClient, IBusinessContext busiCtx)
			throws Exception {
		// TODO Auto-generated method stub
	}

	public static void main(String[] args) {
		SocketFactory4crm factory = new SocketFactory4crm();
		IBusinessContext busiCtx = new BusinessContext();
		busiCtx.setRequestEntry("----------------");
		factory.execute(busiCtx, "");
		String reponse = (String) busiCtx.getResponseEntry();
		System.out.println("reponse:" + reponse);
	}

}
