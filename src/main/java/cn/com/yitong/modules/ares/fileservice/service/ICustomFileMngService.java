package cn.com.yitong.modules.ares.fileservice.service;

import cn.com.yitong.util.CustomFileType;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * 
 * @author lc3@yitong.com.cn
 *
 */
public interface ICustomFileMngService {

	/**
	 * 保存文件
	 * @param in
	 * @param fileName
	 * @param fileType
	 * @return
	 * @throws java.io.FileNotFoundException
	 * @throws java.io.IOException
	 */
	String saveFile(InputStream in, CustomFileType fileType, String fileName) throws FileNotFoundException, IOException;

	/**
	 * 生成图片文件名
	 * @param fileName
	 * @return
	 */
	String genFileName(String fileName);

    /**
     * 保存上传文件
     * @param file
     * @return
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    String saveOneMultipartFile(MultipartFile file, CustomFileType fileType) throws IOException;

    /**
	 * 保存图片，支持zip文件
	 * @param file
	 * @param fileType
	 * @return
	 * @throws java.io.FileNotFoundException
	 * @throws java.io.IOException
	 */
	Map<String, String> saveMultipartFile(MultipartFile file, CustomFileType fileType) throws FileNotFoundException, IOException;
	
	/**
	 * 保存流水文件
	 * @param file
	 * @param fileType
	 * @return
	 * @throws java.io.FileNotFoundException
	 * @throws java.io.IOException
	 */
	Map<String, String> saveMultipartFile(MultipartFile file, CustomFileType fileType, String serialNo) throws FileNotFoundException, IOException;

	/**
	 * 根据图片名得到对应的File
	 * @param fileName
	 * @param fileType
	 * @return
	 */
	File getFileByFileName(String fileName, CustomFileType fileType);

	/**
	 * 根据图片名得到共享存储里对应的File
	 * @param fileName
	 * @param fileType
	 * @return
	 */
	File getPlatFileByFileName(String fileName, CustomFileType fileType);

	/**
	 * 保存zip文件
	 * @param in
	 * @param fileName
	 * @param fileType
	 * @return
	 * @throws java.io.FileNotFoundException
	 * @throws java.io.IOException
	 */
	String saveFile(InputStream in, String zipName, CustomFileType fileType, String fileName) throws FileNotFoundException, IOException;

}
