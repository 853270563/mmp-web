package cn.com.yitong.actions.atom;

import org.springframework.stereotype.Component;

import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.ares.flow.plugin.ITransationEndOp;
import cn.com.yitong.framework.base.IBusinessContext;

/**
 * 事务提交
 */
@Component
public class TransactionEndOp implements IAresSerivce, ITransationEndOp {

	public int execute(IBusinessContext ctx) {
		return NEXT;
	}
}
