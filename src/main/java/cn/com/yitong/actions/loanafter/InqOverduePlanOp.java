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
 * 未上传影像的用户
 */
@Component
public class InqOverduePlanOp extends AbstractOp implements IAresSerivce {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		logger.debug("--未上传影像的用户run--");
		List<Map> balancelist = (List) ctx.getParamMap().get("balancelist");
		List<Map> nocheckbalancelist = null;
		if (balancelist != null) {
			nocheckbalancelist = new ArrayList<Map>();
			for (int i = 0, k = balancelist.size(); i < k; i++) {
				Map map = balancelist.get(i);
				String status = (String) map.get("status");
				if ("0".equals(status)) {
					nocheckbalancelist.add(map);
				}
			}
		}
		ctx.getParamMap().put("balancelist", nocheckbalancelist);
		return NEXT;
	}
}
