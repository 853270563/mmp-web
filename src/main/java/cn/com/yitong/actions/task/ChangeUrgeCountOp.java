package cn.com.yitong.actions.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ibm.db2.jcc.am.lo;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.util.StringUtil;

/**
 * @author luanyu
 * @date   2018年9月6日
 */
/**
 * 处理任务
 *
 * @author
 */
@Service
public class ChangeUrgeCountOp extends AbstractOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 催办次数更改
	 */
	@Override
	public int execute(IBusinessContext ctx) {
		logger.debug("-处理任务-run--");
		String urgeCount = ctx.getParam("URGE_COUNT");
		if (urgeCount == "") {
			ctx.setParam("URGE_COUNT", 1);
		} else {
			ctx.setParam("URGE_COUNT", StringUtil.parseInt(urgeCount) + 1);
		}
		logger.debug(ctx.getParam("URGE_COUNT"));
		return NEXT;
	}

}
