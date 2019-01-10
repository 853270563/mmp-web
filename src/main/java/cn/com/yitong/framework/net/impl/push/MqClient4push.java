package cn.com.yitong.framework.net.impl.push;

import java.io.IOException;
import java.util.Hashtable;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.servlet.ServerInit;
import cn.com.yitong.framework.util.security.Converts;
import cn.com.yitong.util.FileUtil;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

import com.ibm.mq.MQC;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;

/**
 * MQ 工具集
 * 
 * @author yaoym
 */
@Component
public class MqClient4push implements IClientFactory {

	private Logger logger = YTLog.getLogger(MqClient4push.class);

	private MQQueueManager qMgr;

	private String qmName = ServerInit.getString("qmName"); // 隊列管理器名稱
	private String inQName = ServerInit.getString("inQName"); // 请求隊列 名稱
	private String outQName = ServerInit.getString("outQName"); // 響應隊列 名稱

	int i = 1;
	int j = 1;

	public void init() {
		// MQEnvironment.hostname = "172.31.252.205";
		// MQEnvironment.port = 1415;
		// MQEnvironment.channel = "CHANL_PUSH";
		// MQEnvironment.CCSID = 1208;// 字符集编码
		// MQEnvironment.userID = null; // 用户名
		// MQEnvironment.password = null; // 密码

		// logger.info("MQEnvironment.hostname:\t" + MQEnvironment.hostname);
		// logger.info("MQEnvironment.port:\t\t" + MQEnvironment.port);
		// logger.info("MQEnvironment.channel:\t\t" + MQEnvironment.channel);
		// logger.info("MQEnvironment.CCSID:\t\t" + MQEnvironment.CCSID);
		//
		// logger.debug("MQEnvironment.userID:\t\t" + MQEnvironment.userID);
		// logger.debug("MQEnvironment.password:\t\t" + MQEnvironment.password);

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
	public byte[] sendMessage(String message) {
		logger.info("qmName:\t\t\t" + qmName);
		logger.info("inQName:\t\t\t" + inQName);
		logger.info("outQName:\t\t\t" + outQName);

		MQQueue queue = null;
		MQMessage putMessage = null;
		try {
			logger.info("--------1----------");
			if (qMgr == null || !qMgr.isConnected()) {
				Hashtable properties = new Hashtable();
				properties.put("hostname", ServerInit.getString("MQHostName"));
				properties.put("port", ServerInit.getInt("MQPort"));
				properties.put("channel", ServerInit.getString("MQChannel"));
				properties.put("CCSID", ServerInit.getInt("MQCCSID"));
				qMgr = new MQQueueManager(qmName, properties);
			}

			int openOptions = MQC.MQOO_OUTPUT | MQC.MQOO_FAIL_IF_QUIESCING;
			queue = qMgr.accessQueue(inQName, openOptions);

			logger.info("--------2----------");

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

			// 放入消息
			putMessage.write(message.getBytes("utf-8"));

			logger.info("--------3----------");
			// 设置写入消息的属性
			MQPutMessageOptions pmo = new MQPutMessageOptions();

			// 指定队列管理器为消息生成新的消息ID 并将其设为MQMD 的MsgId 字段。
			pmo.options = MQC.MQPMO_NEW_MSG_ID + MQC.MQPMO_NEW_CORREL_ID;

			// 将消息写入队列
			queue.put(putMessage, pmo);

			// 提交消息
			qMgr.commit();

			logger.info("mq send success :"
					+ Converts.base64ToString(putMessage.messageId));
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
		return putMessage.messageId;
	}

	/**
	 * 接收响应
	 * 
	 * @param msgId
	 * @return
	 */
	public String getMessage(byte[] msgId) {
		String message = null;
		MQQueue queue = null;
		try {
			int openOptions = MQC.MQOO_INPUT_SHARED
					| MQC.MQOO_FAIL_IF_QUIESCING;
			// int openOptions = MQC.MQOO_OUTPUT | MQC.MQOO_FAIL_IF_QUIESCING;

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

			// 设置等待的时间限制(1分钟)
			gmo.waitInterval = 1000 * 60;

			// 根据消息ID匹配消息
			gmo.matchOptions = MQC.MQMO_MATCH_MSG_ID;

			// 创建消息缓存区
			MQMessage retrieve = new MQMessage();
			retrieve.messageId = msgId;

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
		return message;
	}

	@Override
	public boolean execute(IBusinessContext ctx, String transCode) {
		// 测试请求报文生成
		String rqctx = (String) ctx.getRequestEntry();
		String xmlStr = StringUtil.formatXmlStr(rqctx);
		if (logger.isDebugEnabled()) {
			logger.debug(transCode + " send xml msg: \n" + xmlStr);
		}
		try{
			// 放入報文到請求隊列
			byte[] msgId = sendMessage(xmlStr);
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
		} catch (Exception e) {
			logger.error("短信发送异常...", e);
		}
		return false;
	}

	@Override
	public void releaseSession(HttpClient httpClient, IBusinessContext ctx)
			throws Exception {
	}

	public static void main(String[] args) throws InterruptedException {
		MqClient4push util = new MqClient4push();
		util.init();
		String filePath = "E:/temp/caches/ibs/sms/TEST.xml";
		String reqMsg = FileUtil.readFileAsString(filePath);
		// 放入報文到請求隊列
		byte[] msgId = util.sendMessage(reqMsg);
		// 根据消息id从响应报文中获取响应
		// byte[] msgId = "1".getBytes();
		String rspMsg = util.getMessage(msgId);
		System.out.println("rsp:" + rspMsg);
	}

}
