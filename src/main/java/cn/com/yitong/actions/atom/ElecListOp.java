package cn.com.yitong.actions.atom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;

/**
 * 空任务
 */
@Component
public class ElecListOp extends AbstractOp implements IAresSerivce {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		List newList = new ArrayList();
		List list = ctx.getParamDatas("LIST");
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			Map map = (Map) iterator.next();
			if (map.get("ACCT_TYPE").equals("04")) {
				newList.add(map);
			}
		}
		ctx.setParam("LIST", newList);
		return NEXT;
	}
}
