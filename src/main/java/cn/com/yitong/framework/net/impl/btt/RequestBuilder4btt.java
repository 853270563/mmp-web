package cn.com.yitong.framework.net.impl.btt;

import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;
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

/**
 * 请求生成器
 * 
 * @author yaoym
 * 
 */
@Component
public class RequestBuilder4btt implements IRequstBuilder {

	private Logger logger = YTLog.getLogger(this.getClass());

	@Override
	public boolean buildSendMessage(IBusinessContext busiContext,
			IEBankConfParser confParser, String transCode) {
		if (logger.isDebugEnabled()) {
			logger.debug("buildSendMessage ......start........" + transCode);
		}
		// 动态报文体
		MBTransConfBean conf = confParser.findTransConfById(transCode);
		if (conf != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("loaded transconf successfully!" + transCode);
			}
		} else {
			logger.error("loaded transconf failed!" + transCode);
			busiContext.setErrorInfo(AppConstants.STATUS_FAIL, "交易定义加载失败!",
					transCode);
			return false;
		}
		return buildDocument(busiContext, conf, transCode);
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
			logger.debug("buildDocument ......start........" + transCode);
		}
		try {
			Element area = ctx.getRequestContext(transCode);
			List<MBTransItem> sed = conf.getSed();

			// 固定参数
			ctx.setParam("tranDate", DateUtil.todayStr("yyyy-MM-dd"));
			ctx.setParam("tranTime", DateUtil.curTimeStr().replaceAll(":", ""));
			// 报文头
			buildHeadContent(area, ctx, transCode);
			// 报文体
			return buildBodyContent(area, sed, ctx, transCode);
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
	private boolean buildHeadContent(Element parent, IBusinessContext ctx,
			String transCode) {
		TransLogBean logbean = ctx.getTransLogBean(transCode);
		// BTT 参数
		parent.addElement("dse_operationName").setText(transCode);
		parent.addElement("dse_sessionId")
				.setText(ctx.getHttpSession().getId());
		parent.addElement("dse_pageId").setText("-1");
		parent.addElement("extend.channel_btt").setText("1");
		// 网银参数
		String chanelType = ctx.getChanelType();
		boolean withoutSession = StringUtil.isNotEmpty(chanelType);
		if (withoutSession) {
			// 网银唯一标识
			String cifNo = ctx.getParam(NS.CIF_NO);
			if (StringUtil.isNotEmpty(cifNo)) {
				parent.addElement(EBankConst.CIF_NO).setText(cifNo);
				logbean.setCifNo(cifNo);
			}
			// 网银客户号
			String tfbcifNo = ctx.getParam(NS.TFB_CIF_NO);
			if (StringUtil.isNotEmpty(tfbcifNo)) {
				parent.addElement(NS.TFB_CIF_NO).setText(tfbcifNo);
				logbean.setCifNo(tfbcifNo);
			}
			// 网银登录号
			String loginId = ctx.getParam(NS.IBS_LGN_ID);
			if (StringUtil.isNotEmpty(loginId)) {
				parent.addElement(EBankConst.IBS_LGN_ID).setText(loginId);
				logbean.setIbsLgnId(loginId);
			}
			// 語言
			String language = ctx.getParam("LAGG");
			if (StringUtil.isNotEmpty(language)) {
				parent.addElement(EBankConst.LAGG).setText(language);
			} else {
				parent.addElement(EBankConst.LAGG).setText("HK");
			}
			// UAS用户ID
			String uas_userId = ctx.getSessionText(NS.UAS_USER_ID);
			if (StringUtil.isNotEmpty(uas_userId)) {
				parent.addElement(EBankConst.UAS_USER_ID).setText(uas_userId);
				logbean.setIbsLgnId(uas_userId);
			}
			//渠道标识
			parent.addElement(EBankConst.CHANNEL).setText("IB");
		} else {
			// 网银唯一标识
			String cifNo = ctx.getSessionText(SessConsts.CIF_NO);
			if (StringUtil.isNotEmpty(cifNo)) {
				parent.addElement(EBankConst.CIF_NO).setText(cifNo);
				logbean.setCifNo(cifNo);
			}
			// 网银客户号
			String tfbcifNo = ctx.getSessionText(NS.TFB_CIF_NO);
			if (StringUtil.isNotEmpty(tfbcifNo)) {
				parent.addElement(NS.TFB_CIF_NO).setText(tfbcifNo);
				logbean.setCifNo(tfbcifNo);
			}
			// 网银登录号
			String loginId = ctx.getSessionText(NS.IBS_LGN_ID);
			if (StringUtil.isNotEmpty(loginId)) {
				parent.addElement(EBankConst.IBS_LGN_ID).setText(loginId);
				logbean.setIbsLgnId(loginId);
			}
			// 語言
			String language = ctx.getSessionText("LAGG");
			if (StringUtil.isNotEmpty(language)) {
				parent.addElement(EBankConst.LAGG).setText(language);
			} else {
				parent.addElement(EBankConst.LAGG).setText("HK");
			}
			// UAS用户ID
			String uas_userId = ctx.getSessionText(NS.UAS_USER_ID);
			if (StringUtil.isNotEmpty(uas_userId)) {
				parent.addElement(EBankConst.UAS_USER_ID).setText(uas_userId);
				logbean.setIbsLgnId(uas_userId);
			}
			//渠道标识
			parent.addElement(EBankConst.CHANNEL).setText("IB");
		}

		// 交易日期
		String tranDate = DateUtil.todayStr();
		parent.addElement(EBankConst.TRAN_DATE).setText(tranDate);
		logbean.setPropery(EBankConst.TRAN_DATE, tranDate);
		// 交易时间
		String tranTime = DateUtil.curTimeStr();
		parent.addElement(EBankConst.TRAN_TIME).setText(tranTime);
		logbean.setPropery(EBankConst.TRAN_TIME, tranTime);
		// 交易流水号
		parent.addElement(EBankConst.TRAN_SEQ).setText(logbean.getTransSeqNo());

		return true;
	}

	/**
	 * 组装报文
	 */
	private boolean buildBodyContent(Element parent, List<MBTransItem> sed,
			IBusinessContext ctx, String transCode) {
		if (logger.isDebugEnabled()) {
			logger.debug("buildBodyContent ......start....." + transCode);
		}
		if (null == sed) {
			if (logger.isDebugEnabled()) {
				logger.debug("交易自定义发送内容为空!");
			}
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
				tname = StringUtil.isEmpty(tname) ? name : tname;
				// 创建数据节点
				// 未设别循环体请求
				if (EBankConst.FILED_TYPE_E.equals(type)) {
					if (logger.isDebugEnabled()) {
						logger.debug(transCode + " list item: "
								+ item.toString());
					}
					Element elem = parent.addElement(name);
					// 循环体--数据
					List<Element> datas = ctx.getParamDatas(item.getXmlPath());
					Element sizeField = parent.addElement(item.getSizeField());
					sizeField.setText("" + datas.size());
					for (Element itemData : datas) {
						// 循环体--定义
						Element mapElem = elem.addElement("map");
						for (MBTransItem childItem : item.getChildren()) {
							String childName = childItem.getName();
							String childText = "";
							Element childNode = itemData.element(childName);
							if (childNode != null) {
								childText = childNode.getText();
							}
							if (StringUtil.isNotEmpty(childText)) {
								mapElem.addElement(childName)
										.setText(childText);
							}
						}
					}
				} else {
					// 普通
					// 由xml中的name得到页面传来的值
					text = ctx.getParam(name);
					if (StringUtil.isEmpty(text)) {
						text = item.getDefaultValue();
						if (item.isRequired() && StringUtil.isEmpty(text)) {
							if (logger.isDebugEnabled()) {
								logger.debug(transCode + ":" + name + ":"
										+ item.getDesc() + "不能为空!");
							}
							ctx.setErrorInfo(AppConstants.STATUS_FAIL, name
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
						ctx.saveNode(parent, tname, text);
					}
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("buildBodyContent ......success." + transCode);
			}
		} catch (Exception e) {
			logger.error(transCode + " build the send data error!", e);
			ctx.setErrorInfo(AppConstants.STATUS_FAIL, "生成请求内容出错!", transCode);
			return false;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("busiCtx.request string is :\n"
					+ ctx.getRequestContext(transCode).asXML());
		}
		return true;
	}

}
