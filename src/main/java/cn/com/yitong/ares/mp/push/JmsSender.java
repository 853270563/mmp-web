package cn.com.yitong.ares.mp.push;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

//import org.apache.activemq.command.ActiveMQTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.core.ProducerCallback;

import cn.com.yitong.common.utils.JsonUtils;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.ares.mp.entity.MpCustMsg;
import cn.com.yitong.util.DateUtil;
import org.springframework.stereotype.Component;

public class JmsSender {
	private static final Logger L = LoggerFactory.getLogger(JmsSender.class);

	@Autowired
	private JmsTemplate jmsTemplateSend;
	@Value("${app.mq.sendDestinationName}")
	private String destination;
	@Autowired
	ICrudService service;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void sendPushMesaage(MpCustMsg custMsg) throws Exception {
		long timeToLive = Message.DEFAULT_TIME_TO_LIVE;
		if (custMsg.getSendEndTime() != null) {
			timeToLive = custMsg.getSendEndTime().getTime() - System.currentTimeMillis();
		}
		if (timeToLive >= 0) {
			// int priority = Message.DEFAULT_PRIORITY;
			// if (custMsg.getPrioVal() != null &&
			// custMsg.getPrioVal().intValue() >= 0 &&
			// custMsg.getPrioVal().intValue() <= 9) {
			// priority = custMsg.getPrioVal().intValue();
			// }
			Map<String, Object> msgMap = new HashMap<String, Object>();
			msgMap.put("MSG_TITLE", custMsg.getMsgTitle());
			msgMap.put("MSG_TYPE", custMsg.getMsgType());
			msgMap.put("MSG_BODY", custMsg.getMsgBody());
			msgMap.put("DIST_VAL", custMsg.getDistVal());
			msgMap.put("CLICK_ACT_TYPE", custMsg.getClickActType());
			msgMap.put("CLICK_ACT_BODY", custMsg.getClickActBody());
			msgMap.put("SEND_DTIME", DateUtil.getDate() + " " + DateUtil.getTime());
			// String msg = JsonUtils.objectToJson(custMsg);
			// sendTextMessage(msg, priority, timeToLive);
			// sentToTopic(msg);
			sentToTopic(custMsg, JsonUtils.objectToJson(msgMap));
		} else {
			L.warn("消息id=" + custMsg.getMsgId() + "已过期，丢弃发送");
			Map params = new HashMap();
			params.put("MSG_ID", custMsg.getMsgId());
			params.put("STATUS", "3");
			service.update("MP_MSG_PUSH.updateCustMsgStatus", params);
		}
	}

	public void sendTextMessage(final String text, final int priority, final long timeToLive) {
		jmsTemplateSend.execute(destination, new ProducerCallback<String>() {
			@Override
			public String doInJms(Session session, MessageProducer producer) throws JMSException {
				producer.send(session.createTextMessage(text), jmsTemplateSend.getDeliveryMode(), priority, timeToLive);
				return text;
			}
		});
	}
	
	public void sentToTopic(final MpCustMsg custMsg, final String text) throws Exception{
		
		/*Connection connection = null;
		Session session = null;
		ActiveMQTopic topic = null;
		
		try {
			connection = jmsTemplateSend.getConnectionFactory().createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		//	topic = (ActiveMQTopic)session.createTopic(custMsg.getClientId());

			jmsTemplateSend.send(topic, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createTextMessage(text);
				}
			});
		} catch (JMSException e) {
			throw e;
		} finally {
			if (connection != null) {
				connection.close();
			}
			if(session != null){
				session.close();
			}
		}*/
	}
}
