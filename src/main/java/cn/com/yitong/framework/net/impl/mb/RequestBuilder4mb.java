package cn.com.yitong.framework.net.impl.mb;

import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.QName;
import org.springframework.stereotype.Component;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.consts.NS;
import cn.com.yitong.consts.SessConsts;
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
public class RequestBuilder4mb implements IRequstBuilder {

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
		if (!buildDocument(busiContext, conf, transCode)) {
			logger.error("loaded transconf is failed!" + conf.toString());
			busiContext.setErrorInfo(AppConstants.STATUS_FAIL, "交易定义加载失败!",
					transCode);
			return false;
		}
		Element reqCtx = busiContext.getRequestContext(transCode);
		busiContext.setRequestEntry(reqCtx.element("soapenv:Envelope").asXML());
		if (logger.isDebugEnabled()) {
			logger.debug(conf.toString() + " \n request str:\n"
					+ busiContext.getRequestEntry());
		}
		return true;
	}

	/**
	 * 创建请求消息体
	 * 
	 * @param dataMap
	 * @param rst
	 * @return
	 */
	private boolean buildDocument(IBusinessContext ctx, MBTransConfBean conf,
			String transCode) {
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
			buildHeadContent(callService, conf, ctx, transCode);
			// 报文体
			return buildBodyContent(callService, conf, ctx, transCode);
		} catch (Exception e) {
			logger.error(transCode + " build the xml head error!", e);
		}
		ctx.setErrorInfo(AppConstants.STATUS_FAIL, "请求消息体加载失败!", transCode);
		return false;
	}

	/**
	 * 报文头
	 * 
	 * @param parent
	 * @param ctx
	 * @param transCode
	 * @return
	 */
	private boolean buildHeadContent(Element parent, MBTransConfBean conf,
			IBusinessContext ctx, String transCode) {
		// 交易日期、交易时间、交易流水、交易渠道、客户号、网银登录编号、交易号
		// 公共头属性需要向外传递
		Element header = parent.addElement("NS1:header");
		TransLogBean logBean = ctx.getTransLogBean(transCode);

		// 网银参数
		String chanelType = ctx.getChanelType();
		boolean withoutSession = StringUtil.isNotEmpty(chanelType);
		if (withoutSession) {
			String cifNo = ctx.getParam(NS.CIF_NO);
			if (StringUtil.isNotEmpty(cifNo)) {
				logBean.setCifNo(cifNo);
			}
			String ibsLgnId = ctx.getParam(NS.IBS_LGN_ID);
			if (StringUtil.isNotEmpty(ibsLgnId)) {
				logBean.setIbsLgnId(ibsLgnId);
			}
		} else {
			String cifNo = ctx.getSessionText(SessConsts.CIF_NO);
			if (StringUtil.isNotEmpty(cifNo)) {
				logBean.setCifNo(cifNo);
			}
			String ibsLgnId = ctx.getSessionText(SessConsts.LOGIN_ID);
			if (StringUtil.isNotEmpty(ibsLgnId)) {
				logBean.setIbsLgnId(ibsLgnId);
			}
		}

		// // 交易流水号
		ctx.getTransLogBean(transCode);
		ctx.saveNode(header, "NS1:version", "0.1");
		ctx.saveNode(header, "NS1:source", "IBS");
		ctx.saveNode(header, "NS1:proxy", "IBCP");
		String serviceName = conf.getName();
		if (StringUtil.isEmpty(serviceName)) {
			serviceName = conf.getProperty(EBankConst.AT_ESB_NAME);
		}
		ctx.saveNode(header, "NS1:service", serviceName);
		ctx.saveNode(header, "NS1:date", DateUtil.todayStr("yyyy-MM-dd"));
		// ctx.saveNode(header, "NS1:date", "2024-03-01");//bancs要求,後面要去掉
		ctx.saveNode(header, "NS1:time", DateUtil.curTimeStr());
		ctx.saveNode(header, "NS1:reqJournalNo", logBean.getTransSeqNo());
		// ctx.saveNode(header, "NS1:channel", "N");
		return true;
	}

	/**
	 * 组装报文
	 */
	private boolean buildBodyContent(Element root, MBTransConfBean conf,
			IBusinessContext ctx, String transCode) {
		if (logger.isDebugEnabled()) {
			logger.debug("buildBodyContent start..........." + transCode);
			logger.debug("btt context is:\n" + ctx.getBaseContext().asXML());
		}

		Element rootBody = root.addElement("NS1:body");
		Element repService = rootBody.addElement("NS1:callApp");

		String previewFlag = ctx.getParam(EBankConst.PREVIEW_FLAG);
		Element innerHeader = repService.addElement("NS1:header");
		if (StringUtil.isEmpty(previewFlag)) {
			previewFlag = "N";
		}
		ctx.saveNode(innerHeader, "NS1:previewFlag", previewFlag);

		Element parent = repService.addElement("NS1:body");
		// 设置头部的信息
		List<MBTransItem> fields = conf.getSedHeader();
		if (fields != null) {
			for (MBTransItem item : fields) {
				String iname = item.getName();
				String itname = item.getTargetName();
				String text = ctx.getParam(itname);
				if (item.isRequred() && StringUtil.isEmpty(text)) {
					text = item.getDefaultValue();
				}
				if (StringUtil.isEmpty(text)) {
					ctx.saveNode(parent, "NS1:" + itname, text);
				}
			}
		}
		String reqIbcpService = conf.getProperty(EBankConst.AT_REQ_SERVICE);
		Element body = parent.addElement("NS1:" + reqIbcpService);
		List<MBTransItem> sed = conf.getSed();
		if (null == sed || sed.isEmpty()) {
			logger.debug(transCode + " 交易自定义发送内容为空!");
			ctx.setErrorInfo(AppConstants.STATUS_FAIL, "发送内容为空!", transCode);
			return true;
		}
		try {
			String text;
			for (MBTransItem item : sed) {
				int length = item.getLength();
				String type = item.getType();
				String name = item.getName();
				String tname = item.getTargetName();
				// 优先targetName
				if (StringUtil.isEmpty(tname)) {
					tname = name;
				}
				// 未设别循环体请求
				if (EBankConst.FILED_TYPE_E.equals(type)) {
					// 循环体 取数据
				} else {
					// 普通
					// 由xml中的name得到页面传来的值
					text = ctx.getParam(tname);
					if (StringUtil.isEmpty(text)) {
						text = item.getDefaultValue();
						if (item.isRequred() && StringUtil.isEmpty(text)) {
							if (logger.isDebugEnabled()) {
								logger.debug(transCode + ":" + tname + ":"
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
					// 创建数据节点
					if (StringUtil.isNotEmpty(text)) {
						Element elem = body.addElement("NS1:" + name);
						elem.setText(text);
					}
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("buildBodyContent ..end.." + conf.toString());
			}
		} catch (Exception e) {
			logger.error(transCode + " build the send data error!", e);
			ctx.setErrorInfo(AppConstants.STATUS_FAIL, "MB通讯 生成请求内容出错!",
					transCode);
			return false;
		}
		return true;
	}

	public static void main(String[] args) {

	}

}