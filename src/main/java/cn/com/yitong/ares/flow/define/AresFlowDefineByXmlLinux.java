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
import cn.com.yitong.ares.core.AresConf;
import cn.com.yitong.ares.flow.AresServiceStep;
import cn.com.yitong.ares.flow.IAresFlowDefine;
import cn.com.yitong.ares.flow.IAresFlowDispatch;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.modules.SpringTools;
import cn.com.yitong.util.common.StringUtil;
import cn.com.yitong.util.common.XmlUtil;

@Component
public class AresFlowDefineByXmlLinux implements IAresFlowDefine {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	/*
	 * json方式定义加载服务编排
	 * 
	 * @see
	 * cn.com.yitong.ares.flow.IAresFlowDefine#parserDefine(java.lang.String,
	 * cn.com.yitong.ares.flow.AresServiceDispatch)
	 */
	@Override
	public boolean parserDefine(String define, IAresFlowDispatch flow) {
		logger.info("flow loading:{}", define);
		if (AresConf.jclModuleRootPath == null) {
			return false;
		}
		String filePath = String.format("file:%s/%s", AresConf.jclModuleRootPath, define);
		logger.info("flow path :{}", filePath);
		
		String content = AresApp.getInstance().loadUtf8Resouce(filePath);
		Document doc = null;
		try {
			doc = XmlUtil.readText(content);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		// logger.debug("config:\n{}", doc.asXML());

		// 初始化JsonMap定义
		Map map = new HashMap();
		Element root = doc.getRootElement();
		// 解析成节点定义
		String name = root.attributeValue("title");
		logger.info("config name {}", name);
		List<Element> actionNodes = root.element("actions").elements("action");

		logger.info("flow loading define ,step number:{}", actionNodes.size());

		AresApp app = AresApp.getInstance();
		List<AresServiceStep> steps = new ArrayList();
		for (int i = 0; i < actionNodes.size(); i++) {
			Element item = actionNodes.get(i);
			String index = item.attributeValue("index");
			if (StringUtil.isEmpty(index)) {
				index = "" + (i + 1);
			}
			String defAct = item.attributeValue("ref");// ref bean
			IAresSerivce service = SpringTools.getBean(defAct,IAresSerivce.class);
			// 设置节点参数
			AresServiceStep step = new AresServiceStep();
			step.setIndex(StringUtil.parseInt(index));
			step.setAresService(service);
			steps.add(step);
			// 设置预置代码
			Element datasElem = item.element("datas");
			if (datasElem != null) {
				List dataNodes = datasElem.elements("data");
				if (dataNodes != null) {
					Map defData = new HashMap();
					for (Object dataNode : dataNodes) {
						Element dataElem = (Element) dataNode;
						defData.put(dataElem.attributeValue("key"),
								dataElem.getText());
					}
					step.setDatas(defData);
				}
			}
			// 设置分支映射的流程节点
			Element mapingElem = item.element("mapping");
			if (null != mapingElem) {
				List fwdNodes = mapingElem.elements("forward");
				if (fwdNodes != null) {
					Map<Integer, Integer> trueMapping = new HashMap();
					for (Object fwdNode : fwdNodes) {
						Element fwdElem = (Element) fwdNode;
						String fwdKey = fwdElem.attributeValue("key");
						if (StringUtil.isNotEmpty(fwdKey)) {
							fwdKey = fwdKey.replaceAll("next", "");
						}
						if (!StringUtil.isNumber(fwdKey)) {
							continue;
						}
						trueMapping.put(StringUtil.parseInt(fwdKey),
								StringUtil.parseInt(fwdElem.getText()));
					}
					if (!trueMapping.isEmpty()) {
						step.setMapping(trueMapping);
					}
				}
			}

		}
		flow.setSteps(steps);
		// 设置预置代码
		Map defData = (Map) map.get("datas");
		flow.setDatas(defData);

		return true;
	}
}
