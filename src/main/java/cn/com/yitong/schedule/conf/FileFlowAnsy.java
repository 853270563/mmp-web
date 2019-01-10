package cn.com.yitong.schedule.conf;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.core.AresApp;

@Component
public class FileFlowAnsy {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${FLOW_ROOT_PATH}")
	private String rootpath;
	private Map<String, Long> cache = new HashMap();
	private boolean running = false;

	public void runAnsyTask() {
		if (running)
			return;
		running = true;
		try {
			// logger.debug("task ansy run..{}", rootpath);
			File rootFile = AresApp.getInstance().getFile(rootpath);
			if (rootFile.exists()) {
				int rootPathLength = rootFile.getPath().length();
				checkDir(rootFile, rootPathLength + 1);
			}
		} finally {
			running = false;
		}
	}

	/**
	 * 检查文件
	 * 
	 * @param file
	 */
	private void checkFile(File file, int subLength) {
		String filePath = file.getPath().substring(subLength);
		// logger.info("check file:{}", filePath);
		long modifed = file.lastModified();
		if (!cache.containsKey(filePath)) {
			cache.put(filePath, file.lastModified());
		} else if (file.isFile()) {
			long last = cache.get(filePath);
			if (last == modifed) {
				return;
			}
			cache.put(filePath, modifed);
			String key = filePath.split("\\.")[0];
			// 删除缓存
			logger.warn("remove flowCaches {}", key);
			AresApp.getInstance().removeFlow(key.replaceAll("\\\\", "/"), rootpath);
		}
		if (file.isDirectory()) {
			checkDir(file, subLength);
			return;
		}
	}

	/**
	 * 检查目录
	 * 
	 * @param parent
	 */
	private void checkDir(File parent, int subLength) {
		if (parent.isFile() || parent.isHidden()) {
			return;
		}
		File[] files = parent.listFiles();
		if (null == files) {
			return;
		}
		for (File file : files) {
			checkFile(file, subLength);
		}
	}
}
