package cn.com.yitong.ares.mp.task;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;

import cn.com.yitong.ares.mp.service.ClearMsgTaskService;

//@Component
public class ClearMsgTask extends AbstractTask {
	
	@Resource
	private ClearMsgTaskService clearMsgTaskService;
	
	@Override
	@Scheduled(fixedRate =5000, initialDelay = 6000)
	public void execute() {
		clearMsgTaskService.scanAndClear();
		// L.info("ClearMsgTask has executed");
	}
}
