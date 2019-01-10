package cn.com.yitong.portal.note.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import cn.com.yitong.ares.mp.controller.AresMpMsgTaskController;
import cn.com.yitong.framework.service.ICrudService;

/**
* 资讯公告发布
* @author 孙伟(sunw@yitong.com.cn)
*/
//@Component
public class AresNotePushTask {
	@Autowired
	ICrudService service;
	
	@Autowired
	private AresMpMsgTaskController aresMpMsgTaskController;
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	protected final Logger logger = LoggerFactory.getLogger(AresNotePushTask.class);
	
	/**
	 * 发布资讯公告
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	@Scheduled(fixedRate = 5000)
	public void aresNotePush(){
		List<Map<String, Object>> noteMapList = service.findList("ARES_NOTE.selectUnPushNote", null);
		
		if (noteMapList != null && !noteMapList.isEmpty()) {
			logger.info("准备推送" + noteMapList.size() + "条资讯公告。");
		}
		
		for (Map<String, Object> noteMap : noteMapList) {
			// 参数设置
			Map<String, Object> param = new HashMap<String, Object>();
			// 手机应用
			param.put("CHANTYPE", "1");
			// 广播
			param.put("SENDTYPE", "0");
			// 消息
			param.put("PUSHTYPE", "0");
			// 客户端应用
			param.put("CLIENTAPPID", noteMap.get("APP_ID"));
			// 接收客户号来源
			param.put("RECCUSTFROM", "0");
			// 消息标题
			param.put("MSGTITLE", noteMap.get("NOTE_TITLE"));
			// 消息内容
			/*String noteContent = (String)noteMap.get("NOTE_CONTENT");
			if (noteContent != null && noteContent.length() > 64) {
				noteContent = noteContent.substring(0, 64) + "。。。";
			}*/
			param.put("MSGBODY", noteMap.get("NOTE_TITLE"));
			// 点击通知动作类型
			param.put("CLICKACTTYPE", "3");
			// 点击通知动作内容
			param.put("CLICKACTBODY", noteMap.get("NOTE_ID"));
			// 推送终端类型
			param.put("PUSHCLIENTTYPE", "9");
			// 干扰值
			param.put("DISTVAL", 0);
			// 优先级
			param.put("PRIOVAL", 0);
			// 操作者
			param.put("OPERID", noteMap.get("OPER_ID"));
			// 发送开始时间
			param.put("SENDBEGINTIME", sdf.format((Date)noteMap.get("SEND_BEGIN_TIME")));
			if (noteMap.get("SEND_END_TIME") != null) {
				// 发送结束时间
				param.put("SENDENDTIME", sdf.format((Date)noteMap.get("SEND_END_TIME")));
			}
			Map<String, String> rst = aresMpMsgTaskController.insertAresMpMsgTask(null, param);
			if ("1".equals(rst.get("STATUS"))) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("NOTE_ID", noteMap.get("NOTE_ID"));
				map.put("UPDATE_TIME", new Date());
				service.update("ARES_NOTE.updateNoteStatus", map);
			}
		}
	}
}
