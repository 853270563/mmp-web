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
 * 贷款检查是否通知过滤
 */
@Component
public class InqLoanCheckOp extends AbstractOp implements IAresSerivce {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		logger.debug("--贷款检查是否通知过滤run--");
		List<Map> checklist = (List) ctx.getParamMap().get("checklist");
		List<Map> nonoticechecklist = null;
		if (checklist != null) {
			nonoticechecklist = new ArrayList<Map>();
			for (int i = 0, k = checklist.size(); i < k; i++) {
				Map map = checklist.get(i);
				String is_mobile = (String) map.get("is_mobile");
				if ("0".equals(is_mobile)) {
					nonoticechecklist.add(map);
				}
			}
		}
		ctx.getParamMap().put("checklist", nonoticechecklist);
		return NEXT;
	}
}
