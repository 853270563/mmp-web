package cn.com.yitong.framework.net.impl.as;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
 * @author huangqiang@yitong.com.cn
 * 
 */
@Component
public class EBankConfParserAs implements IEBankConfParser {
	private Logger logger = YTLog.getLogger(EBankConfParserAs.class);

	@Autowired
	@Qualifier("EBankConfCaches")
	private IEBankConfCaches ebankConfCaches;
	private SAXReader reader;
	private final String rootpath = "classpath:META-INF/disgram/in/";
	public EBankConfParserAs() {
		reader = new SAXReader();
	}

	@Override
	public synchronized MBTransConfBean findTransConfById(String id) {
		logger.info("enter into MBTransconfBean....");
		// ClassLoader loader = Thread.currentThread().getContextClassLoader();
		MBTransConfBean result = null;
		logger.info("hasTransConfById==" + ebankConfCaches.hasTransConfById(id));
		// if (!ebankConfCaches.hasTransConfById(id)) {
		if (!ebankConfCaches.hasTransConfById(id)) {
			// 当前缓存中没有配置信息进行加载
			logger.info("load trancation defined file :" + id);
			Document doc = null;
			try {
				String filePath = rootpath + id + ".xml";// /WEB-INF/conf/cmbc/login/Id.xml
				logger.info("load trancation defined file filePath:" + filePath);
//				doc = reader.read(System.getProperty("mmp.root") + filePath);
				doc = reader.read(SpringContextUtils.getApplicationContext().getResource(filePath).getInputStream());
			} catch (Exception e) {
				logger.error("read xml file error!", e);
				return null;
			}
			if (doc != null && doc.hasContent()) {
				Element root = doc.getRootElement();
				logger.debug("load trancation defined file desc: "
						+ root.attributeValue(EBankConst.AT_NAME));
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

	@SuppressWarnings("unused")
	private String getDelimiter(String type) {
		if (StringUtil.isEmpty(type)) {
			return ";";
		} else if ("tab".equals(type)) {
			return "\t";
		} else if ("blank".equals(type)) {
			return " ";
		}
		return type;
	}

	@SuppressWarnings("unchecked")
	public MBTransConfBean parserObject(Element el) {
		MBTransConfBean conf = new MBTransConfBean();
		conf.setName(el.attributeValue(EBankConst.AT_NAME));
		conf.setExcode(el.attributeValue(EBankConst.AT_EXCODE));
		List<Element> list;
		if (el.hasContent()) {
			list = el.elements();
			for (Element e : list) {
				if (e.getName().equals(EBankConst.SND)) {
					parserSnd(e, conf);
				} else if (e.getName().equals(EBankConst.RCV)) {
					parserRcv(e, conf);
				}
			}
		}
		return conf;
	}
	

	@SuppressWarnings("unchecked")
	public MBTransConfBean parserSnd(Element el,MBTransConfBean conf) {
		List<Element> list;
		if (el.hasContent()) {
			list = el.elements();
			for (Element e : list) {
				if (e.getName().equals(EBankConst.SND_HEAD)) {
					parserChildsnd(e, conf);
				}else if (e.getName().equals(EBankConst.SND_BODY)) {
					parserChildsnd(e, conf);
				}
			}
		}
		return conf;
	}
	
	@SuppressWarnings("unchecked")
	public MBTransConfBean parserRcv(Element el,MBTransConfBean conf) {
		List<Element> list;
		if (el.hasContent()) {
			list = el.elements();
			for (Element e : list) {
				if (e.getName().equals(EBankConst.RCV_HEAD)) {
					parserChildRcv(e, conf);
				}else if (e.getName().equals(EBankConst.RCV_BODY)) {
					parserChildRcv(e, conf);
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
	public void parserChildsnd(Element el, MBTransConfBean conf) {
		List<Element> list = el.elements();
		for (Element e : list) {
			if (e.getName().equals(EBankConst.XT_ITEM)) {
				// 仅支持非列表的字段解析
				MBTransItem item = new MBTransItem();
				parseCommonItem(e, item);
				parseChildItem(item, e);
				logger.debug("send "+e.getName()+" :" + item.toString());
				if (el.getName().equals(EBankConst.SND_HEAD)) {
					conf.addSendHeaderItem(item);
				} else if (el.getName().equals(EBankConst.SND_BODY)) {
					conf.addSedItem(item);
				} 
			}
		}
	}
	
	/**
	 *  解析接收配置节点
	 * 
	 * @param el
	 * @param conf
	 */
	@SuppressWarnings("unchecked")
	public void parserChildRcv(Element el, MBTransConfBean conf) {
		List<Element> list = el.elements();
		for (Element e : list) {
			if (e.getName().equals(EBankConst.XT_ITEM)) {
				// 仅支持非列表的字段解析
				MBTransItem item = new MBTransItem();
				parseCommonItem(e, item);
				parseChildItem(item, e);
				logger.debug("rcv "+e.getName()+" :" + item.toString());
				if (el.getName().equals(EBankConst.RCV_HEAD)) {
					conf.addRcvHeaderItem(item);
				}else if (el.getName().equals(EBankConst.RCV_BODY)) {
					conf.addRcvItem(item);
				} 
			}
		}
	}

	/**
	 * 是否为列表结构
	 * 
	 * @param type
	 * @return
	 */
	private boolean isListItem(String type) {
		return EBankConst.FILED_TYPE_E.equalsIgnoreCase(type) || EBankConst.FILED_TYPE_O.equalsIgnoreCase(type);
	}
	/**
	 * 递归取子结构
	 * 
	 * @param item
	 * @param elem
	 */
	@SuppressWarnings({ "unchecked"})
	private void parseChildItem(MBTransItem item, Element elem) {
		if (isListItem(item.getType())) {
			// 如果类型是list 则进行 子节点解析
			List<Element> mapchild = elem.elements();
			List<MBTransItem> subList = new ArrayList<MBTransItem>();
			for (Element mapel : mapchild) {
				MBTransItem subItem = new MBTransItem();
				parseCommonItem(mapel, subItem);
				parseChildItem(subItem, mapel);
				subList.add(subItem);
			}
			item.setChildren(subList);
		}
	}
	

	/**
	 * 加载接收报文的字段定义
	 * 
	 * @param e
	 * @param item
	 * @return
	 */
	private boolean parseCommonItem(Element e, MBTransItem item) {
		String name = e.attributeValue(EBankConst.AT_NAME);
		String targetName = e.attributeValue(EBankConst.AT_TARNAME);
		String cname = e.attributeValue(EBankConst.AT_CLIENT_NAME);
		String desc = e.attributeValue(EBankConst.AT_DESC);
		String type = e.attributeValue(EBankConst.AT_TYPE);
		String dolt = e.attributeValue(EBankConst.AT_DOLT);
		String length = e.attributeValue(EBankConst.AT_LEN);
		String required = e.attributeValue(EBankConst.AT_REQUIRED);
		String mapkey = e.attributeValue(EBankConst.AT_MAPKEY);
		String descName = e.attributeValue(EBankConst.AT_MAPKEY_DESCNAME);
		String xpath = e.attributeValue(EBankConst.AT_XPATH);
		String defaultValue = e.attributeValue(EBankConst.AT_DEFVAL);
		String plugin = e.attributeValue(EBankConst.AT_PLUS);
		String delimiter = e.attributeValue(EBankConst.AT_DELIMITER_ROW);
		String itemDelimiter = e.attributeValue(EBankConst.AT_DELIMITER_FIELD);
		String dataTyp = e.attributeValue(EBankConst.AT_DATATYP);
		item.setName(name);
		item.setTargetName(targetName);
		item.setType(type);
		item.setMapKey(mapkey);
		item.setDescName(descName);
		item.setClient(StringUtil.isEmpty(cname) ? name : cname);
		item.setLength(StringUtil.string2Int(length));
		item.setRequired("true".equalsIgnoreCase(required));
		item.setDesc(desc);
		item.setPlugin(plugin);
		item.setXmlPath(StringUtil.isEmpty(xpath) ? name : xpath);
		item.setDefaultValue(defaultValue);
		item.setDelimiter(delimiter);
		item.setItemDelimiter(itemDelimiter);
		item.setDolt(StringUtil.isEmpty(dolt) ? 0 : Integer.parseInt(dolt));
//		item.setDataTyp(dataTyp);
		return true;
	}

}