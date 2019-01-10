package cn.com.yitong.actions.atom;

import java.util.Date;

import org.springframework.stereotype.Component;

import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.util.common.DateUtil;
import cn.com.yitong.util.common.ValidUtils;

@Component
public class RemindDateAutoDeal extends AbstractOp implements IAresSerivce {

	@Override
	public int execute(IBusinessContext ctx) {
		
		String remindDate_after = ValidUtils.validEmpty("dealDate", ctx.getParamMap());
		
		String today = DateUtil.todayStr();
		
		//将当前年份月份与账单日拼接作为提醒日
		String remind_date = today.substring(0, 8).concat(remindDate_after);
		
		Date today1 = DateUtil.parseDate(today);
		Date remind_date1 = DateUtil.parseDate(remind_date);
		
		if(remind_date1.before(today1)){
			remind_date = DateUtil.getNextMonthDay(today, Integer.parseInt(remindDate_after));
		}
		
		ctx.setParam("REMIND_DATE", remind_date);
		
		return NEXT;
	}
	
	/*public static void main(String[] args) {
		String today = "2017-08-01";
		
		//将当前年份月份与账单日拼接作为提醒日
		String remind_date = today.substring(0, 8).concat("10");
		
		Date today1 = DateUtil.parseDate(today);
		Date remind_date1 = DateUtil.parseDate(remind_date);
		
		if(remind_date1.before(today1)){
			System.out.println("***");
			remind_date = DateUtil.getNextMonthDay(today, Integer.parseInt("10"));
		}
		System.out.println("remind_date="+remind_date);
	}*/
	
}
