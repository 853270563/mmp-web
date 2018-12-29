package cn.com.yitong.modules.ares.fileservice.service.impl;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Enumeration;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.yitong.common.utils.ConfigUtils;
import cn.com.yitong.modules.ares.fileservice.service.FileUnZipService;
import cn.com.yitong.modules.ares.fileservice.service.ICustomFileMngService;
import cn.com.yitong.modules.service.fileTab.dao.AresFileTabDao;
import cn.com.yitong.modules.service.fileTab.model.AresFileTab;
import cn.com.yitong.modules.service.fileTab.service.AresFileTabService;
import cn.com.yitong.util.CustomFileType;

/**
 * @author zhanglong
 * @date 17/9/5
 */
@Service
public class UnZipFileServiceImpl implements FileUnZipService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private static final String imgFilePath = ConfigUtils.getValue("upload_files_path", "") + "/img/";

    @Autowired
    private ICustomFileMngService customFileMngService;

    @Autowired
    private AresFileTabService aresFileTabService;
    @Resource
    private AresFileTabDao aresFileTabDao;

    @Override
    public boolean zipFileSave(String imgSerialNo, File file) {
        try {
        	File imgDir = new File(imgFilePath + imgSerialNo);
        	if (imgDir.exists()) {
        		deleteFile(imgDir);
        	}
        	aresFileTabDao.deleteBySerialNo(imgSerialNo);
        	
            String fileName = "";
            String extName = "";
            CustomFileType fileType = null;
            AresFileTab aresFileTab = null;
            InputStream in = null;
            ZipFile zipFile = new ZipFile(file);
            Enumeration entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                if(entry.isDirectory()) {
                    continue;
                }
                //保存到磁盘
                fileName = entry.getName();
                extName = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                fileType = CustomFileType.getType(extName);
                in = zipFile.getInputStream(entry);
                if (in.available() == 0) {
                	continue;
                }
                
                customFileMngService.saveFile(in, imgSerialNo, fileType, fileName);
                //保存到数据库
                aresFileTab = new AresFileTab();
                aresFileTab.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                aresFileTab.setCreateTime(new Date());
                aresFileTab.setSerialNo(imgSerialNo);
                aresFileTab.setFileName(fileName);
                aresFileTab.setFileSize(new BigDecimal(entry.getSize()));
                aresFileTabService.save(aresFileTab);
                in.close();
            }
            zipFile.close();
            file.delete();
            logger.info("上传影像处理成功，imageSerialNo={}", imgSerialNo);
        } catch (Exception e) {
            logger.error("保存文件失败：error:", e);
            return false;
        }
        return true;
    }
    
	/**
	 * 删除文件或文件夹
	 * @param file
	 * @return
	 */
	private boolean deleteFile(File file){
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
}
