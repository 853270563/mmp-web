package cn.com.yitong.modules.ares.fileservice.service.impl;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.modules.ares.fileservice.service.ICustomFileMngService;
import cn.com.yitong.util.CustomFileType;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 
 * @author lc3@yitong.com.cn
 *
 */
@Service
public class CustomFileMngServiceImpl implements ICustomFileMngService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private String uploadFilesPath = AppConstants.upload_files_path;

	private String platFilesPath = AppConstants.plat_files_path;

	private static final SimpleDateFormat dateFmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	
	/**
	 * 保存上传文件
	 * @param file
	 * @return
	 * @throws java.io.FileNotFoundException
	 * @throws java.io.IOException
	 */
	@Override
	public String saveOneMultipartFile(MultipartFile file, CustomFileType fileType) throws IOException {
        String fileName = file.getOriginalFilename();
        String filetype = getFileTypeByFileName(fileName);

        if(file.getSize() > fileType.getSize()) {
            throw new IllegalArgumentException("文件太大，不允许上传");
        } else if(!fileType.getFileTypeList().contains(filetype)) {
            throw new RuntimeException(MessageFormat.format("文件{0}不为合法文件，请检查", fileName));
        }

        return saveFile(file.getInputStream(), fileType, fileName);
    }

    /**
     * 保存上传文件
     *
     * @param file
     * @return
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    @Override
	public Map<String, String> saveMultipartFile(MultipartFile file, CustomFileType fileType) throws IOException {
		Map<String, String> rtn = new HashMap<String, String>();
		String fileName = file.getOriginalFilename();
		String filetype = getFileTypeByFileName(fileName);
		
		if(file.getSize() > fileType.getSize()) {
			throw new IllegalArgumentException("文件太大，不允许上传");
		}
		if(CustomFileType.ZIP == fileType && CustomFileType.ZIP.getFileTypeList().contains(filetype)) {
			ZipInputStream zin = null;
			try {
				zin = new ZipInputStream(file.getInputStream());
				ZipEntry entity = null;
				while(null != (entity = zin.getNextEntry())) {
					if(entity.isDirectory()) {
						continue;
					}
					fileName = entity.getName();
					filetype = getFileTypeByFileName(fileName);
					fileType = CustomFileType.getType(filetype);
					if(!CustomFileType.IMG.getFileTypeList().contains(filetype)) {
						throw new RuntimeException(MessageFormat.format("文件{0}不为合法文件，请检查", fileName));
					}
					rtn.put(fileName, saveFile(zin, fileType, fileName));
					zin.closeEntry();
				}
				logger.info("上传图片文件信息：===================>上传成功！");
			} finally {
				IOUtils.closeQuietly(zin);
			}
		} else if(!fileType.getFileTypeList().contains(filetype)) {
			throw new RuntimeException(MessageFormat.format("文件{0}不为合法文件，请检查", fileName));
		} else {
			logger.info("上传图片文件信息：===================>非zip上传");
			rtn.put(fileName, saveFile(file.getInputStream(), fileType, fileName));
		}
		return rtn;
	}
    
    /**
     * 保存上传文件
     *
     * @param file
     * @return
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    @Override
	public Map<String, String> saveMultipartFile(MultipartFile file, CustomFileType fileType, String serialNo) throws IOException {
		Map<String, String> rtn = new HashMap<String, String>();
		String fileName = file.getOriginalFilename();
		String extensionName = getFileTypeByFileName(fileName);
		
		if(file.getSize() > fileType.getSize()) {
			throw new IllegalArgumentException("文件太大，不允许上传");
		}
		if(CustomFileType.ZIP == fileType && CustomFileType.ZIP.getFileTypeList().contains(extensionName)) {
			ZipInputStream zin = null;
			try {
				zin = new ZipInputStream(file.getInputStream());
				ZipEntry entity = null;
				while(null != (entity = zin.getNextEntry())) {
					if(entity.isDirectory()) {
						continue;
					}
					fileName = entity.getName();
					extensionName = getFileTypeByFileName(fileName);
					fileType = CustomFileType.getType(extensionName);
					if(!CustomFileType.IMG.getFileTypeList().contains(extensionName)) {
						throw new RuntimeException(MessageFormat.format("文件{0}不为合法文件，请检查", fileName));
					}
					saveFile(zin, serialNo, fileType, fileName);
					rtn.put(fileName, String.valueOf(entity.getSize()));
					zin.closeEntry();
				}
				logger.info("上传图片文件信息：===================>上传成功！");
			} finally {
				IOUtils.closeQuietly(zin);
			}
		} else if (CustomFileType.IMG == fileType) {
			saveFile(file.getInputStream(), serialNo, fileType, fileName);
			rtn.put(fileName, String.valueOf(file.getSize()));
		}
		return rtn;
	}
	
	/**
	 * 保存图片
	 * @param in	文件输入流
	 * @param fileName
	 * @param fileType
	 * @return
	 * @throws java.io.FileNotFoundException, IOException
	 */
	@Override
	public String saveFile(InputStream in, CustomFileType fileType, String fileName) throws FileNotFoundException, IOException {
		String realFileName = genFileName(fileName);
		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(new File(getParentFile(fileType), realFileName)));
			IOUtils.copy(in, out);
			out.flush();
		} finally {
			if(null != out) {
				out.close();
			}
		}
		return realFileName;
	}
	
	/**
	 * 保存zip包图片
	 * @param in	文件输入流
	 * @param fileName
	 * @param fileType
	 * @return
	 * @throws java.io.FileNotFoundException, IOException
	 */
	@Override
	public String saveFile(InputStream in, String serialNo, CustomFileType fileType, String fileName) throws FileNotFoundException, IOException {
		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(new File(getParentFile(fileType, serialNo), fileName)));
			IOUtils.copy(in, out);
			out.flush();
		} finally {
			if(null != out) {
				out.close();
			}
		}
		return fileName;
	}
	
	/**
	 * 根据图片名得到对应的File
	 * @param fileName
	 * @param fileType
	 * @return
	 */
	@Override
	public File getFileByFileName(String fileName, CustomFileType fileType) {
		Assert.hasText(fileName, "fileName不能为空");
		String[] strArr = fileName.split("\\.");
		Assert.isTrue(1 < strArr.length, String.format("文件名%s不合法", fileName));
		return new File(this.getParentFile(fileType),
				String.format("%s.%s", strArr[strArr.length - 2], strArr[strArr.length - 1]));
	}

	/**
	 * 根据图片名得到共享存储对应的File
	 * @param fileName
	 * @param fileType
	 * @return
	 */
	@Override
	public File getPlatFileByFileName(String fileName, CustomFileType fileType) {
		Assert.hasText(fileName, "fileName不能为空");
		String[] strArr = fileName.split("\\.");
		Assert.isTrue(1 < strArr.length, String.format("文件名%s不合法", fileName));
		return new File(this.getPlatParentFile(fileType),
				String.format("%s.%s", strArr[strArr.length - 2], strArr[strArr.length - 1]));
	}
	
	/**
	 * 生成文件名
	 * @param fileName
	 * @return
	 */
	@Override
	public String genFileName(String fileName) {
		Assert.hasText(fileName, "fileName不能为空");
		String[] strArr = fileName.split("\\.");
		Assert.isTrue(1 < strArr.length, String.format("文件名%s不合法", fileName));
		String path = null;
//        String name = StringUtils.left(strArr[0], 8);
		String name = strArr[0];
        String type = strArr[strArr.length - 1];
		do {
            path = String.format("%s_%s.%s", dateFmt.format(new Date()), name, type);
		} while(new File(path).exists());
		return path;
	}
	
	/**
	 * 得到文件类型
	 * @param fileName
	 * @return
	 */
	private String getFileTypeByFileName(String fileName) {
		if(null == fileName || fileName.isEmpty()) {
			return null;
		}
		return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
	}
	
	/**
	 * 得到照片上传的主目录
	 * @param fileType
	 * @return
	 */
	private File getParentFile(CustomFileType fileType) {
		Assert.notNull(fileType, "fileType不能为空");
		Assert.hasText(uploadFilesPath, "没有配置系统属性：upload_files_path，请联系管理员");
		File parentPath = new File(uploadFilesPath + "/" + fileType.getFilePath() + "/");
		if(!parentPath.exists()) {
			parentPath.mkdirs();
		}
		return parentPath;
	}

	/**
	 * 得到共享存储照片上传的主目录
	 * @param fileType
	 * @return
	 */
	private File getPlatParentFile(CustomFileType fileType) {
		Assert.notNull(fileType, "fileType不能为空");
		Assert.hasText(platFilesPath, "没有配置系统属性：plat_files_path，请联系管理员");
		File parentPath = new File(platFilesPath + "/" + fileType.getFilePath() + "/");
		if(!parentPath.exists()) {
			parentPath.mkdirs();
		}
		return parentPath;
	}
	
	/**
	 * 得到zip包文件上传的主目录
	 * @param fileType
	 * @return
	 */
	private File getParentFile(CustomFileType fileType, String zipName) {
		Assert.notNull(fileType, "fileType不能为空");
		Assert.hasText(uploadFilesPath, "没有配置系统属性：upload_files_path，请联系管理员");
		File parentPath = new File(uploadFilesPath + "/" + fileType.getFilePath() + "/" + zipName + "/");
		if(!parentPath.exists()) {
			parentPath.mkdirs();
		}
		return parentPath;
	}

}
