package cn.com.yitong.ares.mp.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTask {
	protected static final Logger L = LoggerFactory.getLogger(AbstractTask.class);

	public abstract void execute();
}
