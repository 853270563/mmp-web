package cn.com.yitong.ares.mp.service;

import cn.com.yitong.ares.mp.entity.MpCustMsg;
import cn.com.yitong.ares.mp.push.JmsPush;
import cn.com.yitong.ares.mp.util.JPushClientHandler;
import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.service.ICrudService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 消息推送服务
 * @author sunwei (sunw@yitong.com.cn)
 *
 */
@Service
public class MsgPushService {
	
	public static final Logger L = LoggerFactory.getLogger(MsgPushService.class);
	@Autowired
	ICrudService service;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Transactional
	public void scanAndSend() { 
		List<Map> custMsgList = service.findList("MP_MSG_PUSH.queryCustMsgList", null);
		if (custMsgList == null || custMsgList.isEmpty()) {
			return;
		}
        L.info("准备发送" + custMsgList.size() + "条消息");
		List<Map<String, String>> taskMsgTypeList = service.findList("MP_MSG_PUSH.selectMsgType", null);
		Map<String, String> taskMsgTypeMap = new HashMap<String, String>();
		if (taskMsgTypeList != null && !taskMsgTypeList.isEmpty()) {
			for (Map<String, String> map : taskMsgTypeList) {
				taskMsgTypeMap.put(map.get("TASK_ID"), map.get("MSG_TYPE"));
			}
		}
		
		Set<String> taskIdSet = new HashSet<String>();
		for (final Map map : custMsgList) {
			MpCustMsg mpCustMsg = this.parseMapToMpCustMsg(map);
			String msgID = mpCustMsg.getMsgId();
			Map params = new HashMap();
			params.put("MSG_ID", msgID);
			params.put("STATUS", "1");
			params.put("SEND_TIME", new Date());
			// 更新客户消息状态
			boolean success = service.update("MP_MSG_PUSH.updateCustMsgStatusB", params);
			if (!success) {
				L.info("忽略消息id:" + msgID + ",该消息已被非当前节点处理");
				continue;
			}
			// 更新消息任务状态
			params.clear();
			String TASK_ID = this.ObjectToString(map.get("TASK_ID"));
			if (!taskIdSet.contains(TASK_ID)) {
				taskIdSet.add(TASK_ID);
				params.put("TASK_ID", TASK_ID);
				params.put("STATUS", "4");
				service.update("MP_MSG_PUSH.updateMsgTaskStatus", params);
			}
			mpCustMsg.setMsgType(taskMsgTypeMap.get(TASK_ID));
			try {
                if(AppConstants.push_type.equals("1")){
                    // mqtt 推送
                    push2MQTT(mpCustMsg);
                }else if(AppConstants.push_type.equals("2")){
                    //极光推送
                    pushJiGuang(mpCustMsg);
                }
			} catch (Exception e) {
				e.printStackTrace();
				L.error(e.getMessage(), e);
				params.clear();
				params.put("MSG_ID", msgID);
				params.put("STATUS", "3");
				service.update("MP_MSG_PUSH.updateCustMsgStatus", params);
			}
		}  
	}

    public void pushJiGuang(MpCustMsg msg){
        String clientType=msg.getClientType();//客户端类型 0 IOS 1安卓
        String title=msg.getMsgTitle();
        String context=msg.getMsgBody();
        //String alias=msg.getCustId()+"_"+msg.getDeviceId();
        String clickType=msg.getClickActType();
        String clickBody=msg.getClickActBody();
        BigDecimal distVal= msg.getDistVal();
        Map extraMap= new HashMap();
        extraMap.put("clickType",clickType);
        extraMap.put("clickBody",clickBody);
        extraMap.put("distVal",distVal);
        String extra = JSONObject.fromObject(extraMap).toString();
        if(clientType.equals("0")){
            //IOS 以标签方式发送
            JPushClientHandler.sendMessageWithTags(title,context,extra,msg.getClientId());
        }else{
            // 安卓以别名方式发送
            JPushClientHandler.sendMessageWithAlias(title,context,extra,msg.getClientId());
        }

    }

	public void push2MQTT(MpCustMsg msg) {
		new JmsPush(msg, service).run();
	}
	
	@SuppressWarnings("rawtypes")
	private MpCustMsg parseMapToMpCustMsg(Map map) {
		MpCustMsg mpCustMsg = new MpCustMsg();
		mpCustMsg.setMsgId(this.ObjectToString(map.get("MSG_ID"))); // 消息ID
		mpCustMsg.setTaskId(this.ObjectToString(map.get("TASK_ID"))); // 任务ID
		mpCustMsg.setChanType(this.ObjectToString(map.get("CHAN_TYPE"))); // 通道类型，数据字典：0-手机银行，2-微信
		mpCustMsg.setPushType(this.ObjectToString(map.get("PUSH_TYPE"))); // 推送类型，数据字典：0-消息，1-扩展
		mpCustMsg.setClientAppId(this.ObjectToString(map.get("CLIENT_APP_ID"))); // 客户端应用标识
		mpCustMsg.setClientType(this.ObjectToString(map.get("CLIENT_TYPE"))); // 终端类型，数据字典：0-iOS，1-Android，2-WP
		mpCustMsg.setDeviceId(this.ObjectToString(map.get("DEVICE_ID"))); // 设备标识
		mpCustMsg.setClientId(this.ObjectToString(map.get("CLIENT_ID"))); // 终端标识(令牌)
		mpCustMsg.setCustId(this.ObjectToString(map.get("CUST_ID"))); // 签约客户号
		mpCustMsg.setMsgTitle(this.ObjectToString(map.get("MSG_TITLE"))); // 消息标题
		mpCustMsg.setMsgBody(this.ObjectToString(map.get("MSG_BODY"))); // 消息内容
		mpCustMsg.setExtBody(this.ObjectToString(map.get("EXT_BODY"))); // 扩展内容
		mpCustMsg.setDistVal((BigDecimal) map.get("DIST_VAL")); // 干扰值
		mpCustMsg.setPrioVal((BigDecimal) map.get("PRIO_VAL")); // 优先级
		mpCustMsg.setClickActType(this.ObjectToString(map.get("CLICK_ACT_TYPE"))); // 点击通知动作类型，0-打开应用，1-打开网址，2-自定义
		mpCustMsg.setClickActBody(this.ObjectToString(map.get("CLICK_ACT_BODY"))); // 点击通知动作内容
		mpCustMsg.setSendBeginTime((Date) map.get("SEND_BEGIN_TIME")); // 发送开始时间
		mpCustMsg.setSendEndTime((Date) map.get("SEND_END_TIME")); // 发送结束时间
		mpCustMsg.setSendTime((Date) map.get("SEND_TIME")); // 发送时间
		mpCustMsg.setSendTimes((BigDecimal) map.get("SEND_TIMES")); // 发送次数
		mpCustMsg.setTakeTime((BigDecimal) map.get("TAKE_TIME")); // 耗时(毫秒)
		mpCustMsg.setStatus(this.ObjectToString(map.get("STATUS"))); // 状态，0-未发送，1-发送中，2-发送成功，3-发送失败
		mpCustMsg.setDelFlag(this.ObjectToString(map.get("DEL_FLAG"))); // 删除标记，0-未删除，1-已删除
		mpCustMsg.setCreateTime((Date) map.get("CREATE_TIME")); // 创建时间
		mpCustMsg.setUpdateTime((Date) map.get("UPDATE_TIME")); // 修改时间
		mpCustMsg.setErrMsg(this.ObjectToString(map.get("ERR_MSG"))); // 错误信息
		return mpCustMsg;
	}

	/**
	 * Object转字符串
	 */
	private String ObjectToString(Object object){
		return object == null ? null : object.toString();
	}
}
 