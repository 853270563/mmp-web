package cn.com.yitong.framework.net.impl.as;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.error.OtherRuntimeException;
import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.core.util.SecurityUtils;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.MBTransConfBean;
import cn.com.yitong.framework.core.vo.MBTransItem;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.service.ICommonService;
import cn.com.yitong.util.GetIpAddress;
import cn.com.yitong.util.JsonFormat;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * ESB请求报文构造组件
 * 
 * @author huangqiang@yitong.com.cn
 *
 */
@Component
public class RequestBuilderAs implements IRequstBuilder {

	static Logger logger = YTLog.getLogger(RequestBuilderAs.class.getName());

	private ICommonService service;

	public RequestBuilderAs() {
	}

	@Override
	public boolean buildSendMessage(IBusinessContext busiCtx,
			IEBankConfParser confParser, String transCode) {
		JSONObject json = new JSONObject();
		// JSONObject jHead = new JSONObject();
		JSONObject jBody = new JSONObject();

		// 解析报文体
		MBTransConfBean conf = confParser.findTransConfById(transCode);
		if (conf != null) {
			logger.info("loaded transconf successfully!" + transCode);
		} else {
			logger.error("loaded transconf failed!" + transCode);
			busiCtx.setErrorInfo(AppConstants.STATUS_FAIL, "交易定义加载失败!",
					transCode);
			return false;
		}

		if (StringUtil.isEmpty(conf.getExcode())) {
			logger.error("字段属性excode不能为空！" + transCode);
			busiCtx.setErrorInfo(AppConstants.STATUS_FAIL, "字段属性excode不能为空!",
					transCode);
			return false;
		} else {
			busiCtx.setParam(EBankConst.AT_EXCODE, conf.getExcode());
		}

		// 拼装固定报文头
		if (!builderReqHead(conf, busiCtx, json, transCode)) {
			logger.error("builder SYS_HEAD failed!" + transCode);
			busiCtx.setErrorInfo(AppConstants.STATUS_FAIL,
					"构建系统报文头【SYS_HEAD】失败!", transCode);
			return false;
		}

		// 拼装动态报文头
		List<MBTransItem> sndHead = conf.getSedHeader();
		if (!builderReqData(conf.getName(), busiCtx, sndHead, json, transCode)) {
			logger.error("builder SYS_HEAD failed!" + transCode);
			busiCtx.setErrorInfo(AppConstants.STATUS_FAIL,
					"构建系统报文头【SYS_HEAD】失败!", transCode);
			return false;
		}
		// 拼装报文体
		List<MBTransItem> sndBody = conf.getSed();
		if (!builderReqData(conf.getName(), busiCtx, sndBody, jBody, transCode)) {
			logger.error("builder BODY.CompositeData failed!" + transCode);
			busiCtx.setErrorInfo(AppConstants.STATUS_FAIL, "构建本地报文体【BODY】失败!",
					transCode);
			return false;
		}
		// 保存构建其请求报文字符串
		// json.put("header",jHead);
		json.put("body", jBody);
		busiCtx.setRequestEntry(json.toString());

		logger.info("\n请求报文为：" + JsonFormat.formatJson(json.toString()));
		return true;
	}

	/**
	 *
	 * @param atName
	 * @param busiCtx
	 * @param items
	 * @param json
	 * @param transCode
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public boolean builderReqHead(MBTransConfBean conf,
			IBusinessContext busiCtx, JSONObject json, String transCode) {
		try {
			String ip = GetIpAddress.getIP();
			json.put("strategy", "1");// 查询策略
			json.put("scene", "1");// 业务场景
			json.put("source", "07");// 发起系统
			json.put("ip", ip);// 发起系统ip
			json.put("orgId", "90080");// 机构id 
			json.put("userId", "app");// 发起用户
		} catch (Exception e) {
			logger.error("构建请求报文异常[交易代码:" + transCode + "]：", e);
			return false;
		}
		return true;
	}

	/**
	 *
	 * @param atName
	 * @param busiCtx
	 * @param items
	 * @param json
	 * @param transCode
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public boolean builderReqData(String atName, IBusinessContext busiCtx,
			List<MBTransItem> items, JSONObject json, String transCode) {
		for (MBTransItem item : items) {
			String name = item.getName();
			String targetName = item.getTargetName();
			targetName = StringUtil.isEmpty(targetName) ? name : targetName;
			String type = item.getType();
			String fieldValue = "";
			if (EBankConst.FILED_TYPE_C.equals(type)) {
				fieldValue = StringUtil.getString(busiCtx.getParamMap(),
						targetName, "");
				if (StringUtil.isEmpty(fieldValue)) {
					if (item.isRequired()
							&& StringUtil.isEmpty(item.getDefaultValue())) {
						logger.error("交易码【" + transCode + "】的接口，字段【"
								+ targetName + "】的值不能为空！");
						throw new OtherRuntimeException(
								EBankConst.FAIL_RETURN_CODE, "字段【" + targetName
										+ "】的值不能为空！");
					} else {
						if (StringUtil.isEmpty(item.getDefaultValue())) {
							// 补充空位
							fieldValue = "";
						} else {
							fieldValue = item.getDefaultValue();
						}
					}
				}
				logger.info("[" + targetName + "]:[" + fieldValue + "]");
				// 放入json
				json.put(targetName, fieldValue);
			} else if (EBankConst.FILED_TYPE_O.equals(type)) {
				builderObjectData(atName, busiCtx, item, json, transCode);
			} else if (EBankConst.FILED_TYPE_E.equals(type)) {
				JSONArray array = new JSONArray();
				builderListData(atName, busiCtx.getParamMap(), item, array, transCode);
				json.put(targetName, array);
			}
		}
		return true;
	}

	/**
	 * 深度组装对象
	 * 
	 * @param atName
	 * @param busiCtx
	 * @param items
	 * @param json
	 * @param transCode
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public boolean builderObjectData(String atName, IBusinessContext busiCtx,
			MBTransItem item, JSONObject json, String transCode) {
		JSONObject object = new JSONObject();
		Map map = (Map) busiCtx.getParamMap().get(item.getName());
		List<MBTransItem> items = item.getChildren();
		for (MBTransItem ObjItem : items) {
			String name = ObjItem.getName();
			String targetName = ObjItem.getTargetName();
			targetName = StringUtil.isEmpty(targetName) ? name : targetName;
			String type = ObjItem.getType();
			String fieldValue = "";
			if (EBankConst.FILED_TYPE_C.equals(type)) {
				fieldValue = StringUtil.getString(map, targetName, "");
				if (StringUtil.isEmpty(fieldValue)) {
					if (ObjItem.isRequired()
							&& StringUtil.isEmpty(ObjItem.getDefaultValue())) {
						logger.error("交易码【" + transCode + "】的接口，字段【"
								+ targetName + "】的值不能为空！");
						throw new OtherRuntimeException(
								EBankConst.FAIL_RETURN_CODE, "字段【" + targetName
										+ "】的值不能为空！");
					} else {
						if (StringUtil.isEmpty(ObjItem.getDefaultValue())) {
							// 补充空位
							fieldValue = "";
						} else {
							fieldValue = ObjItem.getDefaultValue();
						}
					}
				}
				logger.info("[" + targetName + "]:[" + fieldValue + "]");
				// 放入json
				object.put(targetName, fieldValue);
			} else if (EBankConst.FILED_TYPE_O.equals(type)) {
				builderObjectData(atName, busiCtx, ObjItem, object, transCode);
			} else if (EBankConst.FILED_TYPE_E.equals(type)) {
				JSONArray array = new JSONArray();
				builderListData(atName, map, ObjItem, array, transCode);
				object.put(targetName, array);
			}
		}
		json.put(item.getName(), object);
		return true;
	}

	/**
	 * 深度组装list
	 * 
	 * @param atName
	 * @param busiCtx
	 * @param items
	 * @param json
	 * @param transCode
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public boolean builderListData(String atName, Map busiMap,
			MBTransItem item, JSONArray array, String transCode) {
		List<MBTransItem> items = item.getChildren();
		JSONObject json = new JSONObject();
		ArrayList arrayList = (ArrayList) busiMap.get(
				item.getName());
		if (null != arrayList) {
			for (int i = 0; i < arrayList.size(); i++) {
				Map<String, String> jsontemp = (Map<String, String>) arrayList
						.get(i);
				for (MBTransItem itemChild : items) {
					String name = itemChild.getName();
					String targetName = itemChild.getTargetName();
					targetName = StringUtil.isEmpty(targetName) ? name
							: targetName;
					String type = itemChild.getType();
					String fieldValue = "";
					if (EBankConst.FILED_TYPE_C.equals(type)) {
//						LinkedHashMap<String, String> jsontemp = (LinkedHashMap<String, String>) arrayList
//								.get(i);
//						HashMap<String, String> jsontemp = (HashMap<String, String>) arrayList
//								.get(i);
						fieldValue = jsontemp.get(targetName);
						if (StringUtil.isEmpty(fieldValue)) {
							if (item.isRequired()
									&& StringUtil.isEmpty(item
											.getDefaultValue())) {
								logger.error("交易码【" + transCode + "】的接口，字段【"
										+ targetName + "】的值不能为空！");
								throw new OtherRuntimeException(
										EBankConst.FAIL_RETURN_CODE, "字段【"
												+ targetName + "】的值不能为空！");
							} else {
								if (StringUtil.isEmpty(itemChild
										.getDefaultValue())) {
									// 补充空位
									fieldValue = "";
								} else {
									fieldValue = itemChild.getDefaultValue();
								}
							}
						}
						logger.info("[" + targetName + "]:[" + fieldValue + "]");
						// 放入json
						json.put(targetName, fieldValue);
					} else if (EBankConst.FILED_TYPE_E.equals(type)) {
						JSONArray jsonArray = new JSONArray();
						builderListData(atName, jsontemp, itemChild, jsonArray,
								transCode);
						json.put(targetName, jsonArray);
					}
				}
				array.add(json);
			}
		}
		return true;
	}

}
