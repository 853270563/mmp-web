package cn.com.yitong.ares.mp.push;

import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.ares.mp.entity.MpCustMsg;
import cn.com.yitong.util.SpringContextHolder;


public class JmsPush extends AbstractPush {


	private JmsSender jmsSender;

	public JmsPush(MpCustMsg custMsg, ICrudService service) {
		super(custMsg, service);
		jmsSender = SpringContextHolder.getBean("jmsSender");
	}

	@Override
	public void push() throws Exception {
		jmsSender.sendPushMesaage(custMsg);
	}

}
