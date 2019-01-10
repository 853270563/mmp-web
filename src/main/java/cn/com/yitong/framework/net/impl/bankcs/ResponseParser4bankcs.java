package cn.com.yitong.framework.net.impl.bankcs;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.MBTransConfBean;
import cn.com.yitong.framework.core.vo.MBTransItem;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

@Component
public class ResponseParser4bankcs implements IResponseParser {
	private Logger logger = YTLog.getLogger(this.getClass());

	final int headerItemSize = 12;

	@Override
	public boolean parserResponseData(IBusinessContext busiContext,
			IEBankConfParser confParser, String transCode) {
		String response = (String) busiContext.getResponseEntry();
		if (null == response || "".equals(response.trim())) {
			logger.info("bankcore server reponse is empty !");
			// 设置错误码及错误信息
			busiContext.setErrorInfo(AppConstants.STATUS_FAIL, "服务器响应为空",
					transCode);
			return false;
		}
		logger.info("response message is:\n" + response);
		logger.info("response message length is:\n" + response.length());
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
		if (165 >= response.length()) {
			busiContext.setErrorInfo(AppConstants.STATUS_FAIL, "响应报文不正确!",
					transCode);
			return false;
		}
		response = response.substring(163);
		// 解析头部
		parserBussisDataHeader(transCode, response, confParser, busiContext);
		if (!AppConstants.AAAAAA.equals(busiContext.getRspCode())) {
			return false;
		}
		// 解析体部
		String body = response.substring(2);
		logger.info("response body:\n" + body);
		return parserBussisData(transCode, body, conf, busiContext);
	}

	@SuppressWarnings("unchecked")
	public boolean parserBussisDataHeader(String transCode, String body,
			IEBankConfParser confParser, IBusinessContext busiContext) {
		logger.info("parserBussisData  Header..1..");// 解析进来的方法
		// 163开始，头两位为返回码
		String rspCode = body.substring(0, 2);
		boolean isOk = EBankConst.SUCCESS_RETURN_CODE.equals(rspCode);
		// TODO: 转义错误码
		busiContext.setRspCode(isOk ? AppConstants.AAAAAA : rspCode);
		String rspMsg = "交易成功!";
		if (isOk) {
			busiContext.setErrorInfo(rspCode, rspMsg, transCode);
			return true;
		}
		rspMsg = body.substring(2);
		busiContext.setErrorInfo(rspCode, rspMsg, transCode);
		logger.info("parserBussisData  ..2..");

		return false;
	}
 
	@SuppressWarnings("unchecked")
	public boolean parserBussisData(String transCode, String body,
			MBTransConfBean conf, IBusinessContext busiContext) {
		logger.info("parserBussisData  ..1..");// 解析进来的方法
		int pos = 0;
		Element rctx = busiContext.getResponseContext(transCode);
		if (null != conf) {
			int i = 0;
			String text = "";
			List<MBTransItem> rcv = conf.getRcv();
			for (MBTransItem item : rcv) {
				logger.info(item.toString());
				int length = item.getLength();
				String type = item.getType();
				String name = item.getName();
				if (EBankConst.FILED_TYPE_C.equals(type)) {
					text = body.substring(pos, pos+length).trim();
					pos += length;
					logger.info("text:" + text);
					busiContext.saveNode(rctx, name, text);
					parserItemDesc(item, text, busiContext, rctx);
					
				} else if (EBankConst.FILED_TYPE_N.equals(type)) {
					text = body.substring(pos + 1,
							pos + 1 + length + item.getDolt());
					
					//處理小數值 將text按照小數位格式化
					int doltLen=text.length()-item.getDolt();
					if(doltLen>0&&item.getDolt()>0){
						text = Long.parseLong(text.substring(0, doltLen)) + "."
								+ text.substring(doltLen);			
					}
					
					busiContext.saveNode(rctx, name, text);
					pos += 1 + length + item.getDolt();
					
				} else if (EBankConst.FILED_TYPE_E.equals(type)) {
					Element listElem = rctx.addElement(item.getName());
					listElem.addAttribute(EBankConst.AT_TYPE, EBankConst.TYPE_LIST);
					for (int m = 0; m < length; m++) {
						// 固定记录数												
						Element rowElem = listElem .addElement(AppConstants.XML_MAP);
						rowElem.addAttribute(EBankConst.AT_TYPE, EBankConst.TYPE_MAP); // 取定义字段
						for (MBTransItem childItem : item.getChildren()) {
							int child_length = childItem.getLength();
							String child_type = childItem.getType();
							String child_name = childItem.getName();
							// 最多二级结构
							if (EBankConst.FILED_TYPE_C.equals(child_type)) {
								text = body.substring(pos, pos + child_length).trim();
								pos += child_length;
								busiContext.saveNode(rowElem, child_name, text);
								parserItemDesc(childItem, text, busiContext, rowElem);
							} else if (EBankConst.FILED_TYPE_N.equals(child_type)) {
								String flag = body.substring(pos, pos + 1);
								text = body.substring(pos + 1, pos + 1 + length
										+ childItem.getDolt());
								busiContext.saveNode(rowElem, name, text);
								pos += child_length + childItem.getDolt() + 1;
							}
						}
					}
				}
				i++;
			}
		}

		logger.info("parserBussisData  ..1111..");
		busiContext.saveNode(rctx, AppConstants.STATUS, AppConstants.STATUS_OK);
		busiContext.saveNode(rctx, AppConstants.MSG, "交易成功");

		logger.info("parserBussisData  ..22222..");
		return true;
	}

	private void parserItemDesc(MBTransItem item, String key,
			IBusinessContext busiCtx,Element elem) {
		// 判断是否需要数据字典
		String descName = item.getDescName();
		if (StringUtil.isNotEmpty(key)
				&& StringUtil.isNotEmpty(item.getMapKey())
				&& StringUtil.isNotEmpty(descName)) {
			// 提取数据字典中的缓存设置
			Map<String, String> map = null;
			busiCtx.findOptionsMap(key);
			// 添加注释字段
			if (map != null) {
				String mapDesc = map.get(key);
				busiCtx.saveNode(elem, descName, mapDesc);
			}
		}
	}
}
