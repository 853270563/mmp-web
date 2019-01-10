package cn.com.yitong.ares.mp.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.yitong.framework.service.ICrudService;

/**
 * 消息任务维护
 * 1, 将所有客户消息已经发送成功的消息任务状态设为已发送（'5'）
 * 2, 将所有超过发送截止时间仍未发送成功的客户消息状态设为发送失败('3')，其关联的消息任务，状态设为已发送（'5'）
 * @author 孙伟 (sunw@yitong.com.cn)
 *
 */
@Service
public class ClearMsgTaskService {
	
	public static final Logger L = LoggerFactory.getLogger(ClearMsgTaskService.class);
	@Autowired
	ICrudService service;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Transactional
	public void scanAndClear() {
		// 查询所有发送期间包括当前时间并且状态是'4'(发送中)的消息任务
		List<Map> taskIdList = service.findList("MP_MSG_PUSH.selectAllNowSendMsgTask", null);
		if (taskIdList != null && !taskIdList.isEmpty()) {
			for (Map map : taskIdList) {
				String taskId = (String) map.get("TASK_ID");
				Map params = new HashMap();
				params.put("TASK_ID", taskId);
				List<Map> custMsgStatusList = service.findList("MP_MSG_PUSH.selectAllCustMsgStatusByTaskId", params);
				if (custMsgStatusList == null || custMsgStatusList.isEmpty() || custMsgStatusList.size() > 1) {
					continue;
				}
				// 消息任务客户状态
				boolean flag = false;
				List<Map> taskCustStatusList = service.findList("MP_MSG_PUSH.selectTaskCustStatus", params);
				if (taskCustStatusList!=null && !taskCustStatusList.isEmpty()) {
					for (Map map2 : taskCustStatusList) {
						if ("0".equals(map2.get("STATUS"))) {
							flag = true;
							break;
						}
					}
					if (flag) {
						continue;
					}
				}
				// 所欲客户消息全部发送成功，将该消息任务状态设为'5'(已发送)
				if (custMsgStatusList.size() == 1 && "2".equals(custMsgStatusList.get(0).get("STATUS").toString())) {
					params.put("STATUS", "5");
					service.update("MP_MSG_PUSH.updateMsgTaskStatus", params);
				}
			}
		}
		// 查询所有已过期，但是没有成功发送的客户消息
		List<Map> unSendSucMsgTaskList = service.findList("MP_MSG_PUSH.selectAllUnSendSucMsgTask", null);
		if (unSendSucMsgTaskList != null && !unSendSucMsgTaskList.isEmpty()) {
			for (Map map : unSendSucMsgTaskList) {
				String taskId = (String)map.get("TASK_ID");
				Map params = new HashMap();
				// 将所有未发送的客户消息状态更新为发送失败
				params.put("TASK_ID", taskId);
				params.put("STATUS", "5");
				service.update("MP_MSG_PUSH.updateMsgTaskStatus", params);
			}
		}
	}
}
