package cn.com.yitong.framework.util.io;

import org.springframework.util.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 文件操作工具类 Created by wenin819@gmail.com on 2014-05-26.
 */
public class FileUtils {

	/**
	 * 比较两文件是否相同
	 * 
	 * @param source
	 *            源文件
	 * @param target
	 *            目标文件
	 * @return
	 */
	public static FileAction genFileAction(File source, File target) {
		FileAction result = FileAction.U;
		if(null == source || !source.exists()) {
			if(null == target || !target.exists()) {
				return FileAction.S;
			} else {
				return FileAction.C;
			}
		} else if(null == target || !target.exists()) {
			return FileAction.D;
		}
		Assert.isTrue(source.isFile(), "源文件不为文件");
		Assert.isTrue(target.isFile(), "目标文件不为文件");
		if (source.length() == target.length()) {
			String sourceMD5 = FileUtils.getFileMD5(source);
			String targetMD5 = FileUtils.getFileMD5(target);
			result = targetMD5.equals(sourceMD5) ? FileAction.S : FileAction.U;
		}
		return result;
	}

	/**
	 * @param sourceDir
	 *            源目录
	 * @param targetDir
	 *            目标目录
	 * @return
	 */
	public static boolean isDidd(File sourceDir, File targetDir) {
		boolean result = false;
		if (sourceDir != null && targetDir != null
				&& getDirSize(targetDir) == getDirSize(sourceDir)) {
			List<String> sourceDirMD5 = FileUtils.getDirMD5(sourceDir);
			List<String> targetDirMD5 = FileUtils.getDirMD5(targetDir);
			if (sourceDirMD5 != null && targetDirMD5 != null) {
				result = compareToDirList(sourceDirMD5, targetDirMD5);
			}
		}
		return result;
	}

	/**
	 * @param sourceDirMD5
	 *            源列表
	 * @param targetDirMD5
	 *            目标列表
	 * @return
	 */
	private static boolean compareToDirList(List<String> sourceDirMD5,
			List<String> targetDirMD5) {
		boolean result = false;
		if (sourceDirMD5.size() == targetDirMD5.size()
				&& targetDirMD5.size() > 0) {
			result = true;
			Object[] sourceDirMD5s = sourceDirMD5.toArray();
			Object[] targetDirMD5s = targetDirMD5.toArray();
			Arrays.sort(sourceDirMD5s);
			Arrays.sort(targetDirMD5s);
			for (int i = 0; i < sourceDirMD5s.length; i++) {
				if (!targetDirMD5s[i].equals(sourceDirMD5s[i])) {
					result = false;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * @param file
	 *            文件目录
	 * @return
	 */
	private static List<String> getDirMD5(File file) {
		if (!file.isDirectory()) {
			return null;
		}
		List<String> list = new ArrayList<String>();
		File files[] = file.listFiles();
		for (File f : files) {
			String md5 = "";
			if (f.isDirectory()) {
				list.addAll(getDirMD5(f));
			} else {
				md5 = getFileMD5(f);
				list.add(md5);
			}
		}
		return list;
	}

	/**
	 * @param file
	 *            输入文件
	 * @return
	 */
	public static String getFileMD5(File file) {
		if (!file.isFile()) {
			return null;
		}
		String md5Rresult = "";
		MessageDigest messageDigest = null;
		FileInputStream fin = null;
		BigInteger bigInteger = null;
		byte buffer[] = new byte[1024];
		int len = 0;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			fin = new FileInputStream(file);
			while ((len = fin.read(buffer, 0, 1024)) != -1) {
				messageDigest.update(buffer, 0, len);
			}
			bigInteger = new BigInteger(1, messageDigest.digest());
			md5Rresult = bigInteger.toString(16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fin != null) {
				FileUtils.closeFileInputStream(fin);
			}
		}
		return md5Rresult;
	}

	/**
	 * @param fin
	 *            关闭文件资源
	 */
	public static void closeFileInputStream(FileInputStream fin) {
		if (fin != null) {
			try {
				fin.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				fin = null;
			}
		}
	}

	/**
	 * 获取文件夹中文件的MD5值
	 * 
	 * @param file
	 * @return
	 */
	public static Map<String, String> getFileDirMD5(File file, String str) {
		if (!file.isDirectory()) {
			return null;
		}
		// <filepath,md5>
		Map<String, String> map = new LinkedHashMap<String, String>();
		String md5;
		File files[] = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory()) {
				map.put(f.getAbsolutePath().replace(str + File.separator, ""), "0");
				map.putAll(getFileDirMD5(f, str));
			} else {
				md5 = getFileMD5(f);
				if (md5 != null) {
					map.put(f.getAbsolutePath().replace(str + File.separator, ""), md5);
				}
			}
		}
		return map;
	}

	public static void main(String[] args) {
		String srcPath = "F:\\shyt\\shyt\\workspaces_1\\mmp";
		String destPath = "F:\\shyt\\mmp\\mmp";
		File srcDir = new File(srcPath);
		File destDir = new File(destPath);
		// 首先比较两个目录是不是一致，一致则不动，不一致则有三种情况(可能同时出现的三种情况)。1：增加了哪些。2：变更了哪些。3：删除了哪些
		boolean isIdentify = false;
		isIdentify = isDidd(srcDir, destDir);
		if (true == isIdentify) {
			System.out.println("两个目录文件是一致的");
		} else {
			long l1 = System.currentTimeMillis();
			//获得新增、删除和不一致的
			List<List<String>> fileList = getIsIdentifyFiles(srcDir, destDir);
			for (String s : fileList.get(0)) {
				System.out.println("新增:" + s);
			}
			for (String s : fileList.get(1)) {
				System.out.println("删除:" + s);
			}
			for (String s : fileList.get(2)) {
				System.out.println("更新:" + s);
			}
			long l2 = System.currentTimeMillis();
			System.out.println(l2 - l1);
			//把新增和不一致的打个增量包(删除的暂时不打)
			
		}
	}

	/**
	 * @param srcDir
	 *            源目录
	 * @param destDir
	 *            目标目录
	 * @return 不一致文件列表
	 */
	private static List<List<String>> getIsIdentifyFiles(File srcDir,
			File destDir) {
		List<List<String>> fileList = new ArrayList<List<String>>();
		// 增加文件列表
		List<String> addFiles = new ArrayList<String>();
		// 删除文件列表
		List<String> deleteFiles = new ArrayList<String>();
		// 变更文件列表
		List<String> modifyFiles = new ArrayList<String>();
		String srcAbsolutePath = srcDir.getAbsolutePath();
		String destAbsolutePath = destDir.getAbsolutePath();
		Map<String, String> srcMap = getFileDirMD5(srcDir, srcAbsolutePath);
		Map<String, String> destMap = getFileDirMD5(destDir, destAbsolutePath);
		// 增加的文件
		for (String destKey : destMap.keySet()) {
			if (!srcMap.containsKey(destKey)) {
				addFiles.add(destAbsolutePath + "\\" + destKey);
			}
		}
		// 删除的文件
		for (String srcKey : srcMap.keySet()) {
			if (!destMap.containsKey(srcKey)) {
				deleteFiles.add(srcAbsolutePath + "\\" + srcKey);
			}
		}
		// 变更的文件
		for (String destKey : destMap.keySet()) {
			for (String srcKey : srcMap.keySet()) {
				if (destKey.equals(srcKey)
						&& !destMap.get(destKey).equals(srcMap.get(srcKey))) {
					modifyFiles.add(destAbsolutePath + "\\" + destKey);
				}
			}
		}
		fileList.add(addFiles);
		fileList.add(deleteFiles);
		fileList.add(modifyFiles);
		return fileList;
	}

	/**
	 * @param file
	 * @return 目录文件的大小
	 */
	public static long getDirSize(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] childrenFiles = file.listFiles();
				long fileSize = 0;
				for (File f : childrenFiles) {
					fileSize += getDirSize(f);
				}
				return fileSize;
			} else {
				long size = file.length();
				return size;
			}
		} else {
			return 0;
		}
	}
}