package cn.com.yitong.framework.net.impl.creditCore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.error.AresCoreException;
import cn.com.yitong.ares.error.OtherRuntimeException;
import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.MBTransConfBean;
import cn.com.yitong.framework.core.vo.MBTransItem;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.net.impl.NetConst;
import cn.com.yitong.util.MapUtil;

/**
 * @author luanyu
 * @date   2018年8月9日
 */
@Component
public class ResponsParse4CreditCore implements IResponseParser {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Override
	public boolean parserResponseData(IBusinessContext busiContext, IEBankConfParser confParser, String transCode) {
		MBTransConfBean conf = confParser.findTransConfById(transCode);
		if (conf == null) {
			logger.warn("config_not_definied: {}", transCode);
			throw new AresCoreException("net.config_not_definied", transCode);
		}
			busiContext.getParamMap().clear();
		Map rsp = (Map) busiContext.getResponseEntry();
		if (rsp == null) {
			return false;
		}
		Map map = new LinkedMap();
		// 取定义
		parseHeader(rsp, conf, map);
		parseBody(rsp, conf, map);
		busiContext.getParamMap().putAll(map);
		return true;

	}

	private void parseHeader(Map rsp, MBTransConfBean conf, Map map) {
		// TODO Auto-generated method stub
		Object object = rsp.get("recode");
		if ("000000".equals(object)) {
			return;
		}
			throw new OtherRuntimeException(AppConstants.STATUS_FAIL, rsp.get("recode_info").toString());
	
	}

	/**
	 * 解析响应baowen
	 * @param parameter 响应数据
	 * @param conf 
	 * @param transMap 解析以后的数据
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void parseBody(Map parameter, MBTransConfBean conf, Map transMap) {
		// 手工填写
		List<MBTransItem> items = conf.getRcv();
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
			List<Map> datas = (List<Map>) param.get(item.getTargetName());
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
			map.put(item.getName(), list);
		} else if (isMapItem(item)) {
			//实体取值
			Map<String, Object> data = (Map<String, Object>) param.get(item.getTargetName());
			if (null == data)
				return;
			List<MBTransItem> children = item.getChildren();
			Map itemMap = new HashMap();
			for (MBTransItem child : children) {
				parserData(data, child, itemMap);
			}
			map.put(item.getName(), itemMap);
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
		Object obj = MapUtil.getMapValue(param, targetName, item.getDefaultValue());
		buildElement(map, name, obj);
	}

	private void buildElement(Map map, String name, Object value) {


		map.put(name, value == null ? "" : value);

	}

}
