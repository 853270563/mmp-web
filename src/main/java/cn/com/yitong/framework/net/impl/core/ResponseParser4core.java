package cn.com.yitong.framework.net.impl.core;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.core.bean.MBTransConfBean;
import cn.com.yitong.framework.core.vo.MBTransItem;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

/**
 * 响应报文解析器
 * 
 * @author yaoym
 * 
 * 
 */
@Component
public class ResponseParser4core implements IResponseParser {
	private Logger logger = YTLog.getLogger(this.getClass());

	public boolean parserResponseData(IBusinessContext busiContext,
			IEBankConfParser confParser, String transCode) {
		String xml = (String) busiContext.getResponseEntry();
		if (StringUtil.isEmpty(xml)) {
			if (logger.isDebugEnabled()) {
				logger.debug("bankcore server reponse is empty !");
			}
			// 设置错误码及错误信息
			busiContext.setErrorInfo(AppConstants.STATUS_FAIL, "服务器响应为空",
					transCode);
			return false;
		}
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(xml);
		} catch (DocumentException e) {
			logger.error(transCode + " receive data can't be to xml", e);
			busiContext.setErrorInfo(AppConstants.STATUS_FAIL, "交易响应解析失败",
					transCode);
			return false;
		}
		Element rootel = doc.getRootElement();
		// 解析错误码
		if (parserResponseStatus(rootel, busiContext, transCode)) {
			if (logger.isDebugEnabled()) {
				logger.debug("bankcore server reponse trans ok!" + transCode);
			}
			// 解析正常数据
			return parserBussisData(transCode, rootel, confParser, busiContext);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("bankcore server reponse trans error!" + transCode);
			}
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public boolean parserBussisData(String transCode, Element body,
			IEBankConfParser confParser, IBusinessContext busiContext) {
		MBTransConfBean conf = confParser.findTransConfById(transCode);
		if (logger.isDebugEnabled()) {
			logger.debug("parserBussisData  ..start.." + conf.toString());// 解析进来的方法
		}
		if (null != conf) {
			Element rctx = busiContext.getResponseContext(transCode);
			List<MBTransItem> rcv = conf.getRcv();
			Node tag;
			for (MBTransItem item : rcv) {
				if (logger.isDebugEnabled()) {
					logger.debug(transCode + " parserBussisData item:	"
							+ item.getName());
				}
				if (EBankConst.FILED_TYPE_E.equals(item.getType())) {
					if (logger.isDebugEnabled()) {
						logger.debug(transCode + "parserBussisData List item:	"
								+ item.getName());
					}
					// 列表内容
					List listNodes = body.selectNodes(item.getXmlPath());
					if (null == listNodes) {
						continue;
					}
					parserListData(transCode, item, listNodes, busiContext,
							rctx);
				} else {
					// 普通内容
					tag = body.selectSingleNode(item.getXmlPath());
					if (null != tag) {
						String text = tag.getText();
						if (item.isRequred() && StringUtil.isEmpty(text)) {
							if (logger.isDebugEnabled()) {
								logger.debug("item is required ,can't be empty:\n"
										+ item.toString());
							}
							busiContext.setErrorInfo(AppConstants.STATUS_FAIL,
									"无相关信息", transCode);
							return false;
						}

						Element itemElem = busiContext.saveNode(rctx,
								item.getTargetName(), text);
						parserItemDesc(busiContext, item, tag.getText(),
								itemElem);
					} else if (item.isRequred()) {
						busiContext.saveNode(rctx, item.getTargetName(),
								EBankConst.EMPTY);
					}
				}

			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("parserBussisData  ..end.." + conf.toString());
		}
		return true;
	}

	private boolean parserListData(String transCode, MBTransItem item,
			List<Node> entrys, IBusinessContext busiContext,
			Element parentElement) {
		if (entrys == null || entrys.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug(transCode + " list content is empty ");
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug(transCode + " list size:" + entrys.size());
			}
			String name, type;
			Node tag;
			List<MBTransItem> childMap = item.getChildren();
			Element listElem = parentElement.addElement(item.getTargetName());
			listElem.addAttribute(EBankConst.AT_TYPE, EBankConst.TYPE_LIST);
			// 遍历列表实体
			for (Node entry : entrys) {
				Element mapElem = listElem.addElement(AppConstants.XML_MAP);
				mapElem.addAttribute(EBankConst.AT_TYPE, EBankConst.TYPE_MAP);
				// 遍历实体结构
				for (MBTransItem mapItem : childMap) {
					type = mapItem.getType();
					if (logger.isDebugEnabled()) {
						logger.debug("list map item is :\n"
								+ mapItem.toString());
					}
					name = mapItem.getTargetName();
					if (EBankConst.FILED_TYPE_E.equals(type)) {
						List<Node> listNode = entry.selectNodes(mapItem
								.getXmlPath());
						parserListData(transCode, mapItem, listNode,
								busiContext, mapElem);
						continue;
					}
					tag = entry.selectSingleNode(mapItem.getXmlPath());
					if (null != tag) {
						String text = tag.getText();
						if (mapItem.isRequred() && StringUtil.isEmpty(text)) {
							if (logger.isDebugEnabled()) {
								logger.debug("list item is required ,can't be empty:\n"
										+ item.toString());
							}
							listElem.getParent().remove(listElem);
							busiContext.setErrorInfo(AppConstants.STATUS_FAIL,
									"无相关信息", transCode);
							return false;
						}
						busiContext.saveNode(mapElem, name, tag.getText());
						parserItemDesc(busiContext, mapItem, tag.getText(),
								mapElem);
					}
				}
			}
		}
		return true;
	}

	private void parserItemDesc(IBusinessContext busiCtx, MBTransItem item,
			String key, Element parent) {
		// 判断是否需要数据字典
		String descName = item.getDescName();
		if (StringUtil.isNotEmpty(key)
				&& StringUtil.isNotEmpty(item.getMapKey())
				&& StringUtil.isNotEmpty(descName)) {
			// 提取数据字典中的缓存设置
			Map<String, String> map = busiCtx.findOptionsMap(item.getMapKey());
			// 添加注释字段
			if (map != null) {
				String mapDesc = map.get(key);
				if (StringUtil.isEmpty(mapDesc)) {
					mapDesc = key;
				}
				parent.addElement(descName).setText(mapDesc);
			}
		}
	}

	/**
	 * 设置响应码及响应错误
	 * 
	 * @param rootel
	 * @param ctx
	 * @param transCode
	 */
	public boolean parserResponseStatus(Element rootel, IBusinessContext ctx,
			String transCode) {

		Node rstCode = rootel.selectSingleNode(AppConstants.STATUS);// 状态码
		Node rstMsg = rootel.selectSingleNode(AppConstants.MSG);// 返回信息

		String rspCode = null != rstCode ? rstCode.getText()
				: AppConstants.STATUS_FAIL;
		String rspMsg = null != rstCode ? rstMsg.getText()
				: AppConstants.MSG_FAIL;

		if (logger.isDebugEnabled()) {
			logger.debug(transCode + " reponse code:\t" + rspCode
					+ "\tresponse msg:" + rspMsg);
		}
		ctx.setRspCode(rspCode);
		ctx.setRspMsg(rspMsg);
		if (rspCode.equals(AppConstants.STATUS_OK)) {
			ctx.setErrorInfo(AppConstants.STATUS_OK, rspMsg, transCode);
			return true;
		}
		ctx.setErrorInfo(rspCode, rspMsg, transCode);
		return false;
	}

	public static void main(String[] args) throws DocumentException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource("data/jsnx/ECIP00001000001.xml");
		SAXReader reader = new SAXReader();
		Document doc = reader.read(new File(url.getPath()));
		String xmlStr = doc.asXML();
		IBusinessContext busiContext = new BusinessContext(IBusinessContext.PARAM_TYPE_MAP);
		busiContext.setResponseEntry(xmlStr);

	}

}