package cn.com.yitong.framework.net.impl.creditCore;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.com.yitong.common.utils.SpringContextUtils;
import cn.com.yitong.framework.core.bean.MBTransConfBean;
import cn.com.yitong.framework.core.vo.MBTransItem;
import cn.com.yitong.framework.net.IEBankConfCaches;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.impl.NetConst;
import cn.com.yitong.framework.net.impl.credit.CreditConst;
import cn.com.yitong.util.StringUtil;

/**
 * @author luanyu
 * @date   2018年8月9日
 */
@Component("eBankConfParser4CreditCore")
public class EBankConfParser4CreditCore implements IEBankConfParser {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private IEBankConfCaches ebankConfCaches;
	private SAXReader reader;
	@Value("${DISGRAM_PATH}")
	private String rootpath;
	private final String template = "%s/%s.xml";
	public final String PRE_PATH = "in/";

	public EBankConfParser4CreditCore() {
		reader = new SAXReader();
	}
	@Override
	public MBTransConfBean findTransConfById(String id) {
		logger.info("enter into MBTransconfBean....");
		MBTransConfBean result = null;
		String fullPath = PRE_PATH + id;
		String filePath = String.format(template, rootpath, fullPath);
		logger.info("hasTransConfById==" + ebankConfCaches.hasTransConfById(fullPath));
		if (!ebankConfCaches.hasTransConfById(fullPath)) {
			// 当前缓存中没有配置信息进行加载
			logger.info("load trancation defined file :" + id);
			Document doc = null;
			try {
				logger.info("load trancation defined file filePath:" + filePath);
				doc = reader.read(SpringContextUtils.getApplicationContext().getResource(filePath).getInputStream());
			} catch (Exception e) {
				logger.error("read xml file error!", e);
				return null;
			}
			if (doc != null && doc.hasContent()) {
				Element root = doc.getRootElement();
				logger.debug("load trancation defined file desc: " + root.attributeValue(CreditConst.AT_NAME));
				MBTransConfBean confbean = parserObject(root);
				ebankConfCaches.addTransConf(fullPath, confbean);
				result = confbean;
			} else {
				logger.error("transcation defined name check failure:" + fullPath);
			}
		} else {
			logger.info("id-----:" + id);
			result = ebankConfCaches.getTransConfById(fullPath);
			logger.info("result-----:" + result);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public MBTransConfBean parserObject(Element el) {
		MBTransConfBean conf = new MBTransConfBean();
		conf.setName(el.attributeValue(NetConst.AT_NAME));
		List<Attribute> datas = el.attributes();
		for (Attribute data : datas) {
			conf.setPropery(data.getName(), data.getValue());
		}
		if (el.hasContent()) {
			List<Element> list = el.elements();
			for (Element e : list) {
				if (e.getName().equals(NetConst.XT_SEND)) {
					parserSnd(e, conf);
				} else if (e.getName().equals(NetConst.XT_RCV)) {
					parserRce(e, conf);
				}
			}
		}
		return conf;
	}

	/**
	 * 解析发送配置节点
	 * 
	 * @param el
	 * @param transConf
	 */
	public void parserSnd(Element el, MBTransConfBean transConf) {
		List<Element> list = el.elements();
		for (Element e : list) {
			// 仅支持非列表的字段解析
			MBTransItem item = new MBTransItem();
			parseCommonItem(e, item);
			parseListChildItem(item, e);
			transConf.addSedItem(item);
		}
	}

	/**
	 * 解析发送配置节点
	 * 
	 * @param el
	 * @param transConf
	 */
	public void parserSndHeader(Element el, MBTransConfBean transConf) {
		List<Element> list = el.elements();
		for (Element e : list) {
			if (e.getName().equals(NetConst.XT_ITEM)) {
				// 仅支持非列表的字段解析
				MBTransItem item = new MBTransItem();
				parseCommonItem(e, item);
				parseListChildItem(item, e);
				//logger.debug("send item:{}", item);
				transConf.addSendHeaderItem(item);
			}
		}
	}

	/**
	 * 解析接受配置节点
	 * 
	 * @param el
	 * @param transConf
	 */
	public void parserRce(Element el, MBTransConfBean transConf) {
		List<Element> list = el.elements();
		for (Element e : list) {
			MBTransItem item = new MBTransItem();
			parseCommonItem(e, item);
			//logger.debug("rcv item :{}", item);
			if (isListItem(e)) {
				// 如果类型是list 则进行 子节点解析
				List<Element> children = e.elements();
				List<MBTransItem> subList = new ArrayList<MBTransItem>();
				for (Element mapel : children) {
					MBTransItem subItem = new MBTransItem();
					parseCommonItem(mapel, subItem);
					parseListChildItem(subItem, mapel);
					subList.add(subItem);
				}
				item.setChildren(subList);
			}
			transConf.addRcvItem(item);
		}
	}

	public void parserRceHeader(Element el, MBTransConfBean transConf) {
		List<Element> list = el.elements();
		for (Element e : list) {
			MBTransItem item = new MBTransItem();
			parseCommonItem(e, item);
			if (isListItem(e)) {
				// 如果类型是list 则进行 子节点解析
				List<Element> children = e.elements();
				List<MBTransItem> subList = new ArrayList<MBTransItem>();
				for (Element mapel : children) {
					MBTransItem subItem = new MBTransItem();
					parseCommonItem(mapel, subItem);
					parseListChildItem(subItem, mapel);
					subList.add(subItem);
				}
				item.setChildren(subList);
			}
			transConf.addRcvHeaderItem(item);
		}
	}

	/**
	 * 解析报文字段定义
	 * 
	 * @param e
	 *            元素
	 * @param item
	 *            报文解析对象
	 * @return
	 */
	private boolean parseCommonItem(Element e, MBTransItem item) {
		String name = e.attributeValue(NetConst.AT_NAME);
		String targetName = e.attributeValue(NetConst.AT_TARGET);
		String desc = e.attributeValue(NetConst.AT_DESC);
		String type = e.attributeValue(NetConst.AT_TYPE);
		String length = e.attributeValue(NetConst.AT_LEN);
		String required = e.attributeValue(NetConst.AT_REQUIRED);
		String defaultValue = e.attributeValue(NetConst.AT_DEFAULT);
		String plus = e.attributeValue(NetConst.AT_PLUS);
		String comment = e.attributeValue(NetConst.AT_COMMENT);
		String xpath = e.attributeValue(NetConst.AT_XPATH);
		item.setName(name);
		item.setType(type);
		item.setTargetName(StringUtil.isEmpty(targetName) ? name : targetName);
		item.setLength(StringUtil.parseInt(length));
		item.setRequired("true".equalsIgnoreCase(required));
		item.setDesc(desc);
		item.setDefaultValue(defaultValue);
		item.setXmlPath(StringUtil.isEmpty(xpath) ? name : xpath);
		item.setComment(comment);
		return true;
	}

	/**
	 * 是否为列表结构或实体
	 * 
	 * @param elem
	 * @return
	 */
	private boolean isListItem(Element elem) {
		String type = elem.attributeValue(NetConst.AT_TYPE);
		return NetConst.FILED_TYPE_E.equals(type) || NetConst.FILED_TYPE_M.equals(type) || "list".equalsIgnoreCase(type);
	}

	/**
	 * 递归取子结构
	 * 
	 * @param item
	 * @param elem
	 */
	private void parseListChildItem(MBTransItem item, Element elem) {
		if (isListItem(elem)) {
			// 如果类型是list 则进行 子节点解析
			List<Element> mapchild = elem.elements();
			List<MBTransItem> subList = new ArrayList<MBTransItem>();
			for (Element mapel : mapchild) {
				MBTransItem subItem = new MBTransItem();
				parseCommonItem(mapel, subItem);
				parseListChildItem(subItem, mapel);
				subList.add(subItem);
			}
			item.setChildren(subList);
		}
	}
}
