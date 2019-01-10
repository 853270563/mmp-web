package cn.com.yitong.market.jjk.model;


import java.util.Date;

public class MggTranCheck {
    /**
     * APPR_ID
     */
    private String apprId;

    /**
     * TRAN_CODE
     */
    private String tranCode;

    /**
     * TRANS_ID
     */
    private String transId;

    /**
     * APPR_TIME
     */
    private Date apprTime;

    /**
     * APPR_USER
     */
    private String apprUser;

    /**
     * APPR_STAT_CODE
     */
    private String apprStatCode;

    /**
     * APPR_STAT
     */
    private String apprStat;

    /**
     * APPR_RETN_INFO
     */
    private String apprRetnInfo;

    /**
     * 
     *
     * @return 
     */
    public String getApprId() {
        return apprId;
    }

    /**
     * 
     *
     * @param 
     */
    public void setApprId(String apprId) {
        this.apprId = apprId;
    }

    /**
     * 
     *
     * @return 
     */
    public String getTranCode() {
        return tranCode;
    }

    /**
     * 
     *
     * @param 
     */
    public void setTranCode(String tranCode) {
        this.tranCode = tranCode;
    }

    /**
     * 
     *
     * @return 
     */
    public String getTransId() {
        return transId;
    }

    /**
     * 
     *
     * @param 
     */
    public void setTransId(String transId) {
        this.transId = transId;
    }

    /**
     * 
     *
     * @return 
     */
    public Date getApprTime() {
        return apprTime;
    }

    /**
     * 
     *
     * @param 
     */
    public void setApprTime(Date apprTime) {
        this.apprTime = apprTime;
    }

    /**
     * 
     *
     * @return 
     */
    public String getApprUser() {
        return apprUser;
    }

    /**
     * 
     *
     * @param 
     */
    public void setApprUser(String apprUser) {
        this.apprUser = apprUser;
    }

    /**
     * 
     *
     * @return 
     */
    public String getApprStatCode() {
        return apprStatCode;
    }

    /**
     * 
     *
     * @param 
     */
    public void setApprStatCode(String apprStatCode) {
        this.apprStatCode = apprStatCode;
    }

    /**
     * 
     *
     * @return 
     */
    public String getApprStat() {
        return apprStat;
    }

    /**
     * 
     *
     * @param 
     */
    public void setApprStat(String apprStat) {
        this.apprStat = apprStat;
    }

    /**
     * 
     *
     * @return 
     */
    public String getApprRetnInfo() {
        return apprRetnInfo;
    }

    /**
     * 
     *
     * @param 
     */
    public void setApprRetnInfo(String apprRetnInfo) {
        this.apprRetnInfo = apprRetnInfo;
    }

    public static final class FL {
        public static final String apprId = "APPR_ID";

        public static final String tranCode = "TRAN_CODE";

        public static final String transId = "TRANS_ID";

        public static final String apprTime = "APPR_TIME";

        public static final String apprUser = "APPR_USER";

        public static final String apprStatCode = "APPR_STAT_CODE";

        public static final String apprStat = "APPR_STAT";

        public static final String apprRetnInfo = "APPR_RETN_INFO";
    }
}