package cn.com.yitong.actions.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.net.INetTools;
import cn.com.yitong.util.common.ValidUtils;

/**
 * @author luanyu
 * @date   2018年8月15日
 */
/**
*网贷核心Http通讯
*@author
*/
@Service
public class NetToolsNetCoreOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	@Qualifier("netTools4CreditCore")
	INetTools netTools;
	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-网贷核心Http通讯-run--");
		String transCode = ValidUtils.validEmpty("*transCode", ctx.getParamMap());
		netTools.execute(ctx, transCode);
		return NEXT;
	}

}
