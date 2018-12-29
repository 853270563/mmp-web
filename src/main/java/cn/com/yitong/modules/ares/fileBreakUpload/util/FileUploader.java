package cn.com.yitong.modules.ares.fileBreakUpload.util;

import cn.com.yitong.framework.util.io.ZipUtils;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class FileUploader {

	private Logger logger = Logger.getLogger(this.getClass());
	//临时文件详细地址
	private String tempFileName;
	//上传完成后的文件地址
	private String destFileName;
	//字节指针位置
	private long nodePosition;
	
	// ----------------------------------------------------- Constructors
	public FileUploader(String fileMd5){
		this(fileMd5, fileMd5);
	}

	public FileUploader(String fileMd5, String fileName){
		this.tempFileName = fileMd5 + ".tem";
		this.destFileName = fileName + ".zip";
	}
	// ----------------------------------------------------- public metheds
	public long getNodePosition(){
		if(nodePosition == 0){//初次调用读取字节数时
			if(tempFileExist()){
				initFileNodePosition();
			}
		}
		return nodePosition;
	}

	public String getDestFileName(){
		return destFileName;
	}

	/**
	 * 保存输入流至临时文件
	 * @author hry@yitong.com.cn
	 * @date 2015年4月27日
	 */
	public void saveFile(InputStream inStream){
		RandomAccessFile randomAccessFile = null;
		try {
			tempFileName = tempFileName.replaceAll("\\\\", "/");
			String dir = tempFileName.substring(0, tempFileName.lastIndexOf("/"));
			File directory = new File(dir);
			if (!directory.exists()) {
				directory.mkdirs();
			}
			randomAccessFile = new RandomAccessFile(tempFileName, "rw");
			int length = -1;
			byte[] buffer = new byte[inStream.available()];
			while ((length = inStream.read(buffer)) != -1) {  
	            randomAccessFile.seek(getNodePosition());
				randomAccessFile.write(buffer);
	            nodePosition += length;
	        }  
		} catch (IOException e) {
			logger.error("写文件出现异常，请检查配置是否正确："+e);
		}finally{
			FileUploaderUtil.closeResources(randomAccessFile);
			FileUploaderUtil.closeResources(inStream);
		}
	}

	/**
	 * 临时文件是否存在
	 */
	private boolean tempFileExist() {
		File file = new File(tempFileName);
		return file.exists();
	}

	/**
	 * 删除临时文件
	 */
	public boolean moveFileToUpLoaded()throws Exception {
		return moveFileToUpLoaded(tempFileName, destFileName);
	}

	/**
	 * 读取临时文件信息已经保存的字节数
	 */
	private void initFileNodePosition(){
		RandomAccessFile randomAccessFile = null;
        try {
        	randomAccessFile = new RandomAccessFile(tempFileName, "r");
        	nodePosition = randomAccessFile.length();
		} catch (IOException e) {
			logger.error("读临时文件出现异常，请检查配置是否正确："+e);
		}finally{
			FileUploaderUtil.closeResources(randomAccessFile);
		}
	}
	
	/**
	 * @param tempFileName 临时文件名
	 * @param destFileName 目标文件名
	 */
	private boolean moveFileToUpLoaded(final String tempFileName, final String destFileName) throws Exception {
		File tempFile = new File(tempFileName);
		if(!tempFile.exists()) {
			throw new Exception("临时文件" + tempFile + "不存在");
		}
		if(tempFile.length() <= 0) {
			throw new Exception("临时文件" + tempFileName + "大小为0");
		}
		//使用 重命名方式
		tempFile.renameTo(new File(destFileName));
		logger.info("文件上传完成========》" + tempFileName);
		try {
			FileUtils.deleteQuietly(tempFile);
		} catch (Exception e) {
			logger.info("删除临时文件出现异常，请检查配置是否正确："+e);
		}
		//使用复制文件方式
//		try {
//			FileUtils.copyFile(tempFile, new File(destFileName));
//			FileUtils.deleteQuietly(tempFile);
//			logger.info("文件上传完成========》" + tempFileName);
//		} catch (IOException e) {
//			logger.info("删除临时文件出现异常，请检查配置是否正确："+e);
//			return false;
//		}
		return true;
	}

	/**
	 * 把已经上传好的zip文件解压到指定的目录下 解压完成后可以删除zip文件
	 * @param transPath 业务目录
	 * @return
	 */
	public boolean moveFile2TransPath(String transPath, boolean isDelZip) {
		File zipFile = new File(this.destFileName);
		if(!zipFile.exists()) {
			return false;
		}
		File transFileDir = new File(transPath);
		if(!transFileDir.exists()) {
			transFileDir.mkdirs();
		}
		try {
			ZipUtils.unzip(transPath, this.destFileName);
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if(isDelZip) {
			zipFile.deleteOnExit();
		}
		return true;
	}
}
