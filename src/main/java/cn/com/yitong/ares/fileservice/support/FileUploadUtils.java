package cn.com.yitong.ares.fileservice.support;

import cn.com.yitong.common.utils.ConfigUtils;
import cn.com.yitong.common.utils.SpringContextUtils;
import cn.com.yitong.common.utils.observe.ObservableManager;
import cn.com.yitong.common.utils.observe.suport.StringObservableKeysResolver;
import cn.com.yitong.core.util.ConfigName;
import freemarker.template.utility.DateUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 上传文件工具类
 * @author lc3@yitong.com.cn
 */
public class FileUploadUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadUtils.class);
    public static final ObservableManager<String> uploadFilesObservableManager =
            new ObservableManager<String>(new StringObservableKeysResolver());
    private static JdbcTemplate jdbcTemplate = SpringContextUtils.getBean(JdbcTemplate.class);

    private static final String QUERY_SQL = "select STATUS,FILE_PATH,FILE_NEW_NAME,FILE_SIZE,CHECKSUM from SYS_FILE where FILE_ID = ?";
    private static final String INSERT_SQL = "insert into SYS_FILE(FILE_ID, BUSI_ID, BUSI_TYPE," +
            "FILE_ORIGINAL_NAME, FILE_TYPE, FILE_NEW_NAME, FILE_SIZE, FILE_PATH, CREATE_TIME, STATUS)" +
            " values(?, ?, ?, ?, ?, ?, 0, ?, ?, ?)";
    private static final String UPDATE_SQL = "update SYS_FILE set FILE_SIZE=?,FILE_PATH = ?,STATUS=?,CHECKSUM=? where FILE_ID = ?";
    private static final String DELETE_SQL = "DELETE FROM SYS_FILE WHERE FILE_ID = ?";
    //临时目录
    public static final String CACHE_DIR = "cache";
    //垃圾桶
    public static final String TRASH_DIR = "trash";
    //正式存放文件的目录
    public static final String FILE_DIR = "file";
    //数据库STATUS为0表示准备上传或者上传中
    public static final String STATUS_INIT = "0";
    //数据库STATUS为1表示上传中
    public static final String STATUS_UPLOADING = "1";
    //数据库STATUS为2表示上传成功
    public static final String STATUS_SUCCESS = "2";
    public static String FILE_PATH = ConfigUtils.getValue("fileservice_path");
    /**
     * 已经上传
     */
    public static final long HAS_UPLOADED = -1;
    /**
     * 未上传
     */
    public static final long HAS_NOT_UPLOADED = 0;
    ////[0-只查询，不上传文件，1-上传文件，需要在文件管理表中增加记录],
    public static final String WILLUPLOADFILE_QUERY = "0";
    public static final String WILLUPLOADFILE_UPLOAD = "1";
    /**
     * 得到实际文件句柄
     * @param fileName 文件名
     * @return 正式文件
     */
    public static File getFile(String fileName) {
        Assert.hasText(fileName, "fileName不能为空");
        String pathMonth = getCurrentMonthStr();
        return new File(FILE_PATH + "/" + FILE_DIR + "/" + pathMonth, fileName);
    }

    /**
     * 得到临时文件句柄
     * @param fileName 文件名
     * @return 临时文件
     */
    public static File getTempFile(String fileName) {
        Assert.hasText(fileName, "fileName不能为空");
        return new File(FILE_PATH  + "/" + CACHE_DIR, fileName + ".tmp");
    }

    /**
     * 得到垃圾桶文件句柄
     * @param fileName
     * @return
     */
    public static File getTrashFile(String fileName) {
        Assert.hasText(fileName, "fileName不能为空");
        return new File(FILE_PATH  + "/" + TRASH_DIR, fileName);
    }

    /**
     * 检查文件状态
     * @param fileName 文件名
     * @return -1: 已完成， 0： 未上传， 其他正整数： 文件大小
     */
    public static Map<String, Object> checkFileStatus(String willUploadFile, String fileName, String BUSI_ID, String BUSI_TYPE) {
        Map<String, Object> checkMap = new HashMap<String, Object>();
        checkMap.put("status", HAS_NOT_UPLOADED);
        checkMap.put("length", 0);
        checkMap.put("checksum", "");
        List<Map<String, Object>> list = jdbcTemplate.queryForList(QUERY_SQL, fileName);
        if(willUploadFile.equals(WILLUPLOADFILE_UPLOAD) && list.isEmpty()) {
            saveTempFileDbInfo(fileName, BUSI_ID, BUSI_TYPE);
        }else if(!list.isEmpty()){
            Map<String, Object> fileMap = list.get(0);
            checkMap.put("length", fileMap.get("FILE_SIZE"));
            checkMap.put("checksum", fileMap.get("CHECKSUM"));
            checkMap.put("status", fileMap.get("STATUS"));
        }
        return checkMap;
    }

    /**
     * 保存文件
     * @param inputStream 上传文件
     * @param fileName 文件名
     * @param offset 文件偏移
     */
    public static boolean saveFile(InputStream inputStream, String fileName, long offset,
                                   long contentLength, String checksum) throws IOException {
        Assert.notNull(fileName, "fileName不能为空");
        Assert.notNull(inputStream, "inputStream不能为空");
        List<Map<String, Object>> list = jdbcTemplate.queryForList(QUERY_SQL, fileName);
        if(list.isEmpty()) {
            throw new IllegalArgumentException("上传失败,服务端数据缺失：" + fileName);
        }
        Map<String, Object> fileMap = list.get(0);
        String fileNewname = fileMap.get("FILE_NEW_NAME").toString();
        final File tempFile = getTempFile(fileNewname);
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
        return afterFileUpload(fileName, fileMap, contentLength, checksum);
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
    private static boolean afterFileUpload(String fileName, Map<String, Object> fileMap,
                                           long contentLength, String checksum) throws IOException {
        String fileNewName = fileMap.get("FILE_NEW_NAME").toString();
        String finalFilePath = fileMap.get("FILE_PATH").toString();
        final File file = getFile(fileNewName);
        final File tempFile = getTempFile(fileNewName);
        boolean flag = true;//文件是否上传完成
        long tempFileLength = 0;
        if(tempFile.exists() && !file.exists()) {
            tempFileLength = tempFile.length();
            if(contentLength != tempFileLength) {
                logger.debug("文件续传未完成，文件大小不一致");
                flag = false;
            }else {
                finalFilePath = file.getParent();
                //文件路径不存在就创建
                if(!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                FileUtils.moveFile(tempFile, file);
                uploadFilesObservableManager.notifyObservers(fileName, fileName);
                logger.info("文件续传成功：{}", fileName);
            }
        } else {
            logger.warn("文件续传异常，直接忽略，目标文件已经存在：{}", fileName);
            tempFile.deleteOnExit();
        }
        //上传停止时更新附件表数据
        jdbcTemplate.update(UPDATE_SQL, tempFileLength, finalFilePath, flag ? STATUS_SUCCESS : STATUS_UPLOADING, checksum, fileName);
        return flag;
    }

    /**
     *对刚上传的文件生成一条附件数据
     * @param fileName 客户端上传的文件相对路径，例如：/data/sfz.jpg
     */
    private static void saveTempFileDbInfo(String fileName, String BUSI_ID, String BUSI_TYPE) {
        String[] fileTypes = fileName.split("\\.");
        String fileType = fileTypes.length >1 ? fileTypes[1]:"unknown";
        String fileNewName = UUID.randomUUID().toString() + "." + fileType;
        String filePath = FILE_PATH + "/" + CACHE_DIR;
        jdbcTemplate.update(INSERT_SQL, fileName, BUSI_ID, BUSI_TYPE, "", fileType,
                fileNewName, filePath, new Date(), STATUS_INIT);
    }

    /**
     * 取得当前年月日字符串
     * @return
     */
    private static String getCurrentMonthStr() {
        return new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
    }

    public static File getServerFile(String fileId) {
        Assert.notNull(fileId, "fileName不能为空");
        File file = null;
        List<Map<String, Object>> list = jdbcTemplate.queryForList(QUERY_SQL, fileId);
        if(!list.isEmpty()) {
            Map<String, Object> map = list.get(0);
            String filePath = map.get("FILE_PATH").toString();
            String fileNewName = map.get("FILE_NEW_NAME").toString();
            file = new File(filePath, fileNewName);
        }
        return file;
    }

    /**
     * 删除单个文件。1、删除附件表SYS_FILE对应数据  2、把服务器上文件移至垃圾桶目录
     * @param fileId
     */
    public static void delFile(String fileId) {
        Assert.notNull(fileId, "fileName不能为空");
        List<Map<String, Object>> list = jdbcTemplate.queryForList(QUERY_SQL, fileId);
        if(!list.isEmpty()) {
            Map<String, Object> map = list.get(0);
            String filePath = map.get("FILE_PATH").toString();
            String fileNewName = map.get("FILE_NEW_NAME").toString();
            File file = new File(filePath, fileNewName);
            File trashFile = getTrashFile(fileNewName);
            jdbcTemplate.update(DELETE_SQL, fileId);
            try {
                FileUtils.moveFile(file, trashFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(getCurrentMonthStr());
    }
}
