package cn.com.yitong.framework.net.impl.netloan;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.error.OtherRuntimeException;
import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.MBTransConfBean;
import cn.com.yitong.framework.core.vo.MBTransItem;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 请求ESB响应数据解析
 * @author huangqiang@yitong.com.cn
 *
 */
@Component
public class ResponseParserNetLoan implements IResponseParser  {
	static Logger logger = YTLog.getLogger(ResponseParserNetLoan.class.getName());
	public ResponseParserNetLoan() {
	}


	@Override
	public boolean parserResponseData(IBusinessContext bCtx,
									  IEBankConfParser confParser, String transCode) {
		String rcvMsg =  (String)bCtx.getResponseEntry();
		JSONObject rcvMsgJson = JSONObject.fromObject(rcvMsg);
		JSONObject rcvHead = rcvMsgJson.getJSONObject("header");
		JSONObject rcvBody = rcvMsgJson.getJSONObject("body").getJSONObject("output");
		Map outmap = new HashMap();
		if (null==rcvMsgJson || rcvMsgJson.size()<=0) {
			logger.info("bankcore server reponse is empty !");
			// 设置错误码及错误信息
			bCtx.setErrorInfo(AppConstants.STATUS_FAIL, "服务器响应为空",
					transCode);
			return false;
		}
		// 动态报文体
		MBTransConfBean conf = confParser.findTransConfById(transCode);
		if (null != conf) {
			logger.info("loaded transconf successfully!" + transCode);
		} else {
			logger.error("loaded transconf failed!" + transCode);
			bCtx.setErrorInfo(AppConstants.STATUS_FAIL, "交易定义加载失败!",
					transCode);
			return false;
		}

		if(!checkStatus(bCtx,rcvBody,transCode)){
			//解析报文头
			String errorCode = rcvBody.getString("result");
			if(errorCode.equals("0002")){
				errorCode = "1";
			}
			String errorMsg = rcvBody.getString("reason_fail");
			throw new OtherRuntimeException(errorCode, errorMsg);
			
		}else{
			//解析报文头
			if(!parserBussisData(transCode,rcvHead,conf.getRcvHeader(), bCtx, outmap)){
				return  false;
			}
			//解析报文体
			if(!parserBussisData(transCode,rcvBody,conf.getRcv(), bCtx, outmap)){
				return  false;
			}
		}
		bCtx.getParamMap().putAll(outmap);
		logger.info("数据总线数据：");
		return true;
	}
	/**
	 * 解析响应报文头
	 * @param bCtx
	 * @param rvcMsg
	 * @param transCode
	 * @return
	 */
	public boolean checkStatus(IBusinessContext bCtx,JSONObject rvcMsg,String transCode){
		try {
			String msgId = rvcMsg.getString("result");
			String msgId0 = "000000";
			if(msgId0.equals(msgId)){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			bCtx.setErrorInfo(AppConstants.STATUS_FAIL, "交易通讯异常", transCode);
			logger.warn("解析响应报文SYS_HEAD异常："+e.getMessage());
		}
		return false;
	}


	/**
	 * 解析报文
	 * @param transCode
	 * @param rcvJson
	 * @param lists
	 * @param bCtx
	 * @return
	 */
	public boolean parserBussisData(String transCode,JSONObject rcvJson,
									List<MBTransItem> lists, IBusinessContext bCtx, Map map) {
		try {
			Element rCtx = bCtx.getResponseContext(transCode);
			for (MBTransItem item : lists) {
				logger.info(item.toString());
				String type = item.getType();
				String name = item.getName();
				String clientName = item.getTargetName();
				clientName = StringUtil.isEmpty(clientName) ? name : clientName;
				if (EBankConst.FILED_TYPE_C.equals(type)) {
					String feildValue = rcvJson.getString(name);
					if(StringUtil.isEmpty(feildValue)){
						feildValue = "";
					}
					map.put(clientName, feildValue.trim());
//					bCtx.saveNode(rCtx, clientName, feildValue.trim());
				}else if(EBankConst.FILED_TYPE_E.equals(type)){
					parserListData(transCode, rcvJson, item, bCtx, rCtx,map);
				}else if(EBankConst.FILED_TYPE_O.equals(type)){
					parserObjectData(transCode, rcvJson, item, bCtx, rCtx,map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * 解析报文
	 * @param transCode
	 * @param rcvJson
	 * @param lists
	 * @param bCtx
	 * @return
	 */
	public boolean parserListData(String transCode,JSONObject rcvJson,
									MBTransItem item, IBusinessContext bCtx,Element rCtx, Map map) {
		try {
//			Element rCtx = bCtx.getResponseContext(transCode);
			List<MBTransItem> lists = item.getChildren();
			List<Object> outList = new ArrayList<Object>();
				JSONArray array = rcvJson.getJSONArray(item.getName());
				for(int i = 0; i < array.size(); i++){
					Map listMap = new HashMap();
					JSONObject json = array.getJSONObject(i);
					for (MBTransItem itemChild : lists) {
						String type = itemChild.getType();
						String name = itemChild.getName();
						String clientName = itemChild.getTargetName();
						clientName = StringUtil.isEmpty(clientName) ? name : clientName;
						if (EBankConst.FILED_TYPE_C.equals(type)) {
							String feildValue = json.getString(name);
							if(StringUtil.isEmpty(feildValue)){
								feildValue = "";
							}
							listMap.put(clientName,feildValue.trim());
//							bCtx.saveNode(rCtx, clientName, feildValue.trim());
						}else if(EBankConst.FILED_TYPE_E.equals(type)){
							parserListData(transCode, json, itemChild, bCtx, rCtx,listMap);
						}
					}
					outList.add(listMap);
				}
				map.put(item.getName(), outList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	
	/**
	 * 解析实体结构
	 * @param transCode
	 * @param rcvJson
	 * @param lists
	 * @param bCtx
	 * @return
	 */
	public boolean parserObjectData(String transCode,JSONObject rcvJson,
									MBTransItem item, IBusinessContext bCtx,Element rCtx, Map map) {
		try {
			List<MBTransItem> lists = item.getChildren();
			JSONObject returnObj = new JSONObject();
//			List<Object> outList = new ArrayList<Object>();
			JSONObject obj = rcvJson.getJSONObject(item.getName());
			if(null !=obj && !obj.isEmpty()){
				Map listMap = new HashMap();
				for (MBTransItem itemChild : lists) {
					String type = itemChild.getType();
					String name = itemChild.getName();
					String clientName = itemChild.getTargetName();
					clientName = StringUtil.isEmpty(clientName) ? name : clientName;
					if (EBankConst.FILED_TYPE_C.equals(type)) {
						String feildValue = obj.getString(name);
						if(StringUtil.isEmpty(feildValue)){
							feildValue = "";
						}
						logger.info(clientName+","+feildValue);
						returnObj.put(clientName,feildValue.trim());
					}else if(EBankConst.FILED_TYPE_E.equals(type)){
						parserListData(transCode, obj, itemChild, bCtx, rCtx,listMap);
					}
				}
			}
			map.put(item.getName(), returnObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	
}