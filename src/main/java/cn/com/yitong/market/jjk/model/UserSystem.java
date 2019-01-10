package cn.com.yitong.market.jjk.model;

import java.util.Date;

public class UserSystem {
    /**
     * MAP_ID
     */
    private String mapId;

    /**
     * USER_ID
     */
    private String userId;

    /**
     * SYST_CODE
     */
    private String systCode;

    /**
     * DEVI_TYPE
     */
    private String deviType;

    /**
     * DEVI_CODE
     */
    private String deviCode;

    /**
     * SYST_FLAG
     */
    private String systFlag;

    /**
     * START_DATE
     */
    private Date startDate;

    /**
     * END_DATE
     */
    private Date endDate;

    /**
     * 
     *
     * @return 
     */
    public String getMapId() {
        return mapId;
    }

    /**
     * 
     *
     * @param 
     */
    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    /**
     * 
     *
     * @return 
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 
     *
     * @param 
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 
     *
     * @return 
     */
    public String getSystCode() {
        return systCode;
    }

    /**
     * 
     *
     * @param 
     */
    public void setSystCode(String systCode) {
        this.systCode = systCode;
    }

    /**
     * 
     *
     * @return 
     */
    public String getDeviType() {
        return deviType;
    }

    /**
     * 
     *
     * @param 
     */
    public void setDeviType(String deviType) {
        this.deviType = deviType;
    }

    /**
     * 
     *
     * @return 
     */
    public String getDeviCode() {
        return deviCode;
    }

    /**
     * 
     *
     * @param 
     */
    public void setDeviCode(String deviCode) {
        this.deviCode = deviCode;
    }

    /**
     * 
     *
     * @return 
     */
    public String getSystFlag() {
        return systFlag;
    }

    /**
     * 
     *
     * @param 
     */
    public void setSystFlag(String systFlag) {
        this.systFlag = systFlag;
    }

    /**
     * 
     *
     * @return 
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * 
     *
     * @param 
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * 
     *
     * @return 
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * 
     *
     * @param 
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public static final class FL {
        public static final String mapId = "MAP_ID";

        public static final String userId = "USER_ID";

        public static final String systCode = "SYST_CODE";

        public static final String deviType = "DEVI_TYPE";

        public static final String deviCode = "DEVI_CODE";

        public static final String systFlag = "SYST_FLAG";

        public static final String startDate = "START_DATE";

        public static final String endDate = "END_DATE";
    }
}