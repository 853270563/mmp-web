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
 * 数据更新
 */
@Component
public class DbUpdOp extends AbstractOp implements IAresSerivce {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx){
		String statementName = ValidUtils.validEmpty("*sqlId",ctx.getParamMap());
		Map paramMap = new HashMap();
		paramMap.putAll(ctx.getParamMap());
		paramMap.putAll(ctx.getHeadMap());
		boolean isOk = getDao(ctx).update(statementName, paramMap);
		if (!isOk) {
			if ("true".equalsIgnoreCase(ctx.getParam("*throw"))) {// 默认不抛异常
				throwAresRuntimeException(ctx);
			}
		}
		return NEXT;
	}

}
