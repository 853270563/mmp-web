package cn.com.yitong.actions.atom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;

/**
 * 空任务
 */
@Component
public class CheckPerson4TransferOp extends AbstractOp implements IAresSerivce {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		logger.debug("--run--");
		// RECVIDNO1付款人借记卡证件号
		// RECVIDNO3付款人证件号（电子账号）
		// RECVIDNO4收款人证件号（电子账户）
		// RECVIDNO2收款人证件号(借记卡)
		// RECVIDNO5 收款人证件号（信用卡）
		if ((null != ctx.getParam("RECVIDNO1") && null != ctx.getParam("RECVIDNO4")
				&& ctx.getParam("RECVIDNO1").equals(ctx.getParam("RECVIDNO4")))
				|| (null != ctx.getParam("RECVIDNO1") && null != ctx.getParam("RECVIDNO2")
						&& ctx.getParam("RECVIDNO1").equals(ctx.getParam("RECVIDNO2")))
				|| (null != ctx.getParam("RECVIDNO3") && null != ctx.getParam("RECVIDNO4")
						&& ctx.getParam("RECVIDNO3").equals(ctx.getParam("RECVIDNO4")))
				|| (null != ctx.getParam("RECVIDNO1") && null != ctx.getParam("RECVIDNO2")
						&& ctx.getParam("RECVIDNO1").equals(ctx.getParam("RECVIDNO2")))
				|| (null != ctx.getParam("RECVIDNO1") && null != ctx.getParam("RECVIDNO5")
						&& ctx.getParam("RECVIDNO1").equals(ctx.getParam("RECVIDNO5")))
				|| (null != ctx.getParam("RECVIDNO3") && null != ctx.getParam("RECVIDNO5")
						&& ctx.getParam("RECVIDNO3").equals(ctx.getParam("RECVIDNO5")))) {
			ctx.setParam("SELF_FLAG", "0");
		} else {
			ctx.setParam("SELF_FLAG", "1");
		}
		return NEXT;
	}
}
