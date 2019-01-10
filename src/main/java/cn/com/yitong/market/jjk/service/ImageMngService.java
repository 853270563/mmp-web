package cn.com.yitong.market.jjk.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

/**
 * 
 * @author lc3@yitong.com.cn
 *
 */
public interface ImageMngService {

	/**
	 * 保存图片
	 * @param in
	 * @param filetype
	 * @return
	 * @throws java.io.FileNotFoundException
	 * @throws java.io.IOException
	 */
	String savePhote(InputStream in, String filetype) throws FileNotFoundException, IOException;

	/**
	 * 生成图片文件名
	 * @param filetype
	 * @return
	 */
	String genPhotoFileName(String filetype);

	/**
	 * 保存图片，支持zip文件
	 * @param file
	 * @return
	 * @throws java.io.FileNotFoundException
	 * @throws java.io.IOException
	 */
	Map<String, String> savePhoteMultipartFile(MultipartFile file) throws FileNotFoundException, IOException;

	/**
	 * 根据图片名得到对应的File
	 * @param fileName
	 * @return
	 */
	File getFileByPhotoName(String fileName);

}
