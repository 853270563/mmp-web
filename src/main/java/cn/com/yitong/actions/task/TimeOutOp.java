package cn.com.yitong.actions.task;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.ares.flow.IFlowTool;
import cn.com.yitong.framework.base.IBusinessContext;

/**
 * @author luanyu
 * @date   2018年10月10日
 */
/**
*加入超时
*@author
*/
@Service
public class TimeOutOp extends AbstractOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private IFlowTool flowTool;
	java.util.concurrent.locks.Lock lock = new ReentrantLock();
	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-加入超时-run--");
		List<Map<String, Object>> paramDatas = ctx.getParamDatas("list");
		if (paramDatas != null && !paramDatas.isEmpty()) {
			try {

				lock.lock();
			for (Map<String, Object> map : paramDatas) {
				ctx.setParam("TASK_ID", map.get("bar_code"));
				int queryForInt = getDao(ctx).queryForInt("rushTask.count", ctx.getParamMap());

				if (queryForInt > 0) {
					continue;
				}

				ctx.setParam("CUSTMOER_NAME", map.get("borrower_name"));
				ctx.setParam("HOURSE_ADDR", ((List<Map<String, Object>>) map.get("house_list")).get(0).get("address_detail"));
				if (cn.com.yitong.core.util.SecurityUtils.getSession().getUserId() == null) {

					ctx.setParam("WORK_NUMBER", ctx.getParam("manager_code"));
				} else {

					ctx.setParam("WORK_NUMBER", cn.com.yitong.core.util.SecurityUtils.getSession().getUserId());
				}
				ctx.setParam("PHONE", map.get("borrower_phone"));
				ctx.setParam("CUSTMOER_TYPE", map.get("borrower_type"));
				ctx.setParam("IS_TIMEOUT", "0");
					ctx.setParam("BUSI_START_DATE", map.get("apply_time"));
					ctx.setParam("FIRST_APPLY_ID", map.get("frist_apply_id"));
					ctx.setParam("FIRST_APPLY_TIME", map.get("frist_apply_time"));
				flowTool.execute(ctx, "write/rushTask");
			}
			} finally {
				lock.unlock();
			}
		}
		return NEXT;
	}

}
