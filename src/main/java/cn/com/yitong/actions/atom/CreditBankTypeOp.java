package cn.com.yitong.actions.atom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.util.common.StringUtil;

/**
 * 空任务
 */
@Component
public class CreditBankTypeOp extends AbstractOp implements IAresSerivce {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		String bankType = "";
		String payAcct = ctx.getParam("ACCT_NO");
		if (!StringUtil.isBlank(payAcct)) {
			String cardBin = payAcct.substring(0, 6);
			if ("628323".equals(cardBin)) {
				bankType = "001";
			}
			if ("625187".equals(cardBin)) {
				bankType = "002";
			}
		}
		ctx.setParam("BANK_NO", bankType);
		return NEXT;
	}
}
