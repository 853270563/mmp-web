package cn.com.yitong.actions.loanafter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;

/**
 * 风险客户逾期检查列表
 */
@Component
public class CustOverdueCheckOp extends AbstractOp implements IAresSerivce {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		logger.debug("--风险客户逾期检查列表run--");
		List<Map> checklist = (List) ctx.getParamMap().get("CHECKLIST");
		List<Map> custoverduelist = null;
		if (checklist != null) {
			custoverduelist = new ArrayList<Map>();
			for (int i = 0, k = checklist.size(); i < k; i++) {
				Map map = checklist.get(i);
				Object check_reason = map.get("CHECK_REASON");
				if ("3".equals(check_reason)) {
					custoverduelist.add(map);
				}
			}
		}
		ctx.getParamMap().put("CHECKLIST", custoverduelist);
		return NEXT;
	}
}
