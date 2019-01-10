package cn.com.yitong.actions.atom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.util.common.ValidUtils;

/**
 * 
 * 数据不分页查询
 */
@Component
public class DbListQueryOp extends AbstractOp implements IAresSerivce {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * @param: *listName 列表输出字段名
	 * @param: *sqlId 必输
	 * @param: *throw 是否忽略查询为空的，默认true,忽略查询为空报错
	 * @param: *msgKey 错误码
	 * @param: *msgArgs 错误参数
	 */
	@Override
	public int execute(IBusinessContext ctx) {
		logger.debug("--run--");
		// Assert.hasText(ctx.getParam("sqlId"),"请配置sqlId");
		String statementName = ValidUtils.validEmpty("*sqlId", ctx.getParamMap());
		Map paramMap = new HashMap();
		paramMap.putAll(ctx.getParamMap());
		//paramMap.putAll(ctx.getHeadMap());
		List rs = getDao(ctx).queryForList(statementName, paramMap);
		if (null == rs || rs.isEmpty()) {
			if ("true".equalsIgnoreCase(ctx.getParam("*throw"))) {
				throwAresRuntimeException(ctx);
			}
		}
		String listName = ctx.getParam("*listName", "LIST");
		// 清理内部参数
		ctx.setParam(listName, rs);
		return NEXT;
	}
}
