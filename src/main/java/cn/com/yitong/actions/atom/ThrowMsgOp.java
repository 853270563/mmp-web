package cn.com.yitong.actions.atom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;

/**
 * 
 * 抛异常
 */
@Component
public class ThrowMsgOp extends AbstractOp implements IAresSerivce {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * @param *msgKey,缺省值common.error.undefined
	 * @param *msgArgs,缺省为空
	 */
	@Override
	public int execute(IBusinessContext ctx) {
		logger.info("自定义异常");
		// String msgKey = ctx.getParam("*msgKey", "common.error.undefined");
		// String msgArgs = ctx.getParam("*msgArgs", "");
		throwAresRuntimeException(ctx);
		return EXIT;
	}
}
