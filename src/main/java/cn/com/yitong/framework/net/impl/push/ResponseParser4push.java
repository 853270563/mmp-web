package cn.com.yitong.framework.net.impl.push;

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

/**
 * PUSH响应报文解析器
 * 
 * @author yaoym
 * 
 * 
 */
@Component
public class ResponseParser4push implements IResponseParser {
	private Logger logger = YTLog.getLogger(this.getClass());

	public boolean parserResponseData(IBusinessContext busiContext,
			IEBankConfParser confParser, String transCode) {
		String content = (String) busiContext.getResponseEntry();
		if (StringUtil.isEmpty(content)) {
			if (logger.isDebugEnabled()) {
				logger.debug("esb server reponse is empty !");
			}
			// 设置错误码及错误信息
			busiContext.setErrorInfo(AppConstants.STATUS_FAIL, "服务器响应为空",
					transCode);
			return false;
		}
		if (logger.isDebugEnabled()) {
			logger.debug(transCode + " receive all data:\n" + content);
		}
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(content);
			Element esbRoot = doc.getRootElement();
			// 解析ESB错误码
			if (parserResponseStatus(esbRoot, busiContext, transCode)) {
				if (logger.isDebugEnabled()) {
					logger.debug("esb server reponse trans ok!" + transCode);
				}
				MBTransConfBean conf = confParser.findTransConfById(transCode);
				Element rootel = doc.getRootElement();
				// 解析正常数据
				parserBussisData(transCode, rootel, conf, busiContext);
				return true;
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("esb server reponse trans error!" + transCode);
				}
				return false;
			}
		} catch (DocumentException e) {
			logger.error(transCode + " receive data can't covert to xml", e);
			busiContext.setErrorInfo(AppConstants.STATUS_FAIL, "交易响应解析失败",
					transCode);
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public void parserBussisData(String transCode, Element root,
			MBTransConfBean conf, IBusinessContext busiContext) {
		if (logger.isDebugEnabled()) {
			logger.debug("esb parserBussisData  ..start.." + conf.toString());// 解析进来的方法
		}
		Node body = root.selectSingleNode("body/rsp");
		if (null != conf && body != null) {
			Element rctx = busiContext.getResponseContext(transCode);
			List<MBTransItem> rcv = conf.getRcv();
			Node tag;
			for (MBTransItem item : rcv) {
				if (logger.isDebugEnabled()) {
					logger.debug(transCode + " parserBussisData item:	"
							+ item.getName());
				}
				if (EBankConst.FILED_TYPE_E.equals(item.getType())) {
					logger.info(transCode + " parserBussisData List item:	"
							+ item.getName());
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
						logger.info("text:" + text);
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
			logger.debug("esb parserBussisData  ..success.." + conf.toString());
		}
	}

	private void parserListData(String transCode, MBTransItem item,
			List<Node> entrys, IBusinessContext busiContext,
			Element parentElement) {
		if (entrys == null || entrys.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug(transCode + " list content is empty ");
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.info(transCode + " list size:" + entrys.size());
			}
			String name, type;
			Node tag;
			List<MBTransItem> childMap = item.getChildren();
			Element listElem = parentElement.addElement(item.getName());
			listElem.addAttribute(EBankConst.AT_TYPE, EBankConst.TYPE_LIST);
			// 遍历列表实体
			for (Node entry : entrys) {
				Element mapElem = listElem.addElement(AppConstants.XML_MAP);
				mapElem.addAttribute(EBankConst.AT_TYPE, EBankConst.TYPE_MAP);
				// 遍历实体结构
				for (MBTransItem mapItem : childMap) {
					type = mapItem.getType();
					name = mapItem.getName();
					if (EBankConst.FILED_TYPE_E.equals(type)) {
						List<Node> listNode = entry.selectNodes(mapItem
								.getXmlPath());
						parserListData(transCode, mapItem, listNode,
								busiContext, mapElem);
						continue;
					}
					tag = entry.selectSingleNode(mapItem.getXmlPath());
					if (null != tag) {
						busiContext.saveNode(mapElem, name, tag.getText());
						parserItemDesc(busiContext, mapItem, tag.getText(),
								mapElem);
					}
				}
			}
		}
	}

	/**
	 * 描述信息
	 * 
	 * @param busiCtx
	 * @param item
	 * @param key
	 * @param parent
	 */
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
	 * ESB数据 - 设置响应码及响应错误
	 * 
	 * @param rootel
	 * @param ctx
	 * @param transCode
	 */
	public boolean parserResponseStatus(Element rootel, IBusinessContext ctx,
			String transCode) {
		Node rspCodeNode = rootel.selectSingleNode("header/code");// 状态码
		if (rspCodeNode == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("return code: is null ----" + transCode);
			}
			ctx.setErrorInfo(AppConstants.STATUS_FAIL, "未获取返回码", transCode);
			return false;
		}
		String returnCode = rspCodeNode.getText();
		boolean ok = false;
		String rspCode, rspMsg;
		if (EBankConst.RSP_CODE_SUCCESS.equals(returnCode)) {
			rspCode = AppConstants.STATUS_OK;
			rspMsg = AppConstants.MSG_SUCC;
			ok = true;
		} else {
			rspCode = returnCode;
			Node rspMsgNode = rootel.selectSingleNode("header/msg");// 状态码
			rspMsg = null != rspMsgNode ? rspMsgNode.getText()
					: AppConstants.MSG_FAIL;
		}
		ctx.setRspCode(rspCode);
		ctx.setRspMsg(rspMsg);
		if (logger.isDebugEnabled()) {
			logger.debug(transCode + " reponse code:\t" + rspCode
					+ "\t response msg:" + rspMsg);
		}
		ctx.setErrorInfo(rspCode, rspMsg, transCode);
		return ok;
	}

}