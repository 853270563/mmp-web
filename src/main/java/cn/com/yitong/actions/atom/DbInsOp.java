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
 * 数据新增
 */
@Component
public class DbInsOp extends AbstractOp implements IAresSerivce {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * @param: *sqlId 必输
	 */
	@Override
	public int execute(IBusinessContext ctx) {
		// Assert.hasText(ctx.getParam("sqlId"), "请配置sqlId");
		Map paramMap = new HashMap();
		paramMap.putAll(ctx.getParamMap());
		paramMap.putAll(ctx.getHeadMap());
		logger.debug("db params:{}", paramMap);
		String statementName = ValidUtils.validEmpty("*sqlId", ctx.getParamMap());
		getDao(ctx).insert(statementName, paramMap);
		return NEXT;
	}

}
