package cn.com.yitong.actions.netloan;

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
 * 网贷平台Http通讯组件
 */
@Component
public class NetToolsNetLoanOp extends AbstractOp implements IAresSerivce {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	@Qualifier("netToolsNetLoan")
	INetTools netTools;
	
	@Override
	public int execute(IBusinessContext ctx) {
		logger.debug("--run--");
		String transCode = ctx.getParam("*transCode");
		netTools.execute(ctx, transCode);
		return NEXT;
	}
}
