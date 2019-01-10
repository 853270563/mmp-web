package cn.com.yitong.framework.net.impl.net;

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
public class EBankConfParser4net implements IEBankConfParser {
	private Logger logger = YTLog.getLogger(EBankConfParser4net.class);

	@Autowired
	private IEBankConfCaches ebankConfCaches;
	private SAXReader reader;
	private final String rootpath = "/WEB-INF/conf/ytbank/net/";

	public EBankConfParser4net() {
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
						+ root.attributeValue(EBankConstnet.AT_NAME));
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
		MBTransConfBean conf = new MBTransConfBean();
		conf.setName(el.attributeValue(EBankConstnet.AT_NAME));

		List<Element> list;
		if (el.hasContent()) {
			list = el.elements();
			for (Element e : list) {
				if (e.getName().equals(EBankConstnet.XT_SEND)){
					parserSnd(e, conf);
				} else if (e.getName().equals(EBankConstnet.XT_RCV)) {
					parserRce(e, conf);
				} else if (e.getName().equals(EBankConstnet.XT_SEND_HEAD)) {
					parserSndHeader(e, conf);
				} else if (e.getName().equals(EBankConstnet.XT_RCV_HEAD)) {
					parserRceHeader(e, conf);
				}
			}
		}
		
		return conf;
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
			MBTransItem item =null;
			if(EBankConstnet.XT_head.equals(e.getName())){
				List<Element> head = e.elements();
				for (Element e1 : head) {
					item = new MBTransItem();
					parseCommonItem(e1, item);
					parseRecChildItem(item,e1);
					conf.addSedItem(item);
				}
			}else if(EBankConstnet.XT_body.equals(e.getName())){
				List<Element> body = e.elements();
				for (Element e2 : body) {
					item = new MBTransItem();
					parseCommonItem(e2, item);
					parseRecChildItem(item,e2);
					conf.addSedItem(item);
				};
			} else if(EBankConstnet.XT_ITEM.equals(e.getName())){
				item = new MBTransItem();
				parseCommonItem(e, item);
				parseRecChildItem(item, e);
				conf.addSedItem(item);
			}
		}
	}

	/**
	 * 解析发送配置节点
	 * 
	 * @param el
	 * @param conf
	 */
	@SuppressWarnings("unchecked")
	public void parserSndHeader(Element el, MBTransConfBean conf) {
		List<Element> list = el.elements();
		for (Element e : list) {
			if (e.getName().equals(EBankConstnet.XT_ITEM)) {
				// 仅支持非列表的字段解析
				MBTransItem item = new MBTransItem();
				parseCommonItem(e, item);
				parseRecChildItem(item, e);
				logger.debug("send item :" + item.toString());
				conf.addSendHeaderItem(item);
			}
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
			MBTransItem item =null;
			if(EBankConstnet.XT_head.equals(e.getName())){
				List<Element> head = e.elements();
				for (Element e1 : head) {
					item = new MBTransItem();
					parseCommonItem(e1, item);
					parseRecChildItem(item,e1);
					conf.addRcvItem(item);
				}
			}else if(EBankConstnet.XT_body.equals(e.getName())){
				List<Element> body = e.elements();
				for (Element e2 : body) {
					item = new MBTransItem();
					parseCommonItem(e2, item);
					parseRecChildItem(item,e2);
					conf.addRcvItem(item);
				};
			} else if(EBankConstnet.XT_ITEM.equals(e.getName())){
				item = new MBTransItem();
				parseCommonItem(e, item);
				parseRecChildItem(item, e);
				conf.addRcvItem(item);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void parserRceHeader(Element el, MBTransConfBean conf) {
		List<Element> list = el.elements();
		for (Element e : list) {
			MBTransItem item = new MBTransItem();
			parseCommonItem(e, item);
			logger.debug("rcv Header item :" + item.toString());
			if (isListItem(item.getType())) {
				// 如果类型是list 则进行 子节点解析
				List<Element> children = e.elements();
				List<MBTransItem> subList = new ArrayList<MBTransItem>();
				for (Element mapel : children) {
					MBTransItem subItem = new MBTransItem();
					parseCommonItem(mapel, subItem);
					parseRecChildItem(subItem, mapel);
					logger.debug("subItem Header item :" + subItem.toString());
					subList.add(subItem);
				}
				item.setChildren(subList);
			}
			conf.addRcvHeaderItem(item);
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
		String name = e.attributeValue(EBankConstnet.AT_NAME);
		String targetName = e.attributeValue(EBankConstnet.AT_TARGET_NAME);
		String desc = e.attributeValue(EBankConstnet.AT_DESC);
		String type = e.attributeValue(EBankConstnet.AT_TYPE);
		String dolt = e.attributeValue(EBankConstnet.AT_DOLT);
		String length = e.attributeValue(EBankConstnet.AT_LEN);
		String required = e.attributeValue(EBankConstnet.AT_REQUIRED);
		String mapkey = e.attributeValue(EBankConstnet.AT_MAPKEY);
		String descName = e.attributeValue(EBankConstnet.AT_MAPKEY_DESCNAME);
		String xpath = e.attributeValue(EBankConstnet.AT_XPATH);
		String defaultValue = e.attributeValue(EBankConstnet.AT_DEFVAL);
		String plugin = e.attributeValue(EBankConstnet.AT_PLUS);
		String delimiter = e.attributeValue(EBankConstnet.AT_DELIMITER_ROW);
		String itemDelimiter = e.attributeValue(EBankConstnet.AT_DELIMITER_FIELD);
		String sizeField = e.attributeValue(EBankConstnet.AT_SIZE_FIELD);
		String client = e.attributeValue(EBankConstnet.AT_CLIENT);
		item.setName(name);
		item.setType(type);
		item.setMapKey(mapkey);
		item.setDescName(descName);
		item.setTargetName(StringUtil.isEmpty(targetName) ? name : targetName);
		item.setClient(StringUtil.isEmpty(client) ? name : client);
		item.setLength(StringUtil.string2Int(length));
		item.setRequred("true".equalsIgnoreCase(required));
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
	 * 是否为列表结构
	 * 
	 * @param elem
	 * @return
	 */
	private boolean isListItem(String type) {
		return EBankConstnet.FILED_TYPE_E.equalsIgnoreCase(type) || EBankConstnet.FILED_TYPE_S.equalsIgnoreCase(type);
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
				logger.debug("subItem item :" + subItem.toString());
				subList.add(subItem);
			}
			item.setChildren(subList);
		}
	}

}