package cn.com.yitong.actions.netloan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;

/**
 * 可抢单列表挡板数据
 */
@Component
public class Sp0020JsonOp extends AbstractOp implements IAresSerivce {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		logger.debug("--run--");
		List list = new ArrayList();
		Map map = new HashMap();
		Map map2 = new HashMap();
		ctx.getParamMap().put("result", "0");
		ctx.getParamMap().put("manager_code", "1001");
		ctx.getParamMap().put("reason_fail", "交易成功");
		
		map.put("community_x", "26.5");
		map.put("community_y", "26.5");
		map.put("borrower_name", "穆守博");
		map.put("borrower_phone", "18056621200");
		map.put("apply_time", "2018-08-13");
		map.put("house_name", "武汉江夏");
		map.put("borrower_idcard", "342012199405021236");
		map.put("house_estimate_value", "1000000000");
		map.put("address_pro", "湖北");
		map.put("address_city", "武汉");
		map.put("address_county", "江夏");
		map.put("address_detail", "武大园东路万科红郡B14#1单元1201室");
		map.put("bar_code", "1203");
		list.add(map);
		
		map2.put("community_x", "26.5");
		map2.put("community_y", "26.5");
		map2.put("borrower_name", "时伟");
		map2.put("borrower_phone", "18056621200");
		map2.put("apply_time", "2018-08-14");
		map2.put("house_name", "武汉江夏");
		map2.put("borrower_idcard", "342012199405021236");
		map2.put("house_estimate_value", "1000000000");
		map2.put("address_pro", "江苏");
		map2.put("address_city", "南京");
		map2.put("address_county", "江夏");
		map2.put("address_detail", "上海东路碧桂园B14#1单元1201室");
		map2.put("bar_code", "1203");
		list.add(map2);
		ctx.getParamMap().put("list", list);
		return NEXT;
	}
}
