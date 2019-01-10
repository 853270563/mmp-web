package cn.com.yitong.actions.atom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.util.common.ValidUtils;

/**
 * 
 * 数据批量更新
 *
 */
@Component
public class DbBatchUpdOp extends AbstractOp implements IAresSerivce {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * @param: *sqlId 必输
	 * @param: *throw 是否忽略查询为空的，默认false；不忽略，查询为空报错；
	 * @param: *msgKey 错误码
	 * @param: *msgArgs 错误参数
	 */
	@Override
	public int execute(IBusinessContext ctx) {
		String statementName = ValidUtils.validEmpty("*sqlId", ctx.getParamMap());
		boolean isOk = getDao(ctx).batch4Update(statementName, ctx.getParamDatas(""));
		if (!isOk) {
			// 默认抛异常
			if (!"false".equalsIgnoreCase(ctx.getParam("*throw"))) {
				throwAresRuntimeException(ctx);
			}
		}
		return NEXT;
	}
}
