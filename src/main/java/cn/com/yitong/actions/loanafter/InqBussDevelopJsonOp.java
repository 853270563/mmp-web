package cn.com.yitong.actions.loanafter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;

/**
 * @author luanyu
 * @date   2018年8月29日
 */
/**
 * 业务发展报表挡板数据
 * @author
 */
@Component
public class InqBussDevelopJsonOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-业务发展报表挡板数据-run--");
		List list = new ArrayList();
		Map map = new HashMap();
		ctx.getParamMap().put("STATUS", "1");
		ctx.getParamMap().put("MSG", "交易成功");
		
		map.put("USER_NO", "1001");
		map.put("OVERDUE_AMOUNT", "10000");
		map.put("OVERDUE_RATE", "0");
		map.put("BAD_AMOUNT", "20000");
		map.put("BAD_RATE", "0");
		
		list.add(map);
		ctx.getParamMap().put("REPORTLIST", list);
		return NEXT;
	}

}
