package cn.com.yitong.framework.net.impl.crm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.error.AresRuntimeException;
import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.MBTransConfBean;
import cn.com.yitong.framework.core.vo.MBTransItem;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.net.impl.esb.ESBConst;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

@Component
public class ResponseParser4crm implements IResponseParser {
	private Logger logger = YTLog.getLogger(this.getClass());

	@Override
	public boolean parserResponseData(IBusinessContext busiContext,
			IEBankConfParser confParser, String transCode) {
		String response = (String) busiContext.getResponseEntry();
		if (null == response || "".equals(response.trim())) {
			logger.info("bankcore server reponse is empty !");
			// 设置错误码及错误信息
			busiContext.setErrorInfo(AppConstants.STATUS_FAIL, "服务器响应为空",
					transCode);
			throw new AresRuntimeException(AppConstants.STATUS_FAIL, "服务器响应为空");
		}
		logger.info("response message is:\n" + response);
		logger.info("response message length is:\n" + response.length());
		// 动态报文体
		MBTransConfBean conf = confParser.findTransConfById(transCode);
		if (conf != null) {
			logger.info("loaded  transconf successfully!" + transCode);
		} else {
			logger.error("loaded transconf failed!" + transCode);
			throw new AresRuntimeException(AppConstants.STATUS_FAIL, "交易定义加载失败!");
		}
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(response);
		} catch (DocumentException e) {
			logger.error(transCode + " receive data can't covert to xml", e);
			throw new AresRuntimeException(AppConstants.STATUS_FAIL, "交易定义加载失败!");
		}
		Element root = doc.getRootElement();
		
		// 解析报文
		Map<String, Object> result = new HashMap<String, Object>();
		parseHead(root);
		parseService(transCode, root, confParser, result);
		busiContext.getParamMap().clear();
		busiContext.getParamMap().putAll(result);
		return true;
	}
	

	private void parseHead(Element root) {
		// TODO Auto-generated method stub
		String status = root.selectSingleNode("/Service/Service_Header/service_response/status").getText();
		if (ESBConst.RS_COMPLETE.equals(status)) {
			return;
		}
		String msg = root.selectSingleNode("/Service/Service_Header/service_response/desc").getText();
		throw new AresRuntimeException(AppConstants.STATUS_FAIL, msg);

	}

	/**
	 * 解析报文
	 * @param transCode
	 * @param response 根节点
	 * @param confParser
	 * @param busiContext 总线
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean parseService(String transCode, Element response,
			IEBankConfParser confParser, Map<String, Object> result) {
		MBTransConfBean conf = confParser.findTransConfById(transCode);
		if (null != conf) {
			List<MBTransItem> rcv = conf.getRcv();
			Node node=null;
			for (MBTransItem item : rcv) {
				String clientName = item.getClient();
				String itemType =  item.getType();
				String xpath  =  item.getXmlPath();
				if (CrmConst.FILED_TYPE_E.equals(itemType)) {
					// 列表内容
					List listNodes = response.selectNodes(xpath);
					if (null == listNodes || listNodes.size()<=0) {
						continue;
					}
					parserListData(transCode, item, listNodes, result);
				} else if(CrmConst.FILED_TYPE_C.equals(itemType)){
					// 普通内容
					node = response.selectSingleNode(xpath);
					if (null != node) {
						result.put(clientName, node.getText().trim());
					} else{ 
						result.put(clientName, CrmConst.EMPTY);
					}
				}
			}
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
					String clientName = mapItem.getClient();
					String xPaht = mapItem.getXmlPath();
					if (CrmConst.FILED_TYPE_E.equals(type)) {
						List<Node> listNode = node.selectNodes(xPaht);
						if(listNode!=null && listNode.size()>0){
							//	parserListData(transCode, mapItem, listNode,busiContext, parentElement);
						}
					} else if (CrmConst.FILED_TYPE_S.equals(type)) {
						List<Node> listNode = node.selectNodes(xPaht);
						parserSelectData(transCode, mapItem, listNode, busiContext, parentElement);
					} else if (CrmConst.FILED_TYPE_C.equals(type)) {
						Node tag = node.selectSingleNode(name);
						if (null != tag) {
							String text = tag.getText();
							if (StringUtil.isNotEmpty(text)) {
								busiContext.saveNode(parentElement, clientName, text.trim());
							}else if(mapItem.isRequred()){
								busiContext.saveNode(parentElement, clientName, CrmConst.EMPTY);
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
			List<Node> nodes, 
			Map<String, Object> result) {
		if (nodes == null || nodes.isEmpty()) {
			logger.info(transCode + " list content is empty ");
		} else {
			String name, type,clientName,xpath;
			List<MBTransItem> childMap = item.getChildren();
			List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
			result.put(item.getClient(), mapList);
			// 遍历列表实体
			for (Node node : nodes) {
				HashMap<String, Object> hashMap = new HashMap<String, Object>();
				mapList.add(hashMap);
				// 遍历实体结构
				for (MBTransItem mapItem : childMap) {
					type = mapItem.getType();
					name = mapItem.getName();
					clientName = mapItem.getClient();
					if (CrmConst.FILED_TYPE_E.equals(type)) {
						List<Node> listNode = node.selectNodes(name);
						parserListData(transCode, mapItem, listNode, hashMap);
						continue;

					} else {
						Node tag = node.selectSingleNode(name);
						if (null != tag) {
							String text = tag.getText();
							if (StringUtil.isNotEmpty(text)) {
								hashMap.put(clientName, text.trim());

							}else if(mapItem.isRequred()){
								hashMap.put(clientName, CrmConst.EMPTY);
							}
						}
					}
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
