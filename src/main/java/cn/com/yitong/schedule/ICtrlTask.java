package cn.com.yitong.schedule;

/*
 * 控制器配置加载的任务接口
 * @author yaoym
 *
 */
public interface ICtrlTask {

	/**
	 * Scheduled(fixedRate = 1000*60*1) <br>
	 * 启动时执行一次，每隔1分钟执行一次 <br>
	 * <br>
	 * Scheduled(cron = "0 0 2 * * *") cron表达式：* * * * * *（共6位，使用空格隔开，具体如下） <br>
	 * cron表达式：*(秒0-59) *(分钟0-59) *(小时0-23) *(日期1-31) *(月份1-12或是JAN-DEC)
	 * *(星期1-7或是SUN-SAT)
	 */
	public void load();
}
