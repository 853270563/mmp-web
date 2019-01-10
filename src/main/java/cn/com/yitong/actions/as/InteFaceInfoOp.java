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
 * 业务数据入库
 */
@Component
public class InteFaceInfoOp extends AbstractOp implements IAresSerivce {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	ICrudService service;
	
	@Override
	public int execute(IBusinessContext ctx) {
		logger.debug("--run--");
		Map paramMap = new HashMap<String, Object>();
		String optionType = (String) ctx.getParamMap().get("OPTION_TYPE");
		if(optionType.equals("1")){
			Map map = service.load("INTEFACE_STATE.queryInfo", ctx.getParamMap());
			if(null != map){
				ctx.getParamMap().putAll(map);
			}else{
				throw new OtherRuntimeException(
						"1", "没有该条记录！");			}
		}else if(optionType.equals("2")){
			Map map = service.load("INTEFACE_STATE.queryInfo", ctx.getParamMap());
			if(null == map || map.isEmpty()){
				service.insert("INTEFACE_STATE.insert", ctx.getParamMap());
			}else{
				service.update("INTEFACE_STATE.updateByTaskId", ctx.getParamMap());
			}
		}
		return NEXT;
	}
}
