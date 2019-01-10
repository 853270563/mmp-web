package cn.com.yitong.schedule.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.dao.SqlSessionCache;
import cn.com.yitong.schedule.conf.FileDisgramAnsy;
import cn.com.yitong.schedule.conf.FileFlowAnsy;

@Component("fileMonitor")
public class FileMonitor {

	private boolean running = false;

	@Autowired
	private FileDisgramAnsy disgramAnsy;// 报文配置文件监听
	@Autowired
	private FileFlowAnsy flowAnsy;// 流程编排文件监听

	@Autowired
	@Qualifier("sqlSessionCache")
	private SqlSessionCache sqlSessionCache;


	/**
	 * 每120秒执行一次
	 */
	//@Scheduled(fixedRate = 1000 * 120)
	public void runAnsyTask() {
		if (running)
			return;
		running = true;
		try {
			// 报文定义同步
			disgramAnsy.runAnsyTask();
			// 服务编排同步
			flowAnsy.runAnsyTask();
			// 数据库语句更新
			sqlSessionCache.refreshMapper();
		} finally {
			running = false;
		}
	}

}