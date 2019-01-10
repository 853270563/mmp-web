package cn.com.yitong.actions.loanafter;

import java.sql.Date;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;

/**
 * @author luanyu
 * @date   2018年8月29日
 */
/**
 * 完善查询条件数据--贷后
 *
 * @author
 */
@Component
public class FinishQryDataOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-完善查询条件数据-run--");
		String start_date = ctx.getParam("START_DATE");
		if ("".equals(start_date)) {
			/*Date date = new Date(System.currentTimeMillis());
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.MONTH, -3);
			date = new java.sql.Date(calendar.getTime().getTime());*/
			
			
			//设置默认起始时间为当前时间
//			ctx.setParam("START_DATE",  new Date(System.currentTimeMillis()));
			
			Date date = new Date(System.currentTimeMillis());
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.YEAR, -1000);
			date = new java.sql.Date(calendar.getTime().getTime());
			ctx.setParam("START_DATE", date);
		}
		String end_date = ctx.getParam("END_DATE");
		if ("".equals(end_date)) {
			//设置默认结束时间为当前时间
//			ctx.setParam("END_DATE", new Date(System.currentTimeMillis()));
			
			Date date = new Date(System.currentTimeMillis());
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.YEAR, 1000);
			date = new java.sql.Date(calendar.getTime().getTime());
			ctx.setParam("END_DATE",date);
		}
		return NEXT;
	}

}
