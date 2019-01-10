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
import cn.com.yitong.ares.error.OtherRuntimeException;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.service.ICrudService;

/**
 * 更新派单状态
 */
@Component
public class UpdateDispachStateOp extends AbstractOp implements IAresSerivce {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	ICrudService service;
	
	@Override
	public int execute(IBusinessContext ctx) {
		logger.debug("--run--");
		service.update("MY_DISPACH.updateState", ctx.getParamMap());
		return NEXT;
	}
}
