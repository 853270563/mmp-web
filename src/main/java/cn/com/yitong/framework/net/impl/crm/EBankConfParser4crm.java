package cn.com.yitong.framework.net.impl.crm;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.yitong.common.utils.SpringContextUtils;
import cn.com.yitong.framework.core.bean.MBTransConfBean;
import cn.com.yitong.framework.core.vo.MBTransItem;
import cn.com.yitong.framework.net.IEBankConfCaches;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

/**
 * 加载报文定义
 * 
 * @author yaoym
 * 
 */
@Component
public class EBankConfParser4crm implements IEBankConfParser {
	private Logger logger = YTLog.getLogger(EBankConfParser4crm.class);

	@Autowired
	private IEBankConfCaches ebankConfCaches;
	private SAXReader reader;
	private final String rootpath = "/WEB-INF/conf/ytbank/";

	public EBankConfParser4crm() {
		reader = new SAXReader();
	}

	@Override
	public synchronized MBTransConfBean findTransConfById(String id) {
		logger.info("enter into MBTransconfBean....");
		MBTransConfBean result = null;
		logger.info("hasTransConfById==" + ebankConfCaches.hasTransConfById(id));
		if (!ebankConfCaches.hasTransConfById(id)) {
			// 当前缓存中没有配置信息进行加载
			logger.info("load trancation defined file :" + id);
			Document doc = null;
			try {
				String filePath = rootpath + id + ".xml";
				logger.info("load trancation defined file filePath:" + filePath);
				doc = reader.read(SpringContextUtils.getApplicationContext().getResource(filePath).getInputStream());
			} catch (Exception e) {
				logger.error("read xml file error!", e);
				return null;
			}
			if (doc != null && doc.hasContent()) {
				Element root = doc.getRootElement();
				logger.debug("load trancation defined file desc: "
						+ root.attributeValue(CrmConst.AT_NAME));
				MBTransConfBean confbean = parserObject(root);
				ebankConfCaches.addTransConf(id, confbean);
				result = confbean;
			} else {
				logger.error("transcation defined name check failure:" + id);
			}
		} else {
			logger.info("id-----:" + id);
			result = ebankConfCaches.getTransConfById(id);
			logger.info("result-----:" + result);
		}
		return result;
	}


	@SuppressWarnings("unchecked")
	public MBTransConfBean parserObject(Element el) {
		MBTransConfBean transConf = new MBTransConfBean();
		transConf.setName(el.attributeValue(CrmConst.AT_NAME));
		// serviceId
		transConf.setPropery(CrmConst.AT_SERVICE_ID, el.attributeValue(CrmConst.AT_SERVICE_ID));
		if (el.hasContent()) {
			List<Element> list = el.elements();
			for (Element e : list) {
				if (e.getName().equals(CrmConst.XT_SEND)) {
					parserSnd(e, transConf);
				} else if (e.getName().equals(CrmConst.XT_RCV)) {
					parserRce(e, transConf);
				}
			}
		}
		return transConf;
	}

	/**
	 * 解析发送配置节点
	 * 
	 * @param el
	 * @param conf
	 */
	@SuppressWarnings("unchecked")
	public void parserSnd(Element el, MBTransConfBean conf) {
		List<Element> list = el.elements();
		for (Element e : list) {
			MBTransItem item = new MBTransItem();
			parseCommonItem(e, item);
			parseRecChildItem(item, e);
			conf.addSedItem(item);
		}
	}


	/**
	 * 解析接受配置节点
	 * 
	 * @param el
	 * @param conf
	 */
	@SuppressWarnings("unchecked")
	public void parserRce(Element el, MBTransConfBean conf) {
		List<Element> list = el.elements();
		for (Element e : list) {
			MBTransItem item = new MBTransItem();
			parseCommonItem(e, item);
			parseRecChildItem(item, e);
			conf.addRcvItem(item);
		}
	}


	/**
	 * 加载接受报文的字段定义
	 * 
	 * @param e
	 * @param item
	 * @return
	 */
	private boolean parseCommonItem(Element e, MBTransItem item) {
		String name = e.attributeValue(CrmConst.AT_NAME);
		String targetName = e.attributeValue(CrmConst.AT_TARGET_NAME);
		String client = e.attributeValue(CrmConst.AT_CLIENT);
		String desc = e.attributeValue(CrmConst.AT_DESC);
		String type = e.attributeValue(CrmConst.AT_TYPE);
		String dolt = e.attributeValue(CrmConst.AT_DOLT);
		String length = e.attributeValue(CrmConst.AT_LEN);
		String required = e.attributeValue(CrmConst.AT_REQUIRED);
		String mapkey = e.attributeValue(CrmConst.AT_MAPKEY);
		String descName = e.attributeValue(CrmConst.AT_MAPKEY_DESCNAME);
		String xpath = e.attributeValue(CrmConst.AT_XPATH);
		String defaultValue = e.attributeValue(CrmConst.AT_DEFVAL);
		String plugin = e.attributeValue(CrmConst.AT_PLUS);
		String delimiter = e.attributeValue(CrmConst.AT_DELIMITER_ROW);
		String itemDelimiter = e.attributeValue(CrmConst.AT_DELIMITER_FIELD);
		String sizeField = e.attributeValue(CrmConst.AT_SIZE_FIELD);
		item.setName(name);
		item.setType(type);
		item.setMapKey(mapkey);
		item.setDescName(descName);
		item.setTargetName(StringUtil.isEmpty(targetName) ? name : targetName);
		item.setLength(StringUtil.string2Int(length));
		item.setRequred("true".equalsIgnoreCase(required));
		item.setClient(StringUtil.isEmpty(client) ? name : client);
		item.setDesc(desc);
		item.setPlugin(plugin);
		item.setXmlPath(StringUtil.isEmpty(xpath) ? name : xpath);
		item.setDefaultValue(defaultValue);
		item.setDelimiter(delimiter);
		item.setItemDelimiter(itemDelimiter);
		item.setDolt(StringUtil.isEmpty(dolt) ? 0 : Integer.parseInt(dolt));
		item.setSizeField(sizeField);
		return true;
	}

	/**
	 * 是否有子元素
	 * 
	 * @param elem
	 * @return
	 */
	private boolean isListItem(String type) {
		return CrmConst.FILED_TYPE_E.equalsIgnoreCase(type) || CrmConst.FILED_TYPE_D.equalsIgnoreCase(type);
	}

	/**
	 * 递归取子结构
	 * 
	 * @param item
	 * @param elem
	 */
	@SuppressWarnings("unchecked")
	private void parseRecChildItem(MBTransItem item, Element elem) {
		if (isListItem(item.getType())) {
			// 如果类型是list 则进行 子节点解析
			List<Element> mapchild = elem.elements();
			List<MBTransItem> subList = new ArrayList<MBTransItem>();
			for (Element mapel : mapchild) {
				MBTransItem subItem = new MBTransItem();
				parseCommonItem(mapel, subItem);
				parseRecChildItem(subItem, mapel);
				subList.add(subItem);
			}
			item.setChildren(subList);
		}
	}

}