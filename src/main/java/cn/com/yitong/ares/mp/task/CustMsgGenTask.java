package cn.com.yitong.ares.mp.task;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import cn.com.yitong.ares.mp.entity.MpCustMsg;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.util.DateUtil;

/**
* 客户消息生成定时器
* @author 孙伟(sunw@yitong.com.cn)
*/
//@Component
public class CustMsgGenTask {
	@Autowired
	ICrudService service;

	protected final Logger logger = LoggerFactory.getLogger(CustMsgGenTask.class);
	
	/**
	 * 根据消息任务，消息任务客户，生成客户消息
	 */
	@SuppressWarnings({"unchecked" })
	@Transactional
	//@Scheduled(fixedRate = 5000)
	public void genCustMsg(){
        try {
            List<Map<String, Object>> custMsgList = service.findList("MP_CUST_MSG.genCustMsg", null);
            if (custMsgList == null || custMsgList.isEmpty()) {
                return;
            }
            logger.info("生成" + custMsgList.size() + "条客户消息！");
            // 点击消息动作：自定义
            List<Map<String, Object>> mpMsgClickActList = service.findList("MP_CUST_MSG.selectMpMsgClickActList", null);
            Map<String, String> actValMap = new HashMap<String, String>();
            if (mpMsgClickActList != null && !mpMsgClickActList.isEmpty()) {
                for (Map<String, Object> mpMsgClickAct : mpMsgClickActList) {
                    actValMap.put((String) mpMsgClickAct.get("ACT_ID"), (String) mpMsgClickAct.get("ACT_VAL"));
                }
            }
            Set<String> taskIdList = new HashSet<String>();
            long before = System.currentTimeMillis();
            for (Map<String, Object> map : custMsgList) {
                // 将消息任务客户状态设为已处理
                Map<String, Object> mpMsgTaskCust = new HashMap<String, Object>();
                mpMsgTaskCust.put("TASK_CUST_ID", map.get("TASK_CUST_ID"));
                mpMsgTaskCust.put("UPDATE_TIME", new Date());
                service.update("MP_CUST_MSG.updateStatus", mpMsgTaskCust);

                String taskID = (String) map.get("TASK_ID");
                if (!taskIdList.contains(taskID)) {
                    taskIdList.add(taskID);
                }
                // 生成客户消息
                this.createMpCustMsg(map, actValMap);
                map.put("MSG_ID", genId());
                service.insert("MP_CUST_MSG.insert", map);
            }
            for (String taskID : taskIdList) {
                // 将任务状态设为已处理
                Map<String, Object> mpMsgTaskMap = new HashMap<String, Object>();
                mpMsgTaskMap.put("TASK_ID", taskID);
                mpMsgTaskMap.put("UPDATE_TIME", new Date());
                service.update("MP_CUST_MSG.updateIsDeal", mpMsgTaskMap);
            }
            long after = System.currentTimeMillis();
            logger.info("生成" + custMsgList.size() + "条客户消息，耗时" + (after - before) * 1.0 / 1000 + "秒。");
        }catch (Exception e){
            e.printStackTrace();
            logger.error("----生成客户消息异常------");
        }

	}
	
	public String genId() {
		return DateUtil.format(new Date(), "yyyyMMdd") + service.load("MP_CUST_MSG.loadSeq", null).get("MSG_ID").toString();
	}

	/**
	 * 生成单条客户消息
	 * @param map
	 * @return
	 */
	private void createMpCustMsg(Map<String, Object> map, Map<String, String> actValMap) {
		MpCustMsg mpCustMsg = new MpCustMsg();
		map.put("MSG_BODY", map.get("MSG_BODY_TASK"));// 消息内容
		// 点击通知动作类型，0-打开应用，1-打开网址，2-自定义
		String clickActType = this.ObjectToString(map.get("CLICK_ACT_TYPE"));
		String clickActBody = this.ObjectToString(map.get("CLICK_ACT_BODY"));
		mpCustMsg.setClickActType(clickActType);
		if ("1".equals(clickActType) || "3".equals(clickActType)) {
			// 点击通知动作内容
			map.put("CLICK_ACT_BODY", clickActBody);
		} else if ("2".equals(clickActType)){
			map.put("CLICK_ACT_BODY", actValMap.get(clickActBody));
		}
		map.put("SEND_TIMES", 0);// 发送次数
		map.put("STATUS", 0);// 状态，0-未发送
		map.put("DEL_FLAG", 0);// 删除标记，0-未删除
		map.put("CREATE_TIME", new Date());// 创建时间
		map.put("UPDATE_TIME", new Date());// 修改时间
	}
	
	/**
	 * Object转字符串
	 */
	private String ObjectToString(Object object){
		return object == null ? null : object.toString();
		
	}
}
