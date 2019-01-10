package cn.com.yitong.framework.net.impl.esb;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springframework.stereotype.Component;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.MBTransConfBean;
import cn.com.yitong.framework.core.vo.MBTransItem;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

@Component
public class ResponseParser4esb implements IResponseParser {
	private Logger logger = YTLog.getLogger(this.getClass());

	final int headerItemSize = 12;

	@Override
	public boolean parserResponseData(IBusinessContext busiContext,
			IEBankConfParser confParser, String transCode) {
		String response = (String) busiContext.getResponseEntry();
		if (null == response || "".equals(response.trim())) {
			logger.info("bankcore server reponse is empty !");
			// 设置错误码及错误信息
			busiContext.setErrorInfo(AppConstants.STATUS_FAIL, "服务器响应为空",
					transCode);
			return false;
		}
		logger.info("response message is:\n" + response);
		logger.info("response message length is:\n" + response.length());
		// 动态报文体
		MBTransConfBean conf = confParser.findTransConfById(transCode);
		if (conf != null) {
			logger.info("loaded  transconf successfully!" + transCode);
		} else {
			logger.error("loaded transconf failed!" + transCode);
			busiContext.setErrorInfo(AppConstants.STATUS_FAIL, "交易定义加载失败!",
					transCode);
			return false;
		}
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(response);
		} catch (DocumentException e) {
			logger.error(transCode + " receive data can't covert to xml", e);
			busiContext.setErrorInfo(AppConstants.STATUS_FAIL, "交易响应解析失败",transCode);
			return false;
		}
		Element root = doc.getRootElement();
		
		// 解析报文
		parseService(transCode,root,confParser,busiContext);
	
		return true;
	}
	

	/**
	 * 解析报文
	 * @param transCode
	 * @param response
	 * @param confParser
	 * @param busiContext
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean parseService(String transCode, Element response,
			IEBankConfParser confParser, IBusinessContext busiContext) {
		MBTransConfBean conf = confParser.findTransConfById(transCode);
		if (logger.isDebugEnabled()) {
			logger.info("parserBussisData  ..start.." + conf.toString());// 解析进来的方法
		}
		if (null != conf) {
			List<MBTransItem> rcv = conf.getRcv();
			Element rctx = busiContext.getResponseContext(transCode);
			Node node=null;
			for (MBTransItem item : rcv) {
				String itemName =  item.getName();
				String clientName =  item.getTargetName();
				clientName = StringUtil.isEmpty(clientName) ? itemName : clientName;
				String itemType =  item.getType();
				String xpath  =  item.getXmlPath();
				if (ESBConst.FILED_TYPE_E.equals(itemType)) {
					// 列表内容
					List listNodes = response.selectNodes(xpath);
					if (null == listNodes || listNodes.size()<=0) {
						continue;
					}
					parserListData(transCode, item, listNodes, busiContext,rctx);
				} else if (ESBConst.FILED_TYPE_S.equals(itemType)) {
					// 列表内容
					List listNodes = response.selectNodes(xpath);
					if (null == listNodes || listNodes.size()<=0) {
						continue;
					}
					parserSelectData(transCode, item, listNodes, busiContext,rctx);
				} else if(ESBConst.FILED_TYPE_C.equals(itemType)){
					// 普通内容
					node = response.selectSingleNode(itemName);
					if (null != node) {
						busiContext.saveNode(rctx,clientName, node.getText().trim());
					} else{ 
						busiContext.saveNode(rctx, clientName,ESBConst.EMPTY);
					}
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.info("parserBussisData  ..end.." + conf.toString());
		}
		return true;
	}
	
	/**
	 * 选择深度解析
	 * @param transCode
	 * @param item
	 * @param nodes
	 * @param busiContext
	 * @param parentElement
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	private boolean parserSelectData(String transCode, MBTransItem item,
			List<Node> nodes, IBusinessContext busiContext,
			Element parentElement) {
		if (nodes == null || nodes.isEmpty()) {
			logger.info(transCode + " list content is empty ");
		} else {
			List<MBTransItem> childMap = item.getChildren();
			// 遍历列表实体
			for (Node node : nodes) {
				// 遍历实体结构
				for (MBTransItem mapItem : childMap) {
					String  type = mapItem.getType();
					String name = mapItem.getName();
					String clientName = mapItem.getTargetName();
					String xPaht = mapItem.getXmlPath();
					clientName = StringUtil.isEmpty(clientName) ? name : clientName;
					if (ESBConst.FILED_TYPE_E.equals(type)) {
						List<Node> listNode = node.selectNodes(xPaht);
						if(listNode!=null && listNode.size()>0){
							parserListData(transCode, mapItem, listNode,busiContext, parentElement);
						}
					} else if (ESBConst.FILED_TYPE_S.equals(type)) {
						List<Node> listNode = node.selectNodes(xPaht);
						parserSelectData(transCode, mapItem, listNode, busiContext, parentElement);
					} else if (ESBConst.FILED_TYPE_C.equals(type)) {
						Node tag = node.selectSingleNode(name);
						if (null != tag) {
							String text = tag.getText();
							if (StringUtil.isNotEmpty(text)) {
								busiContext.saveNode(parentElement, clientName, text.trim());
							}else if(mapItem.isRequred()){
								busiContext.saveNode(parentElement, clientName, ESBConst.EMPTY);
							}
						}
					}
				}
				
			
			}
		}
		return true;
	}

	
	/**
	 * 深度解析
	 * @param transCode
	 * @param item
	 * @param nodes
	 * @param busiContext
	 * @param parentElement
	 * @return
	 */
	@SuppressWarnings({ "unused", "unchecked" })
	private boolean parserListData(String transCode, MBTransItem item,
			List<Node> nodes, IBusinessContext busiContext,
			Element parentElement) {
		if (nodes == null || nodes.isEmpty()) {
			logger.info(transCode + " list content is empty ");
		} else {
			String name, type,clientName,xpath;
			List<MBTransItem> childMap = item.getChildren();
			Element listElem = parentElement.addElement(item.getTargetName());
			listElem.addAttribute(ESBConst.AT_TYPE, ESBConst.TYPE_LIST);
			// 遍历列表实体
			for (Node node : nodes) {
				Element mapElem = listElem.addElement(AppConstants.XML_MAP);
				mapElem.addAttribute(ESBConst.AT_TYPE, ESBConst.TYPE_MAP);
				// 遍历实体结构
				for (MBTransItem mapItem : childMap) {
					type = mapItem.getType();
					name = mapItem.getName();
					clientName = mapItem.getTargetName();
					if (ESBConst.FILED_TYPE_E.equals(type)) {
						List<Node> listNode = node.selectNodes(name);
						parserListData(transCode, mapItem, listNode,busiContext, mapElem);
						continue;
					} else if (ESBConst.FILED_TYPE_S.equals(type)) {
						List<Node> selectNodes = node.selectNodes(name);
						parserSelectData(transCode, mapItem, selectNodes, busiContext, mapElem);
					} else if (ESBConst.FILED_TYPE_C.equals(type)) {
						Node tag = node.selectSingleNode(name);
						if (null != tag) {
							String text = tag.getText();
							if (StringUtil.isNotEmpty(text)) {
								busiContext.saveNode(mapElem, clientName, text.trim());
							}else if(mapItem.isRequred()){
								busiContext.saveNode(mapElem, clientName, ESBConst.EMPTY);
							}
						}
					}
				}
				List<Element> eles = mapElem.elements();
				if(eles==null || eles.isEmpty()){
					mapElem.getParent().remove(mapElem);
				}
			}
		}
		return true;
	}

	
	/**
	 * 转义
	 * @param item
	 * @param key
	 * @param busiCtx
	 * @param elem
	 */
	@SuppressWarnings("unused")
	private void parserItemDesc(MBTransItem item, String key,
			IBusinessContext busiCtx,Element elem) {
		// 判断是否需要数据字典
		String descName = item.getDescName();
		if (StringUtil.isNotEmpty(key)
				&& StringUtil.isNotEmpty(item.getMapKey())
				&& StringUtil.isNotEmpty(descName)) {
			// 提取数据字典中的缓存设置
			Map<String, String> map = null;
			map = busiCtx.findOptionsMap(key);
			// 添加注释字段
			if (map != null) {
				String mapDesc = map.get(key);
				busiCtx.saveNode(elem, descName, mapDesc);
			}
		}
	}
}
