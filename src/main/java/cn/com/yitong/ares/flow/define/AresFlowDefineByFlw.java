package cn.com.yitong.ares.flow.define;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.core.AresApp;
import cn.com.yitong.ares.flow.AresServiceStep;
import cn.com.yitong.ares.flow.ConditionItem;
import cn.com.yitong.ares.flow.IAresFlowDefine;
import cn.com.yitong.ares.flow.IAresFlowDispatch;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.util.common.StringUtil;
import cn.com.yitong.util.common.XmlUtil;

/**
 * 
 * 直接根据flw文件解析
 */
@Component
public class AresFlowDefineByFlw implements IAresFlowDefine {

	private Logger logger = LoggerFactory.getLogger(getClass());

	/*
	 * xml方式定义加载服务编排
	 * 
	 * @see com.yitong.ares.flow.IAresFlowDefine#parserDefine(java.lang.String,
	 * com.yitong.ares.flow.AresServiceDispatch)
	 */
	@Override
	public boolean parserDefine(String define, IAresFlowDispatch flow) {
		String content = AresApp.getInstance().loadUtf8Resouce(define);
		Document doc = null;
		try {
			doc = XmlUtil.readText(content);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return false;
		}
		// logger.debug("config:\n{}", doc.asXML());

		// 初始化JsonMap定义
		Map map = new HashMap();
		Element root = doc.getRootElement();
		// 解析成节点定义
		String name = root.attributeValue("desc");
		List<Element> actionNodes = root.element("nodes").elements("node");

		AresApp app = AresApp.getInstance();

		Map defDatasMap = new HashMap();// KEY:node_id,VALUE:参数map
		Map stepsMap = new HashMap();// KEY:node_id,VALUE:步骤索引
		Map<String, String> nodesMap = new HashMap();// KEY:步骤索引,VALUE:node_id
		List<AresServiceStep> steps = new ArrayList();
		for (int i = 0; i < actionNodes.size(); i++) {
			Element item = actionNodes.get(i);
			String index = String.valueOf(i + 1);// 给步骤添加索引

			String defAct = item.elementText("beanId");// ref
														// bean
			IAresSerivce service = app.getBean(defAct);
			// 设置节点参数
			AresServiceStep step = new AresServiceStep();
			step.setIndex(StringUtil.parseInt(index));
			step.setAresService(service);
			step.setServiceName(item.elementText("desc"));
			steps.add(step);

			// 设置参数
			Element datasElem = item.element("data-map");
			if (datasElem != null) {
				List dataNodes = datasElem.elements("data");
				if (dataNodes != null) {
					Map defData = new HashMap();
					for (Object dataNode : dataNodes) {
						Element dataElem = (Element) dataNode;
						defData.put(dataElem.attributeValue("key"), dataElem.attributeValue("expr"));
					}
					step.setDatas(defData);
					defDatasMap.put(item.attributeValue("id"), defData);
				}
			}
			stepsMap.put(item.attributeValue("id"), index);
			nodesMap.put(index, item.attributeValue("id"));
			step.setType(item.elementText("type"));// 类型：普通为空;finally;condition;
		}

		// 根据line设置分支
		List<Element> linesNodes = root.element("lines").elements("line");
		int index = 1;
		for (AresServiceStep step : steps) {
			String nodeId = nodesMap.get(String.valueOf(index));
			generyForwardMapping(step, nodeId, stepsMap, linesNodes);
			index++;
		}

		flow.setSteps(steps);
		// 设置预置代码
		Map defData = (Map) map.get("datas");
		flow.setDatas(defData);

		return true;
	}

	private void generyForwardMapping(AresServiceStep step, String nodeId, Map<String, String> stepsMap,
			List<Element> linesNodes) {
		boolean isCondition = step.isConditionStep();
		Map mapping = step.getMapping();
		List forwards = step.getForwards();
		Map mapDatas = step.getMapDatas();
		for (int i = 0; i < linesNodes.size(); i++) {
			Element item = linesNodes.get(i);
			String from = item.attributeValue("from");
			if (!from.equals(nodeId)) {
				continue;
			}
			String desc = item.attributeValue("desc");
			String[] tmps = desc.split("\\s+|\\||:|：");
			String flag = tmps[0];
			flag = StringUtil.isBlank(flag) ? "1" : flag;
			// 分支序号
			int forwardIndex = StringUtil.parseInt(flag);

			String to = item.attributeValue("to");
			int toIndex = StringUtil.parseInt(stepsMap.get(to));
			String condition = item.attributeValue("condition");
			if (isCondition) {
				// 3.1条件分支
				forwards.add(new ConditionItem(condition, forwardIndex, toIndex));
			} else {
				// 原3.0分支跳转识别
				mapping.put(forwardIndex, toIndex);
			}

			Element datasElem = item.element("data-map");
			if (datasElem != null) {
				List dataNodes = datasElem.elements("data");
				if (dataNodes != null) {
					Map defData = new HashMap();
					for (Object dataNode : dataNodes) {
						Element dataElem = (Element) dataNode;
						defData.put(dataElem.attributeValue("key"), dataElem.attributeValue("expr"));
					}
					mapDatas.put(toIndex, defData);
				}
			}
		}

	}

}