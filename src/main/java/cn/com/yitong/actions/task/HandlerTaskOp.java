package cn.com.yitong.actions.task;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.common.utils.ConfigEnum;
import cn.com.yitong.core.util.DictUtils;
import cn.com.yitong.framework.base.IBusinessContext;

/**
 * @author luanyu
 * @date   2018年9月6日
 */
/**
*处理任务
*@author
*/
@Service
public class HandlerTaskOp extends AbstractOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * 任务超时时间 单位分钟
	 */
	@Override
	public int execute(IBusinessContext ctx) {
		int taskTimeout = Integer.parseInt(DictUtils.getDictValue(ConfigEnum.DICT_SYS_PARAMS, "taskTimeout", "48"));
		// TODO Auto-generated method stub
		logger.debug("-处理任务-run--");
		List<Map<String, Object>> paramDatas = ctx.getParamDatas("LIST");
		for (Map<String, Object> map : paramDatas) {
			Date date = (Date) map.get("RUSH_TIME");
			if (date == null) {
				continue;
			}
			map.put("TIMEOUT_TIME", (System.currentTimeMillis() - date.getTime()) / 1000 / 60 / 60 / 24);
			if (System.currentTimeMillis() - date.getTime() > taskTimeout * 60 * 1000) {
				getDao(ctx).update("rushTask.updateById", map);
			}
		}
		return NEXT;
	}

}
