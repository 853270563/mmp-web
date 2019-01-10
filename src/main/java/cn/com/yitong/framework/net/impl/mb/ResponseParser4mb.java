package cn.com.yitong.framework.net.impl.mb;

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

@Component
public class ResponseParser4mb implements IResponseParser {
	private Logger logger = YTLog.getLogger(this.getClass());

	public boolean parserResponseData(IBusinessContext busiContext,
			IEBankConfParser confParser, String transCode) {
		String xml = (String) busiContext.getResponseEntry();
		if (null == xml || "".equals(xml.trim())) {
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
			// 去除多余的前缀
			xml = xml.replaceAll("tfb:|soapenv:|NS1:|p0:", "");
			if (logger.isDebugEnabled()) {
				logger.debug("receive data:\n" + StringUtil.formatXmlStr(xml));
			}
			doc = DocumentHelper.parseText(xml);
		} catch (DocumentException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(transCode + " receive data can't be to xml", e);
			}
			busiContext.setErrorInfo(AppConstants.STATUS_FAIL, "交易响应解析失败",
					transCode);
			return false;
		}
		Element rootel = doc.getRootElement();
		// 设置响应码及响应错误
		if (parserResponseStatus(rootel, busiContext, transCode)) {
			if (logger.isDebugEnabled()) {
				logger.debug("bankcore server reponse trans ok! " + transCode);
			}
			// 解析正常数据
			Element body = (Element) rootel
					.selectSingleNode("Body/callServiceResponse/body/callAppResponse/body");
			parserBussisData(transCode, body, confParser, busiContext);
			return true;
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("bankcore server reponse trans error! "
						+ transCode);
			}
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public boolean parserBussisData(String transCode, Element parent,
			IEBankConfParser confParser, IBusinessContext ctx) {
		MBTransConfBean conf = confParser.findTransConfById(transCode);
		if (logger.isDebugEnabled()) {
			logger.debug("parserBussisData  ..start.." + conf.toString());// 解析进来的方法
		}
		if (null != conf) {
			String rspBodyName = conf.getProperty(EBankConst.AT_RSP_SERVICE);
			if (StringUtil.isEmpty(rspBodyName)) {
				if (logger.isDebugEnabled()) {
					logger.debug(transCode + " AT_RSP_BODY is empty !");
				}
				return false;
			}
			if (logger.isDebugEnabled()) {
				logger.debug(transCode + " AT_RSP_BODY is :" + rspBodyName);
			}
			Element rctx = ctx.getResponseContext(transCode);
			Element body = parent.element(rspBodyName);
			List<MBTransItem> rcv = conf.getRcv();
			Node tag;
			for (MBTransItem item : rcv) {
				String name = item.getName();
				String tname = item.getTargetName();
				String xpath = item.getXmlPath();
				// 优先targetName
				if (StringUtil.isEmpty(tname)) {
					tname = name;
				}
				logger.debug("parserBussisData item:	" + item.getName());
				if (EBankConst.FILED_TYPE_E.equals(item.getType())) {
					if (logger.isDebugEnabled()) {
						logger.debug("parserBussisData List item:	"
								+ item.getName());
					}
					// 列表内容
					List listNodes = body.selectNodes(xpath);
					if (null == listNodes) {
						continue;
					}
					parserListData(item, listNodes, rctx, ctx);
				} else {
					// 普通内容
					tag = body.selectSingleNode(xpath);
					if (null != tag) {
						String text = tag.getText();
						ctx.saveNode(rctx, tname, text);
						parserItemDesc(ctx, item, text, rctx);
					} else if (item.isRequred()) {
						ctx.saveNode(rctx, tname, EBankConst.EMPTY);
						if (logger.isDebugEnabled()) {
							logger.debug(transCode + "\t name:"
									+ item.getName() + "is emtpy!");
						}
					}
				}
			}
		}
		logger.debug("parserBussisData  ..end.." + conf.toString());
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
	 * 第一层列表
	 * 
	 * @param parent
	 * @param entrys
	 * @param ctx
	 */
	private void parserListData(MBTransItem parent, List<Node> entrys,
			Element rctx, IBusinessContext ctx) {
		if (entrys == null || entrys.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("list size is null!");
			}
		} else {
			Element listElem = rctx.addElement(parent.getName());
			listElem.addAttribute(EBankConst.AT_TYPE, EBankConst.TYPE_LIST);

			String name, tname, xpath, type;
			Node childData;
			List<MBTransItem> childMap = parent.getChildren();
			// 遍历列表实体
			int index = 0;
			String text = "";
			for (Node entry : entrys) {
				// 固定记录数
				Element rowElem = listElem.addElement(AppConstants.XML_MAP);
				rowElem.addAttribute(EBankConst.AT_TYPE, EBankConst.TYPE_MAP); // 取定义字段
				// 遍历实体结构
				for (MBTransItem item : childMap) {
					logger.debug("child item:" + item.toString());
					name = item.getName();
					type = item.getType();
					tname = item.getTargetName();
					xpath = item.getXmlPath();
					if (StringUtil.isEmpty(tname)) {
						tname = name;
					}
					if (StringUtil.isEmpty(xpath)) {
						xpath = name;
					}
					if (EBankConst.FILED_TYPE_E.equals(type)) {
						List<Node> childListData = entry.selectNodes(xpath);
						// 递归层次
						parserListData(item, childListData, rowElem, ctx);
					} else {
						childData = entry.selectSingleNode(xpath);
						try {
							text = childData.getText();
						} catch (Exception e) {
							text = "";
						}
						ctx.saveNode(rowElem, tname, text);
						parserItemDesc(ctx, item, text, rowElem);
					}
				}
				index++;
			}
			// 存储数组长度
			String sizeField = parent.getSizeField();
			if (StringUtil.isNotEmpty(sizeField)) {
				rctx.addElement(sizeField, "" + entrys.size());
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
		Node nodeCode = rootel
				.selectSingleNode("Body/callServiceResponse/header/returnCode");// 状态码
		if (nodeCode == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("return code: is null ----" + transCode);
			}
			ctx.setErrorInfo(AppConstants.STATUS_FAIL, "未获取返回码", transCode);
			return false;
		}
		// TODO:错误码需要转译
		String rspCode = null != nodeCode ? nodeCode.getText()
				: AppConstants.STATUS_FAIL;

		// TODO:可能为列表
		Node nodeMsg = rootel
				.selectSingleNode("Body/callServiceResponse/errorMsg/errors/desc");// 返回信息
		String rspMsg = null != nodeMsg ? nodeMsg.getText()
				: AppConstants.MSG_FAIL;
		/*
		 * String errorMsg = ErrorCodeProperties.getString(rspCode); if (null !=
		 * errorMsg && !"".equals(errorMsg)) { rspMsg = errorMsg; }
		 */

		ctx.setRspCode(rspCode);
		ctx.setRspMsg(rspMsg);
		boolean ok = false;
		// 错误码转译
		if (rspCode.equals(EBankConst.SUCCESS)) {
			if (logger.isDebugEnabled()) {
				logger.debug("reponse code:" + rspCode + "\t response msg:"
						+ AppConstants.MSG_SUCC);
			}
			rspCode = AppConstants.STATUS_OK;
			rspMsg = AppConstants.MSG_SUCC;
			ok = true;
		}
		if (logger.isDebugEnabled()) {
			logger.debug(transCode + " reponse code:\t" + rspCode
					+ "\t response msg:" + rspMsg);
		}
		ctx.setErrorInfo(rspCode, rspMsg, transCode);
		return ok;
	}

	public static void main(String[] args) throws DocumentException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource("data/jsnx/ECIP00001000001.xml");
		SAXReader reader = new SAXReader();
		Document doc = reader.read(new File(url.getPath()));
		String xmlStr = doc.asXML();
		IBusinessContext busiContext =new BusinessContext(IBusinessContext.PARAM_TYPE_MAP);
		busiContext.setResponseEntry(xmlStr);

	}

}