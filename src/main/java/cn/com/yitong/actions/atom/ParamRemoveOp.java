package cn.com.yitong.actions.atom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.util.common.StringUtil;

/**
 * 总线值清理，常量参数可以用,或，或空格分隔
 * 
 * @author yaoyimin
 *
 */
@Service
public class ParamRemoveOp implements IAresSerivce {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * @param *remove 需清理的总线的 KEY；清理多个值时用逗号隔开；若全部清理，则设置为*
	 */
	@Override
	public int execute(IBusinessContext ctx) {
		logger.debug("--run--");
		String varsParam = ctx.getParam("*remove", "");
		// 全部清理
		if ("*".equals(varsParam)) {
			ctx.getParamMap().clear();
			return NEXT;
		}
		String[] vars = varsParam.split(",|，|\\s+");
		for (String var : vars) {
			if (StringUtil.isEmpty(var)) {
				continue;
			}
			ctx.removeParam(var);
		}
		return NEXT;
	}
}