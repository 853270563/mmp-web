package cn.com.yitong.market.jjk.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import cn.com.yitong.market.jjk.service.ImageMngService;

/**
 * 
 * @author lc3@yitong.com.cn
 *
 */
@Service
public class ImageMngServiceImpl implements ImageMngService {
	

	private static final List<String> IMAGE_FILETYPE_LIST = Arrays.asList("jpeg",
			"bmp", "png", "gif", "jpg", "ico");
	
	@Value("${upload_files_path}")
	private String uploadFilesPath;

	private static final SimpleDateFormat dateFmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	
	/**
	 * 保存上传图片
	 * @param file
	 * @return
	 * @throws java.io.FileNotFoundException
	 * @throws java.io.IOException
	 */
	@Override
	public Map<String, String> savePhoteMultipartFile(MultipartFile file) throws FileNotFoundException, IOException {
		Map<String, String> rtn = new HashMap<String, String>();
		String fileName = file.getOriginalFilename();
		String filetype = getFileTypeByFileName(fileName);
		
		if("zip".equals(filetype)) {
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
					rtn.put(fileName, savePhote(zin, filetype));
					zin.closeEntry();
				}
			} finally {
				zin.close();
			}
		} else {
			rtn.put(fileName, savePhote(file.getInputStream(), filetype));
		}
		return rtn;
	}
	
	/**
	 * 保存图片
	 * @param in	文件输入流
	 * @param filetype	文件类型
	 * @return
	 * @throws java.io.FileNotFoundException, IOException
	 */
	@Override
	public String savePhote(InputStream in, String filetype) throws FileNotFoundException, IOException {
		String realFileName = genPhotoFileName(filetype);
		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(new File(getPhotoParentFile(), realFileName)));
			byte[] buffer = new byte[1024];
			int bytesRead = -1;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			out.flush();
		} finally {
			out.close();
		}
		return realFileName;
	}
	
	/**
	 * 根据图片名得到对应的File
	 * @param fileName
	 * @return
	 */
	@Override
	public File getFileByPhotoName(String fileName) {
		return new File(this.uploadFilesPath, fileName);
	}
	
	/**
	 * 生成文件名
	 * @param filetype
	 * @return
	 */
	@Override
	public String genPhotoFileName(String filetype) {
		Assert.hasText(filetype, "filetype不能为空");
		filetype = filetype.replaceAll(".", "");
		Assert.isTrue(isImage4FileType(filetype), String.format("图片%s格式无法识别", filetype));
		String path = null;
		do {
			path = String.format("photo_%s.%s", dateFmt.format(new Date()), filetype);
		} while(new File(path).exists());
		return path;
	}
	
	/**
	 * 判断文件格式是否为图片文件
	 * @param filetype 扩展名
	 * @return
	 */
	public boolean isImage4FileType(String filetype) {
		if(null == filetype || filetype.isEmpty() || 
				!IMAGE_FILETYPE_LIST.contains(filetype)) {
			return false;
		} else {
			return true;
		}
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
	 * @return
	 */
	private File getPhotoParentFile() {
		Assert.hasText(uploadFilesPath, "没有配置系统属性：upload_files_path，请联系管理员");
		File parentPath = new File(uploadFilesPath + "/uploadPhotos");
		if(!parentPath.exists()) {
			parentPath.mkdirs();
		}
		return parentPath;
	}

}
