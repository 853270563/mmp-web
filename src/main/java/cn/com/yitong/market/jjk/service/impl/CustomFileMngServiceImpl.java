package cn.com.yitong.market.jjk.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.market.jjk.service.CustomFileMngService;
import cn.com.yitong.tools.codec.Base64;
import cn.com.yitong.util.CustomFileType;

/**
 * 
 * @author lc3@yitong.com.cn
 *
 */
@Service
public class CustomFileMngServiceImpl implements CustomFileMngService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private String uploadFilesPath = AppConstants.upload_files_path;

	private static final SimpleDateFormat dateFmt = new SimpleDateFormat("yyyyMMddHHmmss");
	
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
            throw new RuntimeException(String.format("文件%s不为合法文件，请检查", fileName));
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
						throw new RuntimeException(String.format("文件%s不为合法文件，请检查", fileName));
					}
					rtn.put(fileName, saveFile(zin, fileType, fileName));
					zin.closeEntry();
				}
				logger.info("上传图片文件信息：===================>上传成功！");
			} finally {
				zin.close();
			}
		} else if(!fileType.getFileTypeList().contains(filetype)) {
			throw new RuntimeException(String.format("文件%s不为合法文件，请检查", fileName));
		} else {
			logger.info("上传图片文件信息：===================>非zip上传");
			rtn.put(fileName, saveFile(file.getInputStream(), fileType, fileName));
		}
		return rtn;
	}
	
	/**
	 * 保存图片
	 * @param in	文件输入流
	 * @param fileName
	 * @param fileType
	 * @return 上传的文件名
	 * @throws java.io.FileNotFoundException, IOException
	 */
	@Override
	public String saveFile(InputStream in, CustomFileType fileType, String fileName) throws FileNotFoundException, IOException {
		String realFileName = genFileName(fileName);
		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(new File(getParentFile(fileType), realFileName)));
			byte[] buffer = new byte[1024];
			int bytesRead = -1;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			out.flush();
		} finally {
			if(null != out) {
				out.close();
			}
		}
		return realFileName;
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
	
	@Override
	public File getFileByFileName(String fileName, CustomFileType fileType,String number) {
		Assert.hasText(fileName, "fileName不能为空");
		String[] strArr = fileName.split("\\.");
		Assert.isTrue(1 < strArr.length, String.format("文件名%s不合法", fileName));
		return new File(this.getParentFile(fileType,number),
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
	private File getParentFile(CustomFileType fileType,String number) {
		Assert.notNull(fileType, "fileType不能为空");
		Assert.hasText(uploadFilesPath, "没有配置系统属性：upload_files_path，请联系管理员");
		File parentPath = new File(uploadFilesPath + "/" + fileType.getFilePath()+"/"+number + "/");
		if(!parentPath.exists()) {
			parentPath.mkdirs();
		}
		return parentPath;
	}

	@Override
	public String getFileByFileName2Base64(String fileName, CustomFileType fileType) {
		File fileByFileName = getFileByFileName(fileName, fileType);
		FileInputStream fileInputStream = null;
		byte[] len = new byte[(int) fileByFileName.length()];
		try {
			fileInputStream = new FileInputStream(fileByFileName);
			fileInputStream.read(len);
		} catch (IOException e) {
			logger.error(e.getMessage());
			return null;
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}
		return Base64.encodeToString(len);
	}
	
}
