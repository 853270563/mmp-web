package cn.com.yitong.modules.service.fileTab.model;

import cn.com.yitong.common.utils.ConfigUtils;
import cn.com.yitong.common.persistence.BaseEntity;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 文件表
 *
 * @author sunw@yitong.com.cn
 */
public class AresFileTab extends BaseEntity {

    /**
     * 编号
     */
    private String id;
    /**
     * 序列号
     */
    private String serialNo;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 大小
     */
    private BigDecimal fileSize;
    /**
     * 时间
     */
    private Date createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public BigDecimal getFileSize() {
        return fileSize;
    }

    public void setFileSize(BigDecimal fileSize) {
        this.fileSize = fileSize;
    }
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public static class TF {
        public static String id = "ID";  // 编号
        public static String serialNo = "SERIAL_NO";  // 序列号
        public static String fileName = "FILE_NAME";  // 文件名
        public static String fileSize = "FILE_SIZE";  // 大小
        public static String createTime = "CREATE_TIME";  // 时间

    }
}
