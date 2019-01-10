package cn.com.yitong.ares.mp.push;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JmsListener implements MessageListener {
	private static final Logger L = LoggerFactory.getLogger(MessageListener.class);
//	@Autowired
//	PushDispatcher pushDispatcher;

	@Override
	public void onMessage(Message message) {
		if (message instanceof TextMessage) {
//			try {
//				CustMsg msg = JsonUtils.parseObject(((TextMessage) message).getText(), CustMsg.class);
//				pushDispatcher.push2Client(msg);
//			} catch (JMSException ex) {
//				throw new RuntimeException(ex);
//			}
		} else {
			L.warn("消息类型错误，非TextMessage");
			throw new IllegalArgumentException("Message must be of type TextMessage");
		}
	}

}
