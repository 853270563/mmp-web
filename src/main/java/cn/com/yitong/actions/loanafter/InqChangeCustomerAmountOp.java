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
 * 风险客户合同变更列表
 */
@Component
public class InqChangeCustomerAmountOp extends AbstractOp implements
		IAresSerivce {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		logger.debug("--风险客户合同变更列表run--");
		List<Map> totalamountlist = (List) ctx.getParamMap().get("TOTALAMOUNTLIST");
		List<Map> normalcreditlist = null;
		if (totalamountlist != null) {
			normalcreditlist = new ArrayList<Map>();
			for (int i = 0, k = totalamountlist.size(); i < k; i++) {
				Map map = totalamountlist.get(i);
				String total_status = (String) map.get("TOTAL_STATUS");
				if ("0".equals(total_status)) {
					normalcreditlist.add(map);
				}
			}
		}
		ctx.getParamMap().put("TOTALAMOUNTLIST", normalcreditlist);
		return NEXT;
	}
}
