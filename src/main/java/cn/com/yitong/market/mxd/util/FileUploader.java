package cn.com.yitong.market.mxd.util;

import cn.com.yitong.consts.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * 文件上传管理类
 *
 * @author hry@yitong.com.cn
 * @date 2015年4月27日
 */
public class FileUploader {
	private Logger logger = Logger.getLogger(this.getClass());
	//临时文件详细地址
	private String tempFileName;
	//上传完成后的文件地址
	private String destFileName;
	//字节指针位置
	private long nodePosition;
	//临时文件目录

	private static final String TEMP_PATH= Properties.getString("XD_TEMP_PATH");//
	//上传完成后的文件目录

	public static final String SAVE_PATH= Properties.getString("XD_SAVE_PATH");//

	// ----------------------------------------------------- Constructors
	public FileUploader(String fileMd5){
		this(fileMd5,fileMd5+".zip");
	}
	public FileUploader(String fileMd5,String fileName){
		this.tempFileName=TEMP_PATH+"/"+fileMd5+".tem";
		this.destFileName=SAVE_PATH+"/"+fileName;
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
			File tempDir=new File(TEMP_PATH);
			if(!tempDir.exists()){
				tempDir.mkdirs();
				logger.info("创建断点续传临时目录成功:" + tempDir.getAbsolutePath());
			}
			randomAccessFile = new RandomAccessFile(tempFileName,"rw");
			int length = -1;
			byte[] buffer = new byte[inStream.available()];
			while ((length = inStream.read(buffer)) != -1) {
	            randomAccessFile.seek(getNodePosition());
				randomAccessFile.write(buffer);
	            nodePosition += length;
	        }
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("写文件出现异常，请检查配置是否正确："+e.getMessage());
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
	 * @throws Exception
	 */
	public boolean moveFileToUpLoaded() throws Exception {
		return moveFileToUpLoaded(tempFileName,destFileName);
	}
	// ----------------------------------------------------- private metheds
	/**
	 * 读取临时文件信息已经保存的字节数
	 */
	private void initFileNodePosition(){
		RandomAccessFile randomAccessFile = null;
        try {
        	randomAccessFile = new RandomAccessFile(tempFileName,"r");
        	nodePosition = randomAccessFile.length();
		} catch (IOException e) {
			logger.error("读临时文件出现异常，请检查配置是否正确："+e.getMessage());
		}finally{
			FileUploaderUtil.closeResources(randomAccessFile);
		}
	}

	/**
	 * 上传完成时删除临时文件
	 * @param tempFileName 临时文件名
	 * @param destFileName 目标文件名
	 * @author hry@yitong.com.cn
	 * @throws Exception
	 * @date 2015年4月28日
	 */
	private boolean moveFileToUpLoaded(final String tempFileName, final String destFileName) throws Exception {
		if(StringUtils.isBlank(tempFileName) || StringUtils.isBlank(destFileName)) {
			throw new Exception("移动文件异常");
		}
		File tempFile = new File(tempFileName);//临时文件
		File destFile = new File(destFileName);//保存文件
		if(!tempFile.exists()) {
			throw new Exception("文件[" + tempFileName + "]不存在");
		}
		if(tempFile.length() <= 0) {
			throw new Exception("文件[" + tempFileName + "]大小为0");
		}

		if(destFile.exists()) {//如果最终文件已存在
			if(destFile.length() == tempFile.length()) {//且文件大小一致
				tempFile.delete();//删除临时文件
				return false;
			}
			destFile.delete();//删除最终文件
		}
		String msgCopy = "移动文件[" + tempFile.getAbsolutePath() + "]->[" + destFile.getAbsolutePath() + "]";
		try {
			FileUtils.moveFile(tempFile, destFile);//移动文件
			logger.debug(msgCopy + "成功");
		} catch (Exception e) {
			throw new Exception(msgCopy + "异常", e);
		}
		return true;
	}
}
