package org.apache.log4j;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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

	@Override
	void rollOver() throws IOException {
		super.rollOver();

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
}
