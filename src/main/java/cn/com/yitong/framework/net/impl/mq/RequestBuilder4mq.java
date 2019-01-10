package cn.com.yitong.framework.net.impl.mq;

import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.springframework.stereotype.Component;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.MBTransConfBean;
import cn.com.yitong.framework.core.vo.MBTransItem;
import cn.com.yitong.framework.core.vo.TransLogBean;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.util.DateUtil;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

@Component
public class RequestBuilder4mq implements IRequstBuilder {

	private Logger logger = YTLog.getLogger(this.getClass());

	@Override
	public boolean buildSendMessage(IBusinessContext busiContext,
			IEBankConfParser confParser, String transCode) {
		logger.info("buildSendMessage ......................");
		// 动态报文体
		MBTransConfBean conf = confParser.findTransConfById(transCode);
		if (conf != null) {
			if (logger.isInfoEnabled()) {
				logger.info("loaded transconf successfully!" + conf.toString());
			}
		} else {
			logger.error("loaded transconf failed!" + conf.toString());
			busiContext.setErrorInfo(AppConstants.STATUS_FAIL, "交易定义加载失败!",
					transCode);
			return false;
		}
		if (!buildDocument4MB(busiContext, conf, transCode)) {
			logger.error("loaded transconf is failed!" + conf.toString());
			busiContext.setErrorInfo(AppConstants.STATUS_FAIL, "交易定义加载失败!",
					transCode);
			return false;
		}
		Element reqCtx = busiContext.getRequestContext(transCode);
		QName qname = new QName("soapenv:Envelope");
		busiContext
				.setRequestEntry("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ reqCtx.element(qname).asXML());
		if (logger.isDebugEnabled()) {
			logger.debug(conf.toString() + " \n request str:\n"
					+ busiContext.getRequestEntry());
		}

		// esb请求

		return true;
	}

	/**
	 * MB请求 - 创建请求消息体
	 * 
	 * @param dataMap
	 * @param rst
	 * @return
	 */
	private boolean buildDocument4MB(IBusinessContext ctx,
			MBTransConfBean conf, String transCode) {
		if (logger.isDebugEnabled()) {
			logger.debug("buildDocument ...start." + conf.toString());
		}
		try {
			Element area = ctx.getRequestContext(transCode);
			QName qname = new QName("soapenv:Envelope");
			Element reqElem = area.addElement(qname);
			reqElem.addNamespace("soapenv",
					"http://schemas.xmlsoap.org/soap/envelope/");
			reqElem.addNamespace("NS1", "http://www.taifungbank.com/TFBService");

			reqElem.addElement("soapenv:Header");
			Element body01 = reqElem.addElement("soapenv:Body");
			Element callService = body01.addElement("NS1:callService");

			// 报文头
			return buildHeadContent4MB(callService, conf, ctx, transCode);
			// 报文体
			// return buildBodyContent(callService, conf, ctx, transCode);
		} catch (Exception e) {
			logger.error(transCode + " build the xml head error!", e);
		}
		ctx.setErrorInfo(AppConstants.STATUS_FAIL, "请求消息体加载失败!", transCode);
		return false;
	}

	/**
	 * MB请求 - 报文头
	 * 
	 * @param parent
	 * @param ctx
	 * @param transCode
	 * @return
	 */
	private boolean buildHeadContent4MB(Element parent, MBTransConfBean conf,
			IBusinessContext ctx, String transCode) {
		// 交易日期、交易时间、交易流水、交易渠道、客户号、网银登录编号、交易号
		// 公共头属性需要向外传递
		TransLogBean logbean = ctx.getTransLogBean(transCode);
		Element header = parent.addElement("NS1:header");
		TransLogBean logBean = ctx.getTransLogBean(transCode);
		// // 交易流水号
		ctx.getTransLogBean(transCode);
		ctx.saveNode(header, "NS1:version", "0.1");
		ctx.saveNode(header, "NS1:source", "IBS");
		ctx.saveNode(header, "NS1:service", conf.getName());
		ctx.saveNode(header, "NS1:date", DateUtil.todayStr("yyyy-MM-dd"));
		ctx.saveNode(header, "NS1:time", DateUtil.curTimeStr());
		ctx.saveNode(header, "NS1:reqJournalNo", logbean.getTransSeqNo());

		Element reqbody = parent.addElement("NS1:body");
		Element reqCallApp = reqbody.addElement("NS1:callApp");
		Element reqCallAppHeader = reqCallApp.addElement("NS1:header");
		ctx.saveNode(reqCallAppHeader, "NS1:previewFlag", "Y");
		ctx.saveNode(reqCallAppHeader, "NS1:channel", "N");

		Element reqCallAppBody = reqCallApp.addElement("NS1:body");
		Element reqCallAppBodyData = reqCallAppBody.addElement("NS1:data");
		buildDocument(reqCallAppBodyData, ctx, conf, transCode);

		return true;
	}

	/**
	 * 卡司请求-创建请求消息体
	 * 
	 * @param dataMap
	 * @param rst
	 * @return
	 */
	private boolean buildDocument(Element dataEle, IBusinessContext ctx,
			MBTransConfBean conf, String transCode) {
		if (logger.isDebugEnabled()) {
			logger.debug("buildDocument ...start." + conf.toString());
		}
		try {
			Document doc = DocumentHelper.createDocument();
			QName qname = new QName("EaiMsg");
			Element reqElem = doc.addElement(qname);
			reqElem.addNamespace("xsi",
					"http://www.w3.org/2001/XMLSchema-instance");
			reqElem.addNamespace("schemaLocation",
					"http://www.bochk.com/eai EaiMessage.xsd");

			Element elemEaiRtn = reqElem.addElement("EaiRtn");
			Element elemEaiHdr = reqElem.addElement("EaiHdr");
			Element elemEaiTxn = reqElem.addElement("EaiTxn");
			Element elemEaiTxnHdr = elemEaiTxn.addElement("EaiTxnHdr");
			Element elemEaiTxnFrm = elemEaiTxn.addElement("Frm");
			Element elemEaiFrmHdr = elemEaiTxnFrm.addElement("FrmHdr");
			Element elemEaiFrmData = elemEaiTxnFrm.addElement("FrmData");
			// 报文头 EaiRtn
			buildEaiRtn(ctx, elemEaiRtn);
			// 报文头 EaiHdr
			buildEaiHdr(elemEaiHdr, conf, ctx, transCode);
			// 报文头 EaiTxnHdr
			buildEaiTxnHdr(elemEaiTxnHdr, conf, ctx, transCode);
			// 报文头 FrmHdr
			buildEaiFrmHdr(ctx, elemEaiFrmHdr);
			// 设置公共报文体
			buildEaiFrmData(ctx, elemEaiFrmData, transCode);
			// 设置交易报文体
			buildBodyContent(elemEaiFrmData, ctx, conf, transCode);

			String data = doc.asXML();
			if (StringUtil.isNotEmpty(data)) {
				dataEle.addCDATA(data);
				return true;
			}
		} catch (Exception e) {
			logger.error(transCode + " build the xml head error!", e);
		}
		ctx.setErrorInfo(AppConstants.STATUS_FAIL, "请求消息体加载失败!", transCode);
		return false;
	}

	/**
	 * 卡司请求-报文头-EaiHdr
	 * 
	 * @param parent
	 * @param ctx
	 * @param transCode
	 * @return
	 */
	private boolean buildEaiRtn(IBusinessContext ctx, Element parent) {
		ctx.addNode(parent, "EaiDmTxn", "00000");
		ctx.addNode(parent, "EaiRc", "00000");
		ctx.addNode(parent, "EaiErrMsg", "");
		return true;
	}

	/**
	 * 
	 * @param ctx
	 * @param parent
	 * @return
	 */
	private boolean buildEaiFrmHdr(IBusinessContext ctx, Element parent) {
		ctx.addNode(parent, "FmId", "I0000001");
		return true;
	}

	/**
	 * 
	 * @param ctx
	 * @param parent
	 * @return
	 */
	private boolean buildEaiFrmData(IBusinessContext ctx, Element parent,
			String transCode) {
		TransLogBean bean = ctx.getTransLogBean(transCode);
		ctx.addNode(parent, "UnqKey", bean.getTransSeqNo());// 唯一鍵
		ctx.addNode(parent, "ReqTS",
				DateUtil.todayStr("yyyy-MM-dd HH:mm:ss.SSS"));// 交易时间
		return true;
	}

	/**
	 * 报文头-EaiHdr
	 * 
	 * @param parent
	 * @param ctx
	 * @param transCode
	 * @return
	 */
	private boolean buildEaiHdr(Element parent, MBTransConfBean conf,
			IBusinessContext ctx, String transCode) {
		ctx.saveNode(parent, "elemEaiTxn", "01");
		ctx.saveNode(parent, "TxnMd", "TFB");
		ctx.saveNode(parent, "AcDt", DateUtil.todayStr("yyyyMMdd"));
		ctx.saveNode(parent, "TxnDt", DateUtil.todayStr("yyyyMMdd"));
		ctx.saveNode(parent, "TxnTm", "141323");
		ctx.saveNode(parent, "SrcAppJnlNo", "00000000000");
		ctx.saveNode(parent, "SrcAppLkgCnt", "01");
		ctx.saveNode(parent, "TlrId", "000000");

		ctx.saveNode(parent, "OvrSts", "N");
		ctx.saveNode(parent, "SupId1", "000000");
		ctx.saveNode(parent, "OvrLvl1", "00");
		ctx.saveNode(parent, "SupId2", "000000");
		ctx.saveNode(parent, "OvrLvl2", "00");
		ctx.saveNode(parent, "BkCd", "889");
		ctx.saveNode(parent, "SrcRgnCd", "MAC");
		ctx.saveNode(parent, "ChnlId", "031");
		ctx.saveNode(parent, "EtyCnt", "001");
		return true;
	}

	/**
	 * 报文头-EaiTxnHdr
	 * 
	 * @param parent
	 * @param ctx
	 * @param transCode
	 * @return
	 */
	private boolean buildEaiTxnHdr(Element parent, MBTransConfBean conf,
			IBusinessContext ctx, String transCode) {
		TransLogBean logBean = ctx.getTransLogBean(transCode);
		// String cifNo = ctx.getSessionText(SessConsts.CIF_NO);
		// if (StringUtil.isNotEmpty(cifNo)) {
		// logBean.setCifNo(cifNo);
		// }
		// String ibsLgnId = ctx.getSessionText(SessConsts.LOGIN_ID);
		// if (StringUtil.isNotEmpty(ibsLgnId)) {
		// logBean.setIbsLgnId(ibsLgnId);
		// }

		ctx.saveNode(parent, "TxnPrty", "0");
		ctx.saveNode(parent, "TarRgnCd", "HKG");
		ctx.saveNode(parent, "TarAppId", "BCC");
		ctx.saveNode(parent, "TarTxnId", conf.getName());
		ctx.saveNode(parent, "TxnBkCd", "889");
		return true;
	}

	/**
	 * 组装报文
	 */
	private boolean buildBodyContent(Element parent, IBusinessContext ctx,
			MBTransConfBean conf, String transCode) {
		if (logger.isDebugEnabled()) {
			logger.debug("buildBodyContent .....start....." + conf.toString());
		}
		List<MBTransItem> sed = conf.getSed();
		if (null == sed) {
			logger.info("交易自定义发送内容为空!" + conf.toString());
			ctx.setErrorInfo(AppConstants.STATUS_FAIL, "发送内容为空!", transCode);
			return false;
		}
		try {
			String text;
			for (MBTransItem item : sed) {
				int length = item.getLength();
				String type = item.getType();
				String name = item.getName();
				String tname = item.getTargetName();
				String xpath = item.getXmlPath();
				tname = StringUtil.isEmpty(tname) ? name : tname;
				xpath = StringUtil.isEmpty(xpath) ? tname : xpath;
				// 创建数据节点
				// 未设别循环体请求
				if (EBankConst.FILED_TYPE_E.equals(type)) {
					Element elem = parent.addElement(xpath);
					// 循环体--数据
					List<Element> datas = ctx.getParamDatas(tname);
					Element sizeField = parent.addElement(item.getSizeField());
					sizeField.setText("" + datas.size());
					if (logger.isDebugEnabled()) {
						logger.debug(transCode + " item: " + item.toString());
					}
					for (Element itemData : datas) {
						// 循环体--定义
						Element mapElem = elem.addElement(EBankConst.AT_ROW);
						for (MBTransItem childItem : item.getChildren()) {
							String childName = childItem.getName();
							String childText = "";
							String childXpath = childItem.getXmlPath();
							childXpath = StringUtil.isEmpty(childXpath) ? childName
									: childXpath;
							Element childNode = itemData.element(childName);
							if (childNode != null) {
								childText = childNode.getText();
							}
							if (StringUtil.isNotEmpty(childText)) {
								saveNode(ctx, mapElem, childXpath, childText);
							}
						}
					}
				} else {
					// 普通
					// 由xml中的name得到页面传来的值
					text = ctx.getParam(tname);
					if (StringUtil.isEmpty(text)) {
						text = item.getDefaultValue();
						if (item.isRequred() && StringUtil.isEmpty(text)) {
							if (logger.isDebugEnabled()) {
								logger.debug(transCode + ":" + name + ":"
										+ item.getDesc() + "不能为空!");
							}
							ctx.setErrorInfo(AppConstants.STATUS_FAIL, tname
									+ ":" + item.getDesc() + "不能为空!", transCode);
							return false;
						}
					}
					if (logger.isDebugEnabled()) {
						logger.debug(transCode + "\tname:\t"
								+ StringUtil.rpadString(name, 20) + " type:["
								+ type + "]\t length:"
								+ StringUtil.rpadString("" + length, 3)
								+ "text=[" + text + "]");
					}
					// 设置数据
					if (StringUtil.isNotEmpty(text)) {
						saveNode(ctx, parent, xpath, text);
					}
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("buildBodyContent ....success..."
						+ conf.toString());
			}
		} catch (Exception e) {
			logger.error(transCode + " build the send data error!", e);
			ctx.setErrorInfo(AppConstants.STATUS_FAIL, "生成请求内容出错!", transCode);
			return false;
		}
		return true;
	}

	/**
	 * 加载节点
	 * 
	 * @param ctx
	 * @param parent
	 * @param xpath
	 * @param text
	 * @return
	 */
	private Element saveNode(IBusinessContext ctx, Element parent,
			String xpath, String text) {
		text = StringUtil.isEmpty(text) ? "" : text;
		String[] arys = xpath.split("\\/");
		if (arys.length == 1) {
			return ctx.saveNode(parent, xpath, text);
		}
		Element last = parent;
		for (String name : arys) {
			Element tmp = last.element(name);
			if (tmp == null) {
				tmp = last.addElement(name);
			}
			last = tmp;
		}
		last.setText(text);
		return last;
	}

}