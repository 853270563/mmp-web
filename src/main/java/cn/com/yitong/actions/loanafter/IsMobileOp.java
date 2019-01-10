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
 * 查询未通知过滤
 */
@Component
public class IsMobileOp extends AbstractOp implements IAresSerivce {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		logger.debug("--查询未通知过滤run--");
		Object chickListName = ctx.getParamMap().get("*chickListName");
		List<Map> chickList = (List) ctx.getParamMap().get(chickListName);
		List<Map> nonoticelist = null;
		if (chickList != null) {
			nonoticelist = new ArrayList<Map>();
			for (int i = 0, k = chickList.size(); i < k; i++) {
				Map map = chickList.get(i);
				String is_mobile = (String) map.get("IS_MOBILE");
				if ("0".equals(is_mobile)) {
					nonoticelist.add(map);
				}
			}
		}
		ctx.getParamMap().put(chickListName, nonoticelist);
		return NEXT;
	}
}
