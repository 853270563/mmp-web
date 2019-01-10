package cn.com.yitong.ares.flow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.yitong.ares.jstl.JstlUtil;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.util.common.StringUtil;
import cn.com.yitong.util.common.ValidUtils;

/**
 * 流程服务节点
 * 
 * @author yaoym
 * 
 */
public class AresServiceStep extends AresFlowData {

	private Logger logger = LoggerFactory.getLogger(getClass());

	public int run(IBusinessContext ctx) {
		if (aresService == null)
			return IAresSerivce.EXIT;
		logger.debug("step>>>\t{} {}; preDatas:{}", index, this.getServiceName(), this.getDatas());
		List<String> tmpKeys = new ArrayList<String>();
		// 执行简单的赋值及转译
		evalPrevDatas(ctx, tmpKeys);
		// 执行必输项检查
		checkRequireParams(ctx);
		// 执行服务
		int indexKey = aresService.execute(ctx);
		// 清理本节点设置的临时参数
		clearTempParams(ctx, tmpKeys);
		// 设置默认的跳转：EXIT
		if (indexKey <= IAresSerivce.EXIT) {
			return IAresSerivce.EXIT;
		}
		// 1、缺省为返回值识别下一节点；
		// 2、是否为条件判断，识别下一节点；
		int next = IAresSerivce.EXIT;
		if (isConditionStep()) {
			logger.debug("condition forwards:{},stepDatas:{},mapDatas:{}", forwards, getDatas(), mapDatas);
			// 是否为条件判断，识别下一节点；
			for (ConditionItem item : forwards) {
				String condition = item.getCondition();
				if (StringUtil.isEmpty(condition) || JstlUtil.evalBoolean(ctx, condition)) {
					logger.info("condition item:{}", item);
					next = item.getNextStepIndex();
					break;
				}
			}
			// 执行完毕，后续数据转译
			AresFlowData nextPrevData = mapDatas.get(next);
			if (null != nextPrevData) {
				nextPrevData.evalPrevDatas(ctx);
			}
		} else {
			logger.debug("return<<<\tmapping:{},stepDatas:{},mapDatas:{}", mapping, getDatas(), mapDatas);
			// 缺省为返回值识别下一节点；
			Integer nextStep = mapping.get(indexKey);
			next = nextStep == null ? IAresSerivce.EXIT : nextStep;
			// 执行完毕，后续数据转译
			AresFlowData nextPrevData = mapDatas.get(indexKey);
			if (null != nextPrevData) {
				nextPrevData.evalPrevDatas(ctx);
			}
		}
		return next;
	}

	/**
	 * 清理临时节点数据
	 * 
	 * @param ctx
	 * @param tmpKeys
	 */
	private void clearTempParams(IBusinessContext ctx, List<String> tmpKeys) {
		for (String key : tmpKeys) {
			ctx.removeParam(key);
		}
	}

	/***
	 * 必输项检查
	 * 
	 * @param ctx
	 */
	private void checkRequireParams(IBusinessContext ctx) {
		String required = ctx.getParam("*required");
		if (null == required) {
			return;
		}
		String[] vars = required.split(",|，|\\s+");
		for (String var : vars) {
			if (StringUtil.isEmpty(var)) {
				continue;
			}
			ValidUtils.validEmpty(var, ctx.getParamMap());
		}
	}

	private ConditionItem evalConditionItem(IBusinessContext ctx) {
		for (ConditionItem item : forwards) {
			String condition = item.getCondition();
			if (StringUtil.isEmpty(condition) || JstlUtil.evalBoolean(ctx, condition)) {
				logger.info("condition item:{}", item);
				return item;
			}
		}
		return null;
	}

	/**
	 * 分支与流程节点的映射
	 */
	private Map<Integer, Integer> mapping = new HashMap();
	private List<ConditionItem> forwards = new ArrayList();
	private Map<Integer, AresFlowData> mapDatas = new HashMap();

	private IAresSerivce aresService;
	private int index;

	private String serviceName;
	private String type;// finally

	public IAresSerivce getAresService() {
		return aresService;
	}

	public void setAresService(IAresSerivce aresService) {
		this.aresService = aresService;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Map<Integer, Integer> getMapping() {
		return mapping;
	}

	public void setMapping(Map<Integer, Integer> mapping) {
		this.mapping = mapping;
	}

	public Map<Integer, AresFlowData> getMapDatas() {
		return mapDatas;
	}

	public void setMapDatas(Map<Integer, AresFlowData> mapDatas) {
		this.mapDatas = mapDatas;
	}

	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * @param serviceName
	 *            the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	public boolean isFinallyStep() {
		return "finally".equals(this.type);
	}

	public boolean isConditionStep() {
		return "condition".equals(this.type);
	}

	/**
	 * @return the forwards
	 */
	public List<ConditionItem> getForwards() {
		return forwards;
	}

	/**
	 * @param forwards
	 *            the forwards to set
	 */
	public void setForwards(List<ConditionItem> forwards) {
		this.forwards = forwards;
	}

}