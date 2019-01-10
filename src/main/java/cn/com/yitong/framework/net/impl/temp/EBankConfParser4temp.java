package cn.com.yitong.framework.net.impl.temp;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.yitong.common.utils.SpringContextUtils;
import cn.com.yitong.consts.NS;
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
public class EBankConfParser4temp implements IEBankConfParser {
	private Logger logger = YTLog.getLogger(this.getClass());

	@Autowired
	private IEBankConfCaches ebankConfCaches;
	private SAXReader reader;
	private final String rootpath = "/WEB-INF/conf/";

	public EBankConfParser4temp() {
		logger.debug("加载通讯报文定义数据文件路径:" + rootpath);
		reader = new SAXReader();
	}

	@Override
	public synchronized MBTransConfBean findTransConfById(String id) {
		MBTransConfBean result = null;
		logger.info("加载通讯报文定义数据文件路径" + rootpath + id);
		if (!ebankConfCaches.hasTransConfById(id)) {
			// 当前缓存中没有配置信息进行加载
			if (logger.isDebugEnabled()) {
				logger.debug("load trancation defined file :" + id);
			}
			Document doc = null;
			try {
				String filePath = rootpath + id + ".xml";// /WEB-INF/conf/db/system/OrganAll.xml
				if (logger.isDebugEnabled()) {
					logger.debug("load trancation defined file filePath:"
							+ filePath);
				}
				doc = reader.read(SpringContextUtils.getApplicationContext().getResource(filePath).getInputStream());
			} catch (Exception e) {
				logger.error(
						"Transation defined file not found! read xml file error!",
						e);
				return null;
			}
			if (doc != null && doc.hasContent()) {
				Element root = doc.getRootElement();
				if (logger.isDebugEnabled()) {
					logger.debug("load trancation defined file desc: "
							+ root.attributeValue(EBankConst.AT_NAME));
				}
				MBTransConfBean confbean = parserObject(root);
				ebankConfCaches.addTransConf(id, confbean);
				result = confbean;
			} else {
				logger.error("transcation defined name check failure:" + id);
			}
		} else {
			result = ebankConfCaches.getTransConfById(id);
		}
		return result;
	}

	public MBTransConfBean parserObject(Element el) {
		MBTransConfBean transConf = new MBTransConfBean();
		transConf.setName(el.attributeValue(EBankConst.AT_NAME));
		// statement
		transConf.setPropery(NS.IBATIS_STATEMENT,
				el.attributeValue(NS.IBATIS_STATEMENT));

		List<Attribute> datas = el.attributes();
		for (Attribute data : datas) {
			transConf.setPropery(data.getName(), data.getValue());
		}

		if (el.hasContent()) {
			List<Element> list = el.elements();
			for (Element e : list) {
				if (e.getName().equals(EBankConst.XT_SEND)) {
					List<Element> reqEles = e.elements();
					Element reqEl = null;
					for(Element reqEle:reqEles){
						if (reqEle.getName().equals("body")) {
							reqEl = reqEle;
						}
					}
					if(reqEl!=null){
						parserSnd(reqEl, transConf);
					}else{
						parserSnd(e, transConf);
					}
				} else if (e.getName().equals(EBankConst.XT_RCV)) {
					List<Element> reqEles = e.elements();
					Element reqEl = null;
					for(Element reqEle:reqEles){
						if (reqEle.getName().equals("body")) {
							reqEl = reqEle;
						}
					}
					if(reqEl!=null){
						parserRce(reqEl, transConf);
					}else{
						parserRce(e, transConf);
					}
					
				}
			}
		}
		return transConf;
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
			if (logger.isDebugEnabled()) {
				logger.debug("send item :" + item.toString());
			}
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
			if (e.getName().equals(EBankConst.XT_ITEM)) {
				// 仅支持非列表的字段解析
				MBTransItem item = new MBTransItem();
				parseCommonItem(e, item);
				if (logger.isDebugEnabled()) {
					logger.debug("send item :" + item.toString());
				}
				parseListChildItem(item, e);
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
			if (logger.isDebugEnabled()) {
				logger.debug("rcv item :" + item.toString());
			}
			if (isListItem(item)) {
				// 如果类型是list 则进行 子节点解析
				List<Element> children = e.elements();
				List<MBTransItem> subList = new ArrayList<MBTransItem>();
				for (Element mapel : children) {
					MBTransItem subItem = new MBTransItem();
					parseCommonItem(mapel, subItem);
					if (logger.isDebugEnabled()) {
						logger.debug("subItem item :" + subItem.toString());
					}
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
			if (logger.isDebugEnabled()) {
				logger.debug("rcv Header item :" + item.toString());
			}
			if (isListItem(item)) {
				// 如果类型是list 则进行 子节点解析
				List<Element> children = e.elements();
				List<MBTransItem> subList = new ArrayList<MBTransItem>();
				for (Element mapel : children) {
					MBTransItem subItem = new MBTransItem();
					parseCommonItem(mapel, subItem);
					if (logger.isDebugEnabled()) {
						logger.debug("subItem Header item :"
								+ subItem.toString());
					}
					parseListChildItem(subItem, mapel);
					subList.add(subItem);
				}
				item.setChildren(subList);
			}
			transConf.addRcvHeaderItem(item);
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
		String name = e.attributeValue(EBankConst.AT_NAME);
		String targetName = e.attributeValue(EBankConst.AT_TARGET_NAME);
		String desc = e.attributeValue(EBankConst.AT_DESC);
		String type = e.attributeValue(EBankConst.AT_TYPE);
		String length = e.attributeValue(EBankConst.AT_LEN);
		String required = e.attributeValue(EBankConst.AT_REQUIRED);
		String mapkey = e.attributeValue(EBankConst.AT_MAPKEY);
		String descName = e.attributeValue(EBankConst.AT_MAPKEY_DESCNAME);
		String xpath = e.attributeValue(EBankConst.AT_XPATH);
		String defaultValue = e.attributeValue(EBankConst.AT_DEFVAL);
		String plugin = e.attributeValue(EBankConst.AT_PLUS);
		String sizeField = e.attributeValue(EBankConst.AT_SIZE_FIELD);
		String client = e.attributeValue(EBankConst.AT_CLIENT);
		item.setName(name);
		item.setType(type);
		item.setMapKey(mapkey);
		item.setDescName(descName);
		item.setTargetName(StringUtil.isEmpty(targetName) ? name : targetName);
		item.setLength(StringUtil.string2Int(length));
		item.setRequired("true".equalsIgnoreCase(required));
		item.setDesc(desc);
		item.setPlugin(plugin);
		item.setXmlPath(StringUtil.isEmpty(xpath) ? name : xpath);
		item.setDefaultValue(defaultValue);
		item.setSizeField(sizeField);
		item.setClient(StringUtil.isEmpty(client) ? name : client);
		return true;
	}

	/**
	 * 是否为列表结构
	 * 
	 * @param elem
	 * @return
	 */
	private boolean isListItem(MBTransItem item) {
		return item.getType().equals(EBankConst.FILED_TYPE_E);
	}

	/**
	 * 递归取子结构
	 * 
	 * @param item
	 * @param elem
	 */
	private void parseListChildItem(MBTransItem item, Element elem) {
		if (isListItem(item)) {
			// 如果类型是list 则进行 子节点解析
			List<Element> mapchild = elem.elements();
			List<MBTransItem> subList = new ArrayList<MBTransItem>();
			for (Element mapel : mapchild) {
				MBTransItem subItem = new MBTransItem();
				parseCommonItem(mapel, subItem);
				parseListChildItem(subItem, mapel);
				if (logger.isDebugEnabled()) {
					logger.debug("subItem item :" + subItem.toString());
				}
				subList.add(subItem);
			}
			item.setChildren(subList);
		}
	}
}