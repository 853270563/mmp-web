package cn.com.yitong.framework.net.impl.push;

import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
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
public class RequestBuilder4push implements IRequstBuilder {

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
		if (logger.isDebugEnabled()) {
			logger.debug(conf.toString() + " \n request str:\n"
					+ busiContext.getRequestEntry());
		}
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
			Document doc = DocumentHelper.createDocument();
			Element service = doc.addElement("service");
			Element header = service.addElement("header");
			// 报文头
			if (!buildHeadContent4MB(header, conf, ctx, transCode)) {
				ctx.setErrorInfo(AppConstants.STATUS_FAIL, "请求消息头加载失败!",
						transCode);
				return false;
			}
			Element body = service.addElement("body");
			Element req = body.addElement("req");
			// 报文体
			if (!buildBodyContent(req, conf, ctx, transCode)) {
				ctx.setErrorInfo(AppConstants.STATUS_FAIL, "请求消息体加载失败!",
						transCode);
				return false;
			}
			ctx.setRequestEntry(doc.asXML());
			return true;
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
	private boolean buildHeadContent4MB(Element header, MBTransConfBean conf,
			IBusinessContext ctx, String transCode) {
		// 交易日期、交易时间、交易流水、交易渠道、客户号、网银登录编号、交易号
		// 公共头属性需要向外传递
		TransLogBean logbean = ctx.getTransLogBean(transCode);
		ctx.saveNode(header, "version", "0.1");
		ctx.saveNode(header, "source", "IBS");
		ctx.saveNode(header, "service", conf.getName());
		ctx.saveNode(header, "date", DateUtil.todayStr("yyyy-MM-dd"));
		ctx.saveNode(header, "time", DateUtil.curTimeStr());
		ctx.saveNode(header, "reqJournalNo", logbean.getTransSeqNo());
		return true;
	}

	/**
	 * 组装报文
	 */
	private boolean buildBodyContent(Element parent, MBTransConfBean conf,
			IBusinessContext ctx, String transCode) {
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