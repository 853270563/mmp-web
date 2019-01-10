package cn.com.yitong.ares.mp.push;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.ares.mp.entity.MpCustMsg;

public abstract class AbstractPush implements Runnable {
	public static final Logger L = LoggerFactory.getLogger(AbstractPush.class);

	protected MpCustMsg custMsg;
	protected ICrudService service;
	protected boolean succ;
	private long startTime;

	public AbstractPush(MpCustMsg custMsg, ICrudService service) {
		this.custMsg = custMsg;
		this.service = service;
		this.succ = false;
	}

	public void doBefore() {
		this.startTime = System.currentTimeMillis();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void doAfter() {
		long takeTime = System.currentTimeMillis() - startTime;
		Map params = new HashMap();
		params.put("MSG_ID", custMsg.getMsgId());
		params.put("TAKE_TIME", takeTime);
		if (this.succ) {
			params.put("STATUS", "2");
			service.update("MP_MSG_PUSH.updateCustMsgStatus", params);
		} else {
			params.put("STATUS", "3");
			service.update("MP_MSG_PUSH.updateCustMsgStatus", params);
		}
		L.info("消息id:" + custMsg.getMsgId() + ",客户端类型:" + custMsg.getClientType() + ",发送结果:" + String.valueOf(succ)
				+ ",耗时:" + takeTime + "毫秒");
	}

	public abstract void push() throws Exception;

	@Override
	public void run() {
		doBefore();
		try {
			push();
			this.succ = true;
		} catch (Exception e) {
			e.printStackTrace();
			L.error(e.getMessage(), e);
			this.succ = false;
		}
		doAfter();
	}

}
