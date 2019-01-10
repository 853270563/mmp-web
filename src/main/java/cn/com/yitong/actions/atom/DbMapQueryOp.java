package cn.com.yitong.actions.atom;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.util.common.ValidUtils;

/**
 * 
 * Map查询
 */
@Component
public class DbMapQueryOp extends AbstractOp implements IAresSerivce {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * @param: *sqlId 必输
	 * @param: *throw 是否忽略查询为空的，默认false；不忽略，查询为空报错；
	 * @param: *msgKey 错误码
	 * @param: *msgArgs 错误参数
	 */
	@Override
	public int execute(IBusinessContext ctx) {
		// Assert.hasText(ctx.getParam("sqlId"), "请配置sqlId");
		String statementName = ValidUtils.validEmpty("*sqlId", ctx.getParamMap());
		Map paramMap = new HashMap();
		paramMap.putAll(ctx.getParamMap());
		//paramMap.putAll(ctx.getHeadMap());
		Map rspMap = getDao(ctx).queryForMap(statementName, paramMap);
		if (rspMap == null) {
			if ("true".equalsIgnoreCase(ctx.getParam("*throw"))) {// 默认不抛异常
				throwAresRuntimeException(ctx);
			}
		} else {
			// 获取返回数据
			ctx.getParamMap().putAll(rspMap);
		}
		return NEXT;
	}
}