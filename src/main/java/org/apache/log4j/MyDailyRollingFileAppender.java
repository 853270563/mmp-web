package org.apache.log4j;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.log4j.helpers.LogLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义按照每天生成的日志<br>
 * 超出最大的文件数会删除历史日志
 * @author luanyu
 * @date   2017年11月15日
 */
public class MyDailyRollingFileAppender extends DailyRollingFileAppender {
	private static Logger logger = LoggerFactory.getLogger(MyDailyRollingFileAppender.class);
	private int maxFileSize = 20;
	private String scheduledFilename;
	@Override
	void rollOver() throws IOException {
		if (getDatePattern() == null) {
			errorHandler.error("Missing DatePattern option in rollOver().");
			return;
		}
		
		String datedFilename = fileName + sdf.format(now);
		// It is too early to roll over because we are still within the  
		// bounds of the current interval. Rollover will occur once the  
		// next interval is reached.  
		if (scheduledFilename.equals(datedFilename)) {
			return;
		}
		
		// close current file, and rename it to datedFilename  
		this.closeFile();
		
		File target = new File(scheduledFilename);
		if (!target.exists()) {//防止多线程其他线程日志看不到问题
			File file = new File(fileName);
			boolean result = file.renameTo(target);
			if (result) {
				LogLog.debug(fileName + " -> " + scheduledFilename);
			} else {
				LogLog.error("Failed to rename [" + fileName + "] to [" + scheduledFilename + "].");
			}
		}
		
		try {
			// This will also close the file. This is OK since multiple  
			// close operations are safe.  
			this.setFile(fileName, true, this.bufferedIO, this.bufferSize);
		} catch (IOException e) {
			errorHandler.error("setFile(" + fileName + ", false) call failed.");
		}
		scheduledFilename = datedFilename;
		logger.info("保留文件数量" + maxFileSize + "，当天日志存放路径为：" + fileName);
		List<File> fileList = getAllLogs();
		sortFiles(fileList);
		logger.info("所有的日志文件:" + fileList.toString());
		deleteOvermuch(fileList);
	}

	/**
	      * 删除过多的文件
	      * @param fileList 所有日志文件
	      */
	private void deleteOvermuch(List<File> fileList) {
		if (fileList.size() > maxFileSize) {
			for (int i = 0; i < fileList.size() - maxFileSize; i++) {
				fileList.get(i).delete();
				logger.info("删除日志" + fileList.get(i));
			}
		}
	}

	/**
	      * 根据文件名称上的特定格式的时间排序日志文件
	      * @param fileList
	     */
	void sortFiles(List<File> fileList) {
		Collections.sort(fileList, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				try {
					if (getDateStr(o1).isEmpty()) {
						return 1;
					}
					Date date1 = sdf.parse(getDateStr(o1));

					if (getDateStr(o2).isEmpty()) {
						return -1;
					}
					Date date2 = sdf.parse(getDateStr(o2));

					if (date1.getTime() > date2.getTime()) {
						return 1;
					} else if (date1.getTime() < date2.getTime()) {
						return -1;
					}
				} catch (ParseException e) {
					logger.error("", e);
				}
				return 0;
			}
		});
	}

	private String getDateStr(File file) {
		if (file == null) {
			return "null";
		}
		return file.getName().replaceAll(new File(fileName).getName(), "");
	}

	/**
	 *  获取所有日志文件，只有文件名符合DatePattern格式的才为日志文件
	 * @return
	 */
	private List<File> getAllLogs() {
		final File file = new File(fileName);
		File logPath = file.getParentFile();
		if (logPath == null) {
			logPath = new File(".");
		}

		File files[] = logPath.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				try {
					if (getDateStr(pathname).isEmpty()) {
						return true;
					}
					sdf.parse(getDateStr(pathname));
					return true;
				} catch (ParseException e) {
					logger.error("", e);
					return false;
				}
			}
		});
		return Arrays.asList(files);
	}

	public int getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(int maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	@Override
	public void activateOptions() {
		// TODO Auto-generated method stub
		super.activateOptions();
		if (getDatePattern() != null && fileName != null) {
			now.setTime(System.currentTimeMillis());
			sdf = new SimpleDateFormat(getDatePattern());
			int type = computeCheckPeriod();
			printPeriodicity(type);
			rc.setType(type);
			File file = new File(fileName);
			scheduledFilename = fileName + sdf.format(new Date(file.lastModified()));
	
		} else {
			LogLog.error("Either File or DatePattern options are not set for appender [" + name + "].");
		}
	}
}
