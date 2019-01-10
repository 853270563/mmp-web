package cn.com.yitong.core.web.fileupload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import cn.com.yitong.common.utils.observe.ObservableManager;
import cn.com.yitong.common.utils.observe.suport.StringObservableKeysResolver;
import cn.com.yitong.core.util.ConfigName;

/**
 * 上传文件工具类
 * @author lc3@yitong.com.cn
 */
public class FileUploadUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadUtils.class);
    public static final ObservableManager<String> uploadFilesObservableManager =
            new ObservableManager<String>(new StringObservableKeysResolver());
    /**
     * 已经上传
     */
    public static final long HAS_UPLOADED = -1;
    /**
     * 未上传
     */
    public static final long HAS_NOT_UPLOADED = 0;

    /**
     * 得到实际文件句柄
     * @param fileName 文件名
     * @return 正式文件
     */
    public static File getFile(String fileName) {
        Assert.hasText(fileName, "fileName不能为空");
        fileName = fileName.toLowerCase();
        return new File(getDir() + getFileDir(fileName), fileName);
    }

    /**
     * 得到临时文件句柄
     * @param fileName 文件名
     * @return 临时文件
     */
    public static File getTempFile(String fileName) {
        Assert.hasText(fileName, "fileName不能为空");
        fileName = fileName.toLowerCase();
        return new File(getTempDir() + getFileDir(fileName), fileName + ".tmp");
    }

    /**
     * 求得文件目录名称
     * @param fileName 文件名
     * @return 目录名称
     */
    private static String getFileDir(String fileName) {
        return fileName.length() >= 2 ? fileName.substring(0, 2) : "xx";
    }

    /**
     * 得到临时目录
     * @return 临时目录
     */
    private static String getTempDir() {
        return ConfigName.dataFilesRoot() + "/uploadFiles/tmp/";
    }

    /**
     * 得到正式目录
     * @return 正式目录
     */
    private static String getDir() {
        return ConfigName.dataFilesRoot() + "/uploadFiles/";
    }

    /**
     * 检查文件状态
     * @param fileName 文件名
     * @return -1: 已完成， 0： 未上传， 其他正整数： 文件大小
     */
    public static long checkFileStatus(String fileName) {
        if(getFile(fileName).exists()) {
            return HAS_UPLOADED;
        }
        final File tempFile = getTempFile(fileName);
        if(tempFile.exists()) {
           return tempFile.length();
        } else {
            return HAS_NOT_UPLOADED;
        }
    }

    /**
     * 保存文件
     * @param inputStream 上传文件
     * @param fileName 文件名
     * @param offset 文件偏移
     */
    public static boolean saveFile(InputStream inputStream, String fileName, long offset) throws IOException {
        Assert.notNull(fileName, "fileName不能为空");
        Assert.notNull(inputStream, "inputStream不能为空");
        fileName = fileName.toLowerCase();
        final File tempFile = getTempFile(fileName);
        RandomAccessFile randomAccessFile = null;
        if(0 < offset && (!tempFile.exists() || tempFile.length() != offset)) {
            throw new IllegalArgumentException("文件续传失败，文件写位置与临时文件不对应：" + fileName);
        }
        try {
            if(!tempFile.getParentFile().exists()) {
                tempFile.getParentFile().mkdirs();
            }
            randomAccessFile = new RandomAccessFile(tempFile, "rw");
            if(0 < offset) {
                randomAccessFile.seek(offset);
            }
            byte[] buffer = new byte[512];
            int len = -1;
            while(0 < (len = inputStream.read(buffer))) {
                randomAccessFile.write(buffer, 0, len);
            }
        } finally {
            IOUtils.closeQuietly(randomAccessFile);
        }
        return afterFileUpload(fileName);
    }

    /**
     * 得到文件的MD5值
     * @param fileName 文件名
     * @return 文件名的MD5值
     */
    public static String getFileNameMd5(String fileName) {
        Assert.notNull(fileName, "fileName不能为空");
        return fileName.split("\\.")[0];
    }

    /**
     * 文件上传完成后，转存并通知相应的观察者
     * @param fileName 文件名
     * @throws java.io.IOException
     * @return 是否成功
     */
    private static boolean afterFileUpload(String fileName) throws IOException {
        final File file = getFile(fileName);
        final File tempFile = getTempFile(fileName);
        if(tempFile.exists() && !file.exists()) {
            // 检查完整性
            FileInputStream data = null;
            String md5Hex = null;
            try {
                data = new FileInputStream(tempFile);
                md5Hex = DigestUtils.md5Hex(data).toLowerCase();
            } finally {
                IOUtils.closeQuietly(data);
            }
            if(!getFileNameMd5(fileName).equals(md5Hex)) {
                logger.debug("文件续传未完成，文件MD5值不一致");
                return false;
            }
            FileUtils.moveFile(tempFile, file);
            uploadFilesObservableManager.notifyObservers(fileName, fileName);
            logger.info("文件续传成功：{}", fileName);
        } else {
            logger.warn("文件续传异常，直接忽略，目标文件已经存在：{}", fileName);
            tempFile.deleteOnExit();
        }
        return true;
    }

}
