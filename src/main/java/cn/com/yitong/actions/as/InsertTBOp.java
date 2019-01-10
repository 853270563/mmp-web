package cn.com.yitong.actions.as;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.service.ICrudService;

/**
 * 业务数据入库
 */
@Component
public class InsertTBOp extends AbstractOp implements IAresSerivce {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	ICrudService service;
	
	@Override
	public int execute(IBusinessContext ctx) {
		logger.debug("--run--");
		Map paramMap = new HashMap<String, Object>();
		String taskId = (String) ctx.getParamMap().get("bar_code");
		String busiId = (String) ctx.getParamMap().get("entrustId");
		String apply_time = (String) ctx.getParamMap().get("apply_time");
		paramMap.put("TASKID", taskId);
		paramMap.put("BUSIID", busiId);
		paramMap.put("CREATE_TIME", apply_time);
		paramMap.put("FLAG", "0");
		service.insert("TASK_BUSI_RELATION.insertBusiInfo", paramMap);
		//判断是否为重新核验
		String reFlag = (String) ctx.getParamMap().get("reFlag");
		if(reFlag.equals("1")){
			paramMap.put("BAR_CODE", taskId);
			paramMap.put("STATE", "03");
			service.update("MY_DISPACH.updateState", paramMap);
		}
		return NEXT;
	}
}
