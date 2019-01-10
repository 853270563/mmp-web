package cn.com.yitong.framework.net.impl.bankcs;

import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.MBTransConfBean;
import cn.com.yitong.framework.core.vo.MBTransItem;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

@Component
public class RequestBuilder4bankcs implements IRequstBuilder {

	private Logger logger = YTLog.getLogger(this.getClass());

	@Override
	public boolean buildSendMessage(IBusinessContext busiContext,
			IEBankConfParser confParser, String transCode) {
		logger.info("RequestBuilder4cupd.buildSendMessage33 ......................"
				+ transCode);
		// 动态报文体
		MBTransConfBean conf = confParser.findTransConfById(transCode);
		if (conf != null) {
			logger.info("loaded transconf successfully!" + transCode);
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
		logger.info("RequestBuilder4cupd.buildDocument ......................");
		try {
			List<MBTransItem> sed = conf.getSed();
			StringBuffer bf = new StringBuffer();
			// 固定参数
			// 固定头
			buildHeadContent(bf, ctx, conf, transCode);
			// 特定结构
			return buildBodyContent(bf, sed, ctx, conf, transCode);
		} catch (Exception e) {
			logger.error("build the xml head error!", e);
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
	private boolean buildHeadContent(StringBuffer bf, IBusinessContext ctx,
			MBTransConfBean conf, String transCode) {
		String targetTransCode = conf.getName();
		if (StringUtil.isEmpty(targetTransCode)) {
			targetTransCode = "291001";
		}
		// 2910010002 0N291001 OJIB00 EB $
		// TXN_CODE: 6位
		bf.append(targetTransCode);
		// CTRL_BLK_IND:0000
		bf.append("0000 0N");
		// TXN_CODE
		bf.append(targetTransCode);
		// TXN_MNEM
		bf.append("     OJIB00 EB");
		// 232位填空字符
		bf.append(StringUtil.repeatString(" ", 222));
		return true;
	}

	/**
	 * 组装报文
	 */

	private boolean buildBodyContent(StringBuffer bf, List<MBTransItem> sed,
			IBusinessContext ctx, MBTransConfBean conf, String transCode) {
		logger.info("RequestBuilder4cupd.buildBodyContent ...........1...........");
		if (null == sed) {
			logger.info("交易自定义发送内容为空!");
			ctx.setErrorInfo(AppConstants.STATUS_FAIL, "发送内容为空!", transCode);
			return false;
		}
		logger.info("RequestBuilder4cupd.buildBodyContent ...........2...........");
		try {
			String text;
			int i = 0;
			for (MBTransItem item : sed) {
				int length = item.getLength();
				String type = item.getType();
				String name = item.getName();

				// 由xml中的name得到页面传来的值
				text = ctx.getParam(name);
				if (StringUtil.isEmpty(text)) {
					text = item.getDefaultValue();
					if (item.isRequred() && StringUtil.isEmpty(text)) {
						logger.info(item.getDesc() + "不能为空!");
						ctx.setErrorInfo(AppConstants.STATUS_FAIL,
								item.getDesc() + "不能为空!", transCode);
						return false;
					}
				}
				if (EBankConst.FILED_TYPE_C.equals(type)) {
					text = StringUtil.rpadString(text, length);
				} else if (EBankConst.FILED_TYPE_AC.equals(type)) {
					// 账号类型
					if (text.length() == 17) {
						text = text.substring(3);
					}
					text = StringUtil.rpadString(text, length);
				} else if (EBankConst.FILED_TYPE_N.equals(type)) {
					length = length + item.getDolt();
					text = text.replaceAll("\\.|\\+", "");
					text = "+" + StringUtil.lpadString(text, length, "0");
				} else if (EBankConst.FILED_TYPE_E.equals(type)) {
					// 数组内容: 最多二级数组
					List<Element> datas = ctx.getParamDatas(item.getXmlPath());
					for (Element elem : datas) {
						int m = 0;
						for (MBTransItem childItem : item.getChildren()) {
							int child_length = childItem.getLength();
							String child_type = childItem.getType();
							String child_name = childItem.getName();
							text = ctx.getParam(name);
							if (EBankConst.FILED_TYPE_C.equals(type)) {
								text = StringUtil
										.rpadString(text, child_length);
							} else if (EBankConst.FILED_TYPE_AC.equals(type)) {
								// 账号类型
								if (text.length() == 17) {
									text = text.substring(4);
								}
								text = StringUtil.rpadString(text, child_length);

							} else if (EBankConst.FILED_TYPE_N.equals(type)) {
								child_length = child_length
										+ childItem.getDolt();
								text = text.replaceAll("\\.|\\+", "");
								text = "+"
										+ StringUtil.lpadString(text,
												child_length, "0");
							}
							bf.append(text);

							logger.info("name:\t"
									+ StringUtil.rpadString(child_name, 20)
									+ " type:["
									+ child_type
									+ "]\t length:"
									+ StringUtil.rpadString("" + child_length,
											3) + "text=[" + text + "]");
							m++;
						}
					}
					continue;
				}
				logger.info("name:\t" + StringUtil.rpadString(name, 20)
						+ " type:[" + type + "]\t length:"
						+ StringUtil.rpadString("" + length, 3) + "text=["
						+ text + "]");
				bf.append(text);
				i++;
			}
			logger.info("RequestBuilder4cupd.buildBodyContent ...........3...........");
		} catch (Exception e) {
			logger.error("build the send data error!", e);
			ctx.setErrorInfo(AppConstants.STATUS_FAIL, "生成请求内容出错!", transCode);
			return false;
		}
		// 拼装请求报文
		ctx.setRequestEntry(bf.toString());
		logger.info("busiCtx.request string is :\n" + ctx.getRequestEntry());
		return true;
	}

	public static void main(String[] args) {
		// String
		// temp="291001                                                                                                                                                             ";
		String temp = "+3.0000";

		temp = temp.replaceAll("\\.|\\+", "");

		System.out.println("temp:\n" + temp);
	}
}
