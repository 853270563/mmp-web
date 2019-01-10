package cn.com.yitong.ares.appManage.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import cn.com.yitong.framework.util.io.ZipUtils;
import cn.com.yitong.util.StringUtil;


/**
 * 创建资源升级包
 *
 * @author 孙伟（sunw@yitong.com.cn）
 */
public class CreateUpgradePackService {
	
	/**
	 * zip
	 */
	private static final String zip = "zip";

	/**
	 * 生成资源升级包
	 * @param originalFile 源资源包
	 * @param newFile 新资源包
	 * @return
	 * @throws java.io.IOException
	 */
	public static Map<String, Object> createUpgradePack(File originalFile, File newFile) throws Exception{
		// 文件不存在或者不是zip文件
		if (!originalFile.exists()
				|| !zip.equals(getExtension(originalFile))
				|| !newFile.exists()
				|| !zip.equals(getExtension(newFile))) {
			return null;
		}
		Map<String, Object> rst = new HashMap<String, Object>();
		if (getMd5ByFile(newFile).equals(getMd5ByFile(originalFile))) {
			rst.put("status", "1");
			return rst;
		}
		// 解压资源包
		final File originalZipFile = unZip(originalFile);
		int originalPathLength = originalZipFile.getPath().length();
		// 将源文件的相对路径和文件放入map中
		Map<String, File> originalFilesMap = getFilesMap(originalZipFile, originalPathLength);
		
		final File newZipFile = unZip(newFile);
		int newPathLength = newZipFile.getPath().length();
		// 将新文件的相对路径和文件放入map中
		Map<String, File> newFilesMap = getFilesMap(newZipFile, newPathLength);
		
		// 创建升级包文件夹
		String upgradePackPath = newFile.getPath().substring(0, newFile.getPath().lastIndexOf(".")) + "_upgradepack";
		File upgradePackFilePath = new File(upgradePackPath);
		upgradePackFilePath.mkdir();
		File deleteFilesRecord = new File(upgradePackPath + File.separator + "DelFilesList.txt");
		deleteFilesRecord.createNewFile();
		
		Set<Entry<String, File>> newEntrySet = newFilesMap.entrySet();
		for (Entry<String, File> entry : newEntrySet) {
			String key = entry.getKey();
			// 旧版本存在的 文件
			if (originalFilesMap.containsKey(key)) {
				File oldTempFile = originalFilesMap.get(key);
				File newTempFile = entry.getValue();
				// 是目录的话，继续判断下个文件
				if (newTempFile.isDirectory()) {
					continue;
				}
				String oldTempFileMD5 = getMd5ByFile(oldTempFile);
				String newTempFileMD5 = getMd5ByFile(newTempFile);
				if (StringUtil.isNotEmpty(newTempFileMD5) && newTempFileMD5.equals(oldTempFileMD5)) {
					continue;
				} else {
					File updatedFile = createNewFile(upgradePackPath, key, true);
					copyFile(newTempFile, updatedFile);
				}
			// 旧版本中不存在的文件
			} else {
				File newTempFile = entry.getValue();
				// 是目录的话，创建新目录
				if (newTempFile.isDirectory()) {
					createNewFile(upgradePackPath, key, false);
					continue;
				}
				File updatedFile = createNewFile(upgradePackPath, key, true);
				copyFile(newTempFile, updatedFile);
			}
		}
		// 新版本中不存在的文件，记录到待删文件列表中
		Set<Entry<String, File>> originalEntrySet = originalFilesMap.entrySet();
		StringBuffer stringBuffer = new StringBuffer();
		for (Entry<String, File> entry : originalEntrySet) {
			String key = entry.getKey();
			if (!newFilesMap.containsKey(key)) {
				stringBuffer.append(key + "\n");
			}
		}
		if (!StringUtils.isBlank(stringBuffer.toString())) {
			FileOutputStream fileOutputStream = new FileOutputStream(deleteFilesRecord);
			try {
				fileOutputStream.write(stringBuffer.toString().getBytes());
			} finally {
				IOUtils.closeQuietly(fileOutputStream);
			}
		}
		// 创建并压缩文件
		File upgradePackZipFile = new File(upgradePackPath + ".zip");
		upgradePackZipFile.createNewFile();
		ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(upgradePackZipFile));
		FileInputStream fileInputStream = new FileInputStream(deleteFilesRecord);
		zipOutputStream.putNextEntry(new ZipEntry(deleteFilesRecord.getName()));
		try {
			int len = 0;
			byte[] buf = new byte[1024 * 8];
			while ((len = fileInputStream.read(buf, 0, 1024 * 8)) > 0) {
				zipOutputStream.write(buf, 0, len);
			}
		}catch (Exception e){
			e.printStackTrace();
		} finally {
			fileInputStream.close();
			zipOutputStream.closeEntry();
			zipOutputStream.close();
		}
		
		ZipUtils.zip(upgradePackFilePath, upgradePackZipFile);

		// 删除临时文件
		deleteFile(originalZipFile);
		deleteFile(newZipFile);
		deleteFile(upgradePackFilePath);
		// 升级包大于全量包大小的80%
		if (upgradePackZipFile.length() > newFile.length() * 0.8) {
			deleteFile(upgradePackZipFile);
			rst.put("status", "2");
			return rst;
		}
		rst.put("file", upgradePackZipFile);
		return rst;
	}
	
	/**
	 * 复制文件
	 * @param oldFile
	 * @param newFile
	 * @return
	 * @throws Exception
	 */
	private static boolean copyFile(File oldFile, File newFile) throws Exception{
		byte[] buffer = new byte[2048];
		
		FileInputStream  in = new FileInputStream(oldFile);
		FileOutputStream out = new FileOutputStream(newFile);
		try {
			int len = 0;
			while((len = in.read(buffer, 0, 2048)) != -1){
				out.write(buffer, 0, len);
			}
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
		
		return true;
	}
	
	/**
	 * 创建文件
	 * @param basePath
	 * @param fileName
	 * @throws Exception
	 */
	private static File createNewFile(String basePath, String fileName, boolean isFile) throws Exception{
		String[] pathes = fileName.split("\\\\");
		if (pathes.length == 1) {
			pathes = fileName.split("/");
		}
		int length = pathes.length;
		for (int i = 1; i < length; i ++) {
			basePath += File.separator + pathes[i];
			File file = new File(basePath);
			if (i == length - 1 && isFile) {
				file.createNewFile();
				return file;
			} else if (!file.exists()) {
				file.mkdir();
			}
		}
		return null;
	}
	
	/**
	 * 获取文件扩展名
	 * @param file
	 * @return
	 */
	private static String getExtension(File file){
		String fileName = file.getName();
		if (fileName.indexOf(".") < 0) {
			return null;
		} else {
			return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
		}
	}
	
	/**
	 * 解压文件
	 * @param file
	 * @return
	 */
	private static File unZip(File file){
		String fileName = file.getName().substring(0, file.getName().lastIndexOf("."));
		String filePath = file.getParent();
		File dir = new File(filePath + File.separator + fileName);
		dir.mkdir();
		ZipUtils.unzip(file, dir);
		return dir;
	}
	
	/**
	 * 获取某文件夹下所有文件列表
	 * @param file
	 * @return
	 */
	private static Map<String, File> getFilesMap(File file, int dirLength){
		Map<String, File> filesMap = new HashMap<String, File>();
		if (file == null || !file.exists()) {
			return filesMap;
		}
		if (file.getPath().length() > dirLength) {
			String relativePath = file.getPath().substring(dirLength);
			filesMap.put(relativePath, file);
		}
		if (file.isDirectory()) {
			File[] listFiles = file.listFiles();
			for (File tempFile : listFiles) {
				filesMap.putAll(getFilesMap(tempFile, dirLength));
			}
		}
		return filesMap;
	}
	
	/**
	 * 删除文件或文件夹
	 * @param file
	 * @return
	 */
	private static boolean deleteFile(File file){
		if (file == null || !file.exists()) {
			return false;
		}
		if (file.isFile()) {
			return file.delete();
		} else {
			File[] listFiles = file.listFiles();
			for (File tempFile : listFiles) {
				if(!deleteFile(tempFile)){
					return false;
				}
			}
			return file.delete();
		}
	}
	
	/**
	 * 获取文件MD5值
	 * @param file
	 * @return
	 * @throws Exception
	 */
	private static String getMd5ByFile(File file) throws Exception {
		String value = null;
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] buf = new byte[2048];
			boolean fileIsNull = true;
			int len = -1;
			while ((len = in.read(buf)) > 0) {
				fileIsNull = false;
				md5.update(buf, 0, len);
			}
			if (!fileIsNull) {
				BigInteger bi = new BigInteger(1, md5.digest());
				value = bi.toString(16);
			}
		} catch (FileNotFoundException e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(in);
		}
		return value;
	}

	public static void main(String[] args) throws Exception {

		File file = new File("c:/test3/简历-栾钰.doc");
		File file2 = new File("c:/test3/aha2.txt");
		System.out.println(DigestUtils.md5Hex(new FileInputStream(file)));
		System.out.println(getMd5ByFile(file));
		System.out.println(getMd5ByFile(file2));
	}
}
