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
 * 数据分页查询
 */
@Component
public class DbPageQueryOp extends AbstractOp implements IAresSerivce {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		// Assert.hasText(ctx.getParam("sqlId"),"请配置sqlId");
		String statementName = ValidUtils.validEmpty("*sqlId", ctx.getParamMap());
		Map paramMap = new HashMap();
		paramMap.putAll(ctx.getParamMap());
		paramMap.putAll(ctx.getHeadMap());
		List rs = getDao(ctx).pageQuery(statementName, paramMap, ctx);
		if (null==rs || rs.size() == 0) {
			if ("true".equalsIgnoreCase(ctx.getParam("*throw"))) {// 默认不抛异常
				throwAresRuntimeException(ctx);
			}
		}
		String listName = ctx.getParam("*listName", "LIST");
		// 清理内部参数
		ctx.setParam(listName, rs);
		return NEXT;
	}
}
