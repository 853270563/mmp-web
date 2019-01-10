package cn.com.yitong.framework.net.impl.creditCore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.error.AresCoreException;
import cn.com.yitong.ares.error.AresRuntimeException;
import cn.com.yitong.common.utils.JsonUtils;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.MBTransConfBean;
import cn.com.yitong.framework.core.vo.MBTransItem;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.impl.NetConst;
import cn.com.yitong.util.DateUtil;
import cn.com.yitong.util.JsonFormat;
import cn.com.yitong.util.MapUtil;

/**
 * @author luanyu
 * @date   2018年8月9日
 */
@Component
public class RequestBuilder4CreditCore implements IRequstBuilder {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public boolean buildSendMessage(IBusinessContext ctx, IEBankConfParser confParser, String transCode) {
		MBTransConfBean conf = confParser.findTransConfById(transCode);
		if (conf == null) {
			logger.warn("config_not_definied: {}", transCode);
			throw new AresCoreException("net.config_not_definied", transCode);
		}
		Map requestMap = new HashMap();
		buildMsgHead(ctx.getParamMap(), conf, requestMap);
		// 消息体：
		buildMsgBody(ctx.getParamMap(), conf, requestMap);

		ctx.setRequestEntry(requestMap);
		logger.debug("格式化显示请求报文：\n{}", JsonFormat.formatJson(JsonUtils.objectToJson(requestMap)));
		return true;
	}

	/**
	 * @Title: buildMsgHead
	 * @Description: TODO 构建消息头
	 * @param ctx
	 * @param conf
	 * @param transCode
	 * @param bf
	 */
	@SuppressWarnings("")
	private void buildMsgHead(Map parameter, MBTransConfBean conf, Map transMap) {
		transMap.put("channel_no", "07");
		transMap.put("datamodel", "01");
		transMap.put("serial_no", System.currentTimeMillis());
		transMap.put("trans_date", DateUtil.todayStr());
		transMap.put("trans_tradetime", DateUtil.todayStr("yyyy-MM-dd HH:mm:ss"));
		transMap.put("sign_data", "0");
	}

	/**
	 * 生成报文主体
	 * 
	 * @param ctx
	 * @param conf
	 * @param transCode
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void buildMsgBody(Map parameter, MBTransConfBean conf, Map transMap) {
		// 手工填写

		List<MBTransItem> items = conf.getSed();
		for (MBTransItem item : items) {
			parserData(parameter, item, transMap);

		}
	}

	/**
	 * 递归解析请求参数
	 * @param param 参数
	 * @param item
	 * @param map 发送数据
	 */
	void parserData(Map param, MBTransItem item, Map map) {
		if (isListItem(item)) {
			// 取列表值
			List<Map> datas = (List<Map>) param.get(item.getName());
			if (null == datas)
				return;
			List<MBTransItem> children = item.getChildren();
			List list = new ArrayList();
			for (Map data : datas) {
				Map itemMap = new HashMap();
				// 遍历定义
				for (MBTransItem child : children) {
					parserData(data, child, itemMap);
				}
				list.add(itemMap);
			}
			map.put(item.getTargetName(), list); 
		} else if (isMapItem(item)) {
			//实体取值
			Map<String, Object> data = (Map<String, Object>) param.get(item.getName());
			if (null == data)
				return;
			List<MBTransItem> children = item.getChildren();
			Map itemMap = new HashMap();
			for (MBTransItem child : children) {
				parserData(data, child, itemMap);
			}
			map.put(item.getTargetName(), itemMap);
		} else {
			// 普通字段
			buildItem(param, item, map);
		}

	}

	/**
	 * 是否为列表结构
	 * 
	 * @param elem
	 * @return
	 */
	private boolean isListItem(MBTransItem item) {
		String type = item.getType();
		return NetConst.FILED_TYPE_E.equals(type);
	}

	/**
	 * 是否为实体结构
	 * 
	 * @param elem
	 * @return
	 */
	private boolean isMapItem(MBTransItem item) {
		String type = item.getType();
		return NetConst.FILED_TYPE_M.equals(type);
	}
	/**
	 * 生成字段内容
	 * 
	 * @param param
	 * @param item
	 * @param bfOut
	 */
	@SuppressWarnings("rawtypes")
	private void buildItem(Map param, MBTransItem item, Map map) {
		String name = item.getName();
		// 字段转换，如果设置了targetName则上送targetName对应的字段，否则送name字段
		String targetName = item.getTargetName();
		Object obj = MapUtil.getMapValue(param, name, item.getDefaultValue());
		if (obj == null || obj.equals("")) {
			if (item.isRequired()) {
				logger.warn("common.parameter_empty {}", item.getDesc());
				throw new AresRuntimeException("common.parameter_empty", item.getDesc());
			}
		}

		buildElement(map, targetName, obj);
	}

	private void buildElement(Map map, String name, Object value) {
		if (value != null && !value.equals("")) {

			map.put(name, value);
		}
	}
}
