package cn.com.yitong.actions.as;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.net.INetTools;

/**
 * 安硕Http通讯组件
 */
@Component
public class NetToolsAsOp extends AbstractOp implements IAresSerivce {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	@Qualifier("netToolsAs")
	INetTools netTools;
	
	@Override
	public int execute(IBusinessContext ctx) {
		logger.debug("--run--");
		String transCode = ctx.getParam("*transCode");
		netTools.execute(ctx, transCode);
		return NEXT;
	}
}
