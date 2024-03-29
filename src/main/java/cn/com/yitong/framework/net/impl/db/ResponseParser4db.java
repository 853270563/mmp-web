package cn.com.yitong.framework.net.impl.db;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.MBTransConfBean;
import cn.com.yitong.framework.core.vo.MBTransItem;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.util.MessageTools;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 响应报文解析器
 * 
 * @author yaoym
 * 
 * 
 */
@Component
public class ResponseParser4db implements IResponseParser {

	private Logger logger = YTLog.getLogger(this.getClass());

	public boolean parserResponseData(IBusinessContext busiContext, IEBankConfParser confParser, String transCode) {
		Object entry = busiContext.getResponseEntry();
		if (entry == null || !(entry instanceof Map)) {
			if (logger.isDebugEnabled()) {
				logger.debug("响应内容为空或为非法内容!");
			}
			// 设置错误码及错误信息
			busiContext.setErrorInfo(AppConstants.STATUS_FAIL, "响应内容为空或为非法内容", transCode);
			return false;
		}
		Map rst = (Map) entry;
		// 检查错误码
		if (parserResponseStatus(busiContext, transCode)) {
			if (logger.isDebugEnabled()) {
				logger.debug("bankcore server reponse trans ok!" + transCode);
			}
			// 检查返回数据
			return parserBussisData(transCode, rst, confParser, busiContext);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("bankcore server reponse trans error!" + transCode);
			}
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public boolean parserBussisData(String transCode, Map rst,
			IEBankConfParser confParser, IBusinessContext busiContext) {
		MBTransConfBean conf = confParser.findTransConfById(transCode);
		if (logger.isDebugEnabled()) {
			logger.debug("parserBussisData  ..start.." + conf.toString());// 解析进来的方法
		}
		if (null != conf) {
			Element rctx = busiContext.getResponseContext(transCode);
			List<MBTransItem> rcv = conf.getRcv();
			Object tag;
			for (MBTransItem item : rcv) {
				if (logger.isDebugEnabled()) {
					logger.debug(transCode + " parserBussisData item:	" + item.getName());
				}
				if (EBankConst.FILED_TYPE_E.equals(item.getType())) {
					if (logger.isDebugEnabled()) {
						logger.debug(transCode + "parserBussisData List item:	" + item.getName());
					}
					// 列表内容
					Object nodes = rst.get(item.getXmlPath());
					if (null == nodes || !(nodes instanceof List)) {
						if (logger.isDebugEnabled()) {
							logger.debug(transCode + "List item is empty or validator:	" + item.getName());
						}
						continue;
					}
					List listNodes = (List) nodes;
					parserListData(transCode, item, listNodes, busiContext, rctx);
				} else {
					// 普通内容
					tag = (Object) rst.get(item.getXmlPath());
					if (null != tag) {
					    //输出文本
					    String text = null;
					    //array类型
                        if (EBankConst.FILED_TYPE_ARRAY.equals(item.getType())) {
                            //数组转化json字符串
                            text = toJson(tag);
                        } else {
                            text = tag.toString();
                        }
						if (item.isRequred() && StringUtil.isEmpty(text)) {
							logger.error("item is required ,can't be empty:\n" + item.toString());
							busiContext.setErrorInfo(AppConstants.STATUS_FAIL, "无相关信息", transCode);
							return false;
						}
						Element itemElem = busiContext.saveNode(rctx, item.getTargetName(), text);
						parserItemDesc(busiContext, item, text, itemElem);
					} else if (item.isRequred()) {
						busiContext.saveNode(rctx, item.getTargetName(), EBankConst.EMPTY);
					}
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("parserBussisData   " + transCode + " responose is :\n" + rctx.asXML());
			}
			// 只提供符合要求的内容
			Map temp = new HashMap();
			if (MessageTools.elementToMap(rctx, temp)) {
				rst.clear();
				rst.putAll(temp);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("parserBussisData  ..end.." + conf.toString());
		}
		return true;
	}

	private boolean parserListData(String transCode, MBTransItem item, List<Map> entrys, IBusinessContext busiContext,
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
			Object tag;
			List<MBTransItem> childMap = item.getChildren();
			Element listElem = parentElement.addElement(item.getTargetName());
			listElem.addAttribute(EBankConst.AT_TYPE, EBankConst.TYPE_LIST);
			// 遍历列表实体
			for (Map entry : entrys) {
				Element mapElem = listElem.addElement(AppConstants.XML_MAP);
				mapElem.addAttribute(EBankConst.AT_TYPE, EBankConst.TYPE_MAP);
				// 遍历实体结构
				for (MBTransItem mapItem : childMap) {
					type = mapItem.getType();
					if (logger.isDebugEnabled()) {
						logger.debug("list map item is :\n" + mapItem.toString());
					}
					name = mapItem.getTargetName();
					if (EBankConst.FILED_TYPE_E.equals(type)) {
						List listNode = (List) entry.get(mapItem.getXmlPath());
						parserListData(transCode, mapItem, listNode, busiContext, mapElem);
						continue;
					}
					tag = entry.get(mapItem.getXmlPath());
					if (null != tag) {
						String text = tag.toString();
						if (mapItem.isRequred() && StringUtil.isEmpty(text)) {
							logger.error("list item is required ,can't be empty:\n" + item.toString());
							listElem.getParent().remove(listElem);
							busiContext.setErrorInfo(AppConstants.STATUS_FAIL, "无相关信息", transCode);
							return false;
						}
						busiContext.saveNode(mapElem, name, text);
						parserItemDesc(busiContext, mapItem, text, mapElem);
					}
				}
			}
		}
		return true;
	}

	private void parserItemDesc(IBusinessContext busiCtx, MBTransItem item, String key, Element parent) {
		// 判断是否需要数据字典
		String descName = item.getDescName();
		if (StringUtil.isNotEmpty(key) && StringUtil.isNotEmpty(item.getMapKey())
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
	 */
	public boolean parserResponseStatus(IBusinessContext ctx, String transCode) {
		String rspCode = ctx.getResponseStatus();// 获取响应状态码
		String rspMsg = ctx.getRspMsg();// 获取响应信息
		logger.debug("rspCode:" + rspCode);
		if (AppConstants.STATUS_OK.equals(rspCode)) {
			return true;
		}
		// 未定义响应码及响应错误
		if (rspCode == null || rspCode == "")
			rspCode = AppConstants.STATUS_FAIL;
		if (rspMsg == null || rspMsg == "")
			rspMsg = "未定义错误信息" + AppConstants.MSG_FAIL;
		if (logger.isDebugEnabled()) {
			logger.debug(transCode + " reponse code:\t" + rspCode + "\tresponse msg:" + rspMsg);
		}
		ctx.setErrorInfo(rspCode, rspMsg, transCode);
		return false;
	}
	
    /**
     * 数组转化为json字符串
     * @param object 数组
     * @return json字符串
     */
    private String toJson(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            return null;
        }
    }
}