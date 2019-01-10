package cn.com.yitong.actions.atom;

import org.springframework.stereotype.Component;

import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.ares.flow.plugin.ITransationBeginOp;
import cn.com.yitong.framework.base.IBusinessContext;

/**
 * 事务开始
 */
@Component
public class TransactionBeginOp implements IAresSerivce, ITransationBeginOp {

	public int execute(IBusinessContext ctx) {
		return NEXT;
	}
}
