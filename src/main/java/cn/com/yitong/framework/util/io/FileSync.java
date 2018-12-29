package cn.com.yitong.framework.util.io;

import java.io.File;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.Assert;

/**
 * 文件目录同步工具类 Created by wenin819@gmail.com on 2014-05-26.
 */
public class FileSync {

	/**
	 * 包含的文件列表，支持正则
	 */
	private List<String> includeFiles;

	/**
	 * 排除的文件列表，支持正则
	 */
	private List<String> excludeFiles;

	public FileSync(List<String> includeFiles, List<String> excludeFiles) {
		this.includeFiles = includeFiles;
		this.excludeFiles = excludeFiles;
	}

	public FileSync(String[] includeFiles, String[] excludeFiles) {
		if(null != excludeFiles) {
			this.excludeFiles = Arrays.asList(excludeFiles);
		}
		if(null != includeFiles) {
			this.includeFiles = Arrays.asList(includeFiles);
		}
	}

	public FileSync() {

	}

	public Map<String, FileAction> genFileActionList(String sourceDir,
			String targetDir) {
		return genFileActionList(new File(sourceDir), new File(targetDir));
	}

	public Map<String, FileAction> genFileActionList(File sourceDir,
			File targetDir) {
		Map<String, FileAction> result = new LinkedHashMap<String, FileAction>();
		Assert.isTrue(sourceDir.isDirectory(), "源目录不为目录");
		Assert.isTrue(targetDir.isDirectory(), "目标目录不为目录");
		Assert.isTrue(sourceDir.exists(), "源目录不存在");
		Assert.isTrue(targetDir.exists(), "目标目录不存在");

		Deque<File[]> stack = new LinkedList<File[]>();
		File[] files = new File[] { sourceDir, targetDir };

		stack.offer(files);

		int targetBasePath = targetDir.getAbsolutePath().length();
		int srcBasePath = sourceDir.getAbsolutePath().length();

		File sourceFile = null;
		File targetFile = null;
		FileAction tmpAction = null;
		String path = null;
		boolean isFirst = true;
		while (null != (files = stack.pollFirst())) {
			sourceFile = files[0];
			targetFile = files[1];
			path = sourceFile.getAbsolutePath().substring(srcBasePath);
			if (!isFirst) {
				if (sourceFile.exists()) {
					if (targetFile.exists()) {
						tmpAction = FileAction.S;
					} else {
						tmpAction = FileAction.D;
					}
				} else {
					if (targetFile.exists()) {
						tmpAction = FileAction.C;
					} else {
						tmpAction = FileAction.S;
					}
				}
				result.put(path, tmpAction);
				isFirst = false;
			}
			// 添加正则过滤
			File[] srcFiles = sourceFile.listFiles(new RegexListFilter(sourceFile,
					includeFiles, excludeFiles));
			File[] targetFiles = targetFile.listFiles(new RegexListFilter(targetFile,
					includeFiles, excludeFiles));

			Set<File> hasFile = new HashSet<File>();
			for (File srcFile : srcFiles) {
				path = srcFile.getAbsolutePath().substring(srcBasePath);
				targetFile = findFile(targetFiles, srcFile);
				if (null == targetFile) {
					result.put(path, FileAction.D);
				} else {
					hasFile.add(targetFile);
					if (srcFile.isFile()) {
						FileAction action = FileUtils.genFileAction(srcFile,
								targetFile);
						if (null != action && action != FileAction.S) {
							result.put(path, action);
						}
					} else {
						stack.add(new File[] { srcFile, targetFile });
					}
				}
			}
			for (File tFile : targetFiles) {
				if (!hasFile.contains(tFile)) {
					if (tFile.isDirectory()) {
						Deque<File> createStack = new LinkedList<File>();
						createStack.add(tFile);
						while (null != (targetFile = createStack.pollFirst())) {
							path = targetFile.getAbsolutePath().substring(
									targetBasePath);
							result.put(path, FileAction.C);
							for (File file : targetFile.listFiles()) {
								if (file.isFile()) {
									path = file.getAbsolutePath().substring(
											targetBasePath);
									result.put(path, FileAction.C);
								} else {
									createStack.add(file);
								}
							}
						}
					} else {
						path = tFile.getAbsolutePath()
								.substring(targetBasePath);
						result.put(path, FileAction.C);
					}
				}
			}
		}

		return result;
	}

	private File findFile(File[] files, File f) {
		if (null == files || null == f) {
			return null;
		}
		String fileName = f.getName();
		boolean isFile = f.isFile();
		for (File file : files) {
			if (fileName.equals(file.getName()) && isFile == file.isFile()) {
				return file;
			}
		}
		return null;
	}

	public static void main(String[] args) {
		String srcPath = "d://最大熵";
		String destPath = "d://最大熵1.1";
		long s = System.currentTimeMillis();
		Map<String, FileAction> fileActionMap = new FileSync(new String[]{".*\\.txt", ".*\\.jpg"}, new String[]{"\\\\技术新闻特征 - 副本\\.txt"})
				.genFileActionList(srcPath, destPath);
		long e = System.currentTimeMillis();
		for (Map.Entry<String, FileAction> actionEntry : fileActionMap
				.entrySet()) {
			System.out.println(actionEntry.getValue().getTitle() + ":"
					+ actionEntry.getKey());
		}
		System.out.println(e - s);
	}

}
