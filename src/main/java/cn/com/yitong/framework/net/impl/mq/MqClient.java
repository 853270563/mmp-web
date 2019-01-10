package cn.com.yitong.framework.net.impl.mq;

import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.core.vo.TransLogBean;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.util.security.Converts;
import cn.com.yitong.util.FileUtil;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

/**
 * MQ 工具集
 * 
 * @author yaoym
 */
@Component
public class MqClient implements IClientFactory {

	private Logger logger = YTLog.getLogger(MqClient.class);

	private static MQQueueManager qMgr;

	private static String qmName; // 隊列管理器名稱
	private static String outQName; // 響應隊列 名稱
	private static String inQName; // 请求隊列 名稱

	private boolean inited = false;

	public void init() {
		inited = true;
		MQEnvironment.hostname = "172.31.252.205";
		MQEnvironment.port = 1415;
		MQEnvironment.channel = "CHANL_BOCCC";// 前端渠道
		MQEnvironment.CCSID = 1208;// 字符集编码
		// MQEnvironment.userID = null; // 用户名
		// MQEnvironment.password = null; // 密码

		qmName = "MB8QMGR";

		// inQName = "BCC.TFB.EREPL06"; // 写入队列
		// outQName = "TFB.BCC.EREQS06"; // 响应队列
		inQName = "TFB.BCC.EREQS06"; // 写入队列
		outQName = "BCC.TFB.EREPL06"; // 响应队列

		logger.info("MQEnvironment.hostname:\t" + MQEnvironment.hostname);
		logger.info("MQEnvironment.port:\t\t" + MQEnvironment.port);
		logger.info("MQEnvironment.channel:\t\t" + MQEnvironment.channel);
		logger.info("MQEnvironment.CCSID:\t\t" + MQEnvironment.CCSID);

		logger.debug("MQEnvironment.userID:\t\t" + MQEnvironment.userID);
		logger.debug("MQEnvironment.password:\t\t" + MQEnvironment.password);

		logger.info("qmName:\t\t\t" + qmName);
		logger.info("inQName:\t\t\t" + inQName);
		logger.info("outQName:\t\t\t" + outQName);
	}

	/**
	 * 请求发送
	 * 
	 * @param message
	 * @return 消息ID
	 */
	public String sendMessage(String id, String message) {
		MQQueue queue = null;
		MQMessage putMessage = null;
		boolean fixedMsgId = StringUtil.isNotEmpty(id);
		String msgId = null;
		try {
			logger.debug("--------1----------");
			if (qMgr == null || !qMgr.isConnected()) {
				qMgr = new MQQueueManager(qmName);
			}

			int openOptions = MQC.MQOO_OUTPUT | MQC.MQOO_FAIL_IF_QUIESCING;
			queue = qMgr.accessQueue(inQName, openOptions);

			logger.debug("--------2----------");

			// 创建消息缓存区
			putMessage = new MQMessage();

			// 设置MQMD 格式字段
			putMessage.format = MQC.MQFMT_STRING;

			// 设置消息优先级，数字越大优先级越大，范围0-9，网银暂定为5
			putMessage.priority = 5;

			// 消息过期时间（单位0.1秒），5分钟内没有处理，消息自动清除
			putMessage.expiry = 3000;

			// 设置消息编码（utf-8）
			putMessage.encoding = 1208;
			if (fixedMsgId) {
				putMessage.messageId = id.getBytes();
			}

			// 放入消息
			putMessage.write(message.getBytes("utf-8"));

			logger.debug("--------3----------");
			// 设置写入消息的属性
			MQPutMessageOptions pmo = new MQPutMessageOptions();

			// 指定队列管理器为消息生成新的消息ID 并将其设为MQMD 的MsgId 字段。
			pmo.options = MQC.MQPMO_NEW_MSG_ID + MQC.MQPMO_NEW_CORREL_ID;

			// 将消息写入队列
			queue.put(putMessage, pmo);

			// 提交消息
			qMgr.commit();
			if (fixedMsgId) {
				msgId = id;
			} else {
				msgId = Converts.base64ToString(putMessage.messageId);
			}
			logger.info("mq send success: " + msgId);
		} catch (MQException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				queue.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return msgId;
	}

	/**
	 * 接收响应
	 * 
	 * @param msgId
	 * @return
	 */
	public String getMessage(String msgId) {
		if (StringUtil.isEmpty(msgId)) {
			return null;
		}
		String message = null;
		MQQueue queue = null;
		try {
			int openOptions = MQC.MQOO_INPUT_SHARED
					| MQC.MQOO_FAIL_IF_QUIESCING;

			/* 关闭了就重新打开 */
			if (qMgr == null || !qMgr.isConnected()) {
				qMgr = new MQQueueManager(qmName);
			}

			// 打开响应队列
			queue = qMgr.accessQueue(outQName, openOptions);

			// 设置写入消息的属性
			MQGetMessageOptions gmo = new MQGetMessageOptions();

			// 在同步点控制下获取消息
			gmo.options += MQC.MQGMO_SYNCPOINT;

			// 如果在队列上没有消息则等待
			gmo.options += MQC.MQGMO_WAIT;

			// 如果队列管理器停顿则失败
			gmo.options += MQC.MQGMO_FAIL_IF_QUIESCING;

			// 设置等待的时间限制(30秒)
			gmo.waitInterval = 1000 * 20;

			// 根据消息ID匹配消息
			gmo.matchOptions = MQC.MQMO_MATCH_MSG_ID;

			// 创建消息缓存区
			MQMessage retrieve = new MQMessage();
			retrieve.messageId = Converts.strToBase64(msgId);

			// 从队列中取出消息
			queue.get(retrieve, gmo);

			byte[] bytes = new byte[retrieve.getDataLength()];
			retrieve.readFully(bytes);
			message = new String(bytes, "UTF-8");
			qMgr.commit();
		} catch (MQException ex) {
			if (ex.reasonCode == 2033) {
				System.out.println("沒有消息...");
			} else {
				ex.printStackTrace();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				queue.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		logger.debug("rspxml is :\n" + message);
		return message;
	}

	@Override
	public boolean execute(IBusinessContext ctx, String transCode) {
		if(!inited){
			init();
		}
		// 测试请求报文生成
		String xmlStr = (String) ctx.getRequestEntry();
		if (logger.isDebugEnabled()) {
			logger.debug(transCode + " send xml msg: \n" + xmlStr);
		}
		// 放入報文到請求隊列
		String msgId = null;
		msgId = sendMessage(msgId, xmlStr);
		if (msgId == null) {
			ctx.setErrorInfo(AppConstants.STATUS_FAIL,
					"CRT MQ请求发送发生异常，请检查MQ环境！", transCode);
			return false;
		}
		// 根据消息id从响应报文中获取响应
		String rspStr = getMessage(msgId);
		if (StringUtil.isEmpty(rspStr)) {
			ctx.setErrorInfo(AppConstants.STATUS_FAIL,
					"CRT MQ响应消息发生异常，请检查MQ环境！", transCode);
			return false;
		}
		ctx.setResponseEntry(rspStr);
		return true;
	}

	@Override
	public void releaseSession(HttpClient httpClient, IBusinessContext ctx)
			throws Exception {
	}

	public static void main(String[] args) throws InterruptedException {
//		MqClient util = new MqClient();
//		util.init();
//		String filePath = "D:/works/push_space/ibanking/WebContent/WEB-INF/conf/btt/07/PD07001Op.xml";
//		String reqMsg = FileUtil.readFileAsString(filePath);
//		String msgId = "" + (new Date().getTime() % 10000);

		String transCode = "PP07003Op";
		IBusinessContext ctx = new BusinessContext();
		TransLogBean bean = ctx.getTransLogBean(transCode);
		bean.setTransSeqNo("01");
		ctx.setParam("NID", "73381840");
		ctx.setParam("PAN", "5441070027120715");
		ctx.setParam("EstatFg", "Y");

		IRequstBuilder requestBuilder = new RequestBuilder4mqTest();
		IEBankConfParser confParser = new EBankConfParser4mqtest();
		if (!requestBuilder.buildSendMessage(ctx, confParser, transCode)) {
			System.out.println("---------buildSendMessage error----------");
		}
		// util.execute(ctx, transCode);

		String filePath2 = "D:/works/push_space/ibanking/WebContent/WEB-INF/data/EAI/ASM-SETU01_OUT.xml";
		String rspMsg = FileUtil.readFileAsString(filePath2);
		ctx.setResponseEntry(rspMsg);
		UrlClient4mq client4mq = new UrlClient4mq();
//		client4mq.execute(ctx, transCode);
		IResponseParser responseParser = new ResponseParser4mqTest();
		responseParser.parserResponseData(ctx, confParser, transCode);

		System.out.println(">> base context:\n"
				+ StringUtil.formatXmlStr(ctx.getResponseContext(transCode)
						.asXML()));

		 // 放入報文到請求隊列
//		 msgId = util.sendMessage(msgId, reqMsg);
//		 // 根据消息id从响应报文中获取响应
//		 String rspMsg = util.getMessage(msgId);
//		 System.out.println("mq response msg:\n" + rspMsg);
	}

}
