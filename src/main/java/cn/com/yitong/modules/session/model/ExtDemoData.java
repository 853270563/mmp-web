package cn.com.yitong.modules.session.model;

import java.util.Date;

public class ExtDemoData {
    /**
     * DATA_ID
     */
    private String dataId;

    /**
     * URL
     */
    private String url;

    /**
     * CREATE_DTIME
     */
    private Date createDtime;

    /**
     * CODE
     */
    private String code;

    /**
     * 
     *
     * @return 
     */
    public String getDataId() {
        return dataId;
    }

    /**
     * 
     *
     * @param 
     */
    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    /**
     * 
     *
     * @return 
     */
    public String getUrl() {
        return url;
    }

    /**
     * 
     *
     * @param 
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 
     *
     * @return 
     */
    public Date getCreateDtime() {
        return createDtime;
    }

    /**
     * 
     *
     * @param 
     */
    public void setCreateDtime(Date createDtime) {
        this.createDtime = createDtime;
    }

    /**
     * 
     *
     * @return 
     */
    public String getCode() {
        return code;
    }

    /**
     * 
     *
     * @param 
     */
    public void setCode(String code) {
        this.code = code;
    }

    public static final class FL {
        public static final String dataId = "DATA_ID";

        public static final String url = "URL";

        public static final String createDtime = "CREATE_DTIME";

        public static final String code = "CODE";
    }
}