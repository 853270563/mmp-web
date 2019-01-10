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
public class QueryDetailsOp extends AbstractOp implements IAresSerivce {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	ICrudService service;
	
	@Override
	public int execute(IBusinessContext ctx) {
		logger.debug("--run--");
		Map paramMap = new HashMap<String, Object>();
		String taskId = (String) ctx.getParamMap().get("TASKID");
		paramMap.put("TASKID", taskId);
		List list = service.findList("BUSI_DETAILS.queryDetails", paramMap);
		ctx.getParamMap().put("dList", list);
		return NEXT;
	}
}
