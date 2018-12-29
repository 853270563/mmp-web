package cn.com.yitong.framework.net.impl.db;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.MBTransConfBean;
import cn.com.yitong.framework.core.vo.MBTransItem;
import cn.com.yitong.framework.core.vo.TransLogBean;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.util.DateFormatter;
import cn.com.yitong.util.DateUtil;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 请求生成器
 */
@Component
public class RequestBuilder4db implements IRequstBuilder {

	private Logger logger = YTLog.getLogger(this.getClass());
	private boolean createXml = false;

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
			busiContext.setErrorInfo(AppConstants.STATUS_FAIL, "交易定义加载失败!", transCode);
			return false;
		}
		return buildDocument(busiContext, conf, transCode);
	}

	/**
	 * 创建请求消息体
	 */
	private boolean buildDocument(IBusinessContext ctx, MBTransConfBean conf,
			String transCode) {
		if (logger.isDebugEnabled()) {
			logger.debug("buildDocument ......start........" + transCode);
		}
		try {
			Element area = ctx.getRequestContext(transCode);
			List<MBTransItem> sed = conf.getSed();
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
	 */
	private boolean buildHeadContent(Element parent, IBusinessContext ctx, String transCode) {
		TransLogBean logbean = ctx.getTransLogBean(transCode);
		// 交易日期
		String tranDate = DateUtil.todayStr();
		logbean.setTransDate(tranDate);
		// 交易时间
		String tranTime = DateUtil.curTimeStr();
		logbean.setTransTime(tranTime);
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
				String tname = item.getClient();
				tname = StringUtil.isEmpty(name) ? tname : name;
				// 创建数据节点
				// 未设别循环体请求
				if (EBankConst.FILED_TYPE_E.equals(type)) {
					if (logger.isDebugEnabled()) {
						logger.debug(transCode + " list item: " + item.toString());
					}
					if (ctx.isMapParamType()) {
						List<Map> datas = ctx.getParamDatas(tname);
						if (item.isRequred() && (datas == null || datas.isEmpty())) {
							logger.error(transCode + ":" + tname + ":" + item.getDesc() + "不能为空!");
							ctx.setErrorInfo(AppConstants.STATUS_FAIL, tname + ":" + item.getDesc() + "不能为空!", transCode);
							return false;
						}
					}
					if (!createXml) {
						continue;
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
								mapElem.addElement(childName).setText(childText);
							}
						}
					}
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug(transCode + " common item: " + item.toString());
					}
					// 普通
					// 由xml中的name得到页面传来的值
					text = ctx.getParam(tname);
					if (StringUtil.isEmpty(text)) {
						text = item.getDefaultValue();
						if (item.isRequred() && StringUtil.isEmpty(text)) {
							logger.error(transCode + ":" + tname + ":" + item.getDesc() + "不能为空!");
							ctx.setErrorInfo(AppConstants.STATUS_FAIL, tname + ":" + item.getDesc() + "不能为空!", transCode);
							return false;
						}
					}else if (EBankConst.FILED_TYPE_DATE.equals(type) && !DateUtil.dateFormat(text,DateFormatter.SDF_YMD1)) {
						logger.error(transCode + ":" + tname + ":" + item.getDesc() + "请指定"+DateFormatter.SDF_YMD1+"格式!");
                        ctx.setErrorInfo(AppConstants.STATUS_FAIL, tname  + ":" + item.getDesc() + "请指定"+DateFormatter.SDF_YMD1+"格式!", transCode);
                        return false;
                    }
					if (logger.isDebugEnabled()) {
						logger.debug(transCode + "\tname:\t" + StringUtil.rpadString(name, 20) + " type:["
								+ type + "]\t length:" + StringUtil.rpadString("" + length, 3) + "text=[" + text + "]");
					}
					// 设置数据
					if (createXml && StringUtil.isNotEmpty(text)) {
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
		return true;
	}
}
