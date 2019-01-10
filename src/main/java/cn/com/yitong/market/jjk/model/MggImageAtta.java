package cn.com.yitong.market.jjk.model;

import java.math.BigDecimal;
import java.util.Date;

public class MggImageAtta {
    /**
     * ATTA_ID
     */
    private String attaId;

    /**
     * TRAN_CODE
     */
    private String tranCode;

    /**
     * TRANS_ID
     */
    private String transId;

    /**
     * ATTA_TYPE
     */
    private String attaType;

    /**
     * ATTA_NAME
     */
    private String attaName;

    /**
     * ATTA_URL
     */
    private String attaUrl;

    /**
     * ATTA_DIR_URL
     */
    private String attaDirUrl;

    /**
     * ATTA_SIZE
     */
    private BigDecimal attaSize;

    /**
     * ATTA_UPDA_TIME
     */
    private Date attaUpdaTime;

    /**
     * ATTA_USER
     */
    private String attaUser;

    /**
     * ATTA_RES1
     */
    private String attaRes1;

    /**
     * ATTA_RES2
     */
    private String attaRes2;

    /**
     * 
     *
     * @return 
     */
    public String getAttaId() {
        return attaId;
    }

    /**
     * 
     *
     * @param 
     */
    public void setAttaId(String attaId) {
        this.attaId = attaId;
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
    public String getAttaType() {
        return attaType;
    }

    /**
     * 
     *
     * @param 
     */
    public void setAttaType(String attaType) {
        this.attaType = attaType;
    }

    /**
     * 
     *
     * @return 
     */
    public String getAttaName() {
        return attaName;
    }

    /**
     * 
     *
     * @param 
     */
    public void setAttaName(String attaName) {
        this.attaName = attaName;
    }

    /**
     * 
     *
     * @return 
     */
    public String getAttaUrl() {
        return attaUrl;
    }

    /**
     * 
     *
     * @param 
     */
    public void setAttaUrl(String attaUrl) {
        this.attaUrl = attaUrl;
    }

    /**
     * 
     *
     * @return 
     */
    public String getAttaDirUrl() {
        return attaDirUrl;
    }

    /**
     * 
     *
     * @param 
     */
    public void setAttaDirUrl(String attaDirUrl) {
        this.attaDirUrl = attaDirUrl;
    }

    /**
     * 
     *
     * @return 
     */
    public BigDecimal getAttaSize() {
        return attaSize;
    }

    /**
     * 
     *
     * @param 
     */
    public void setAttaSize(BigDecimal attaSize) {
        this.attaSize = attaSize;
    }

    /**
     * 
     *
     * @return 
     */
    public Date getAttaUpdaTime() {
        return attaUpdaTime;
    }

    /**
     * 
     *
     * @param 
     */
    public void setAttaUpdaTime(Date attaUpdaTime) {
        this.attaUpdaTime = attaUpdaTime;
    }

    /**
     * 
     *
     * @return 
     */
    public String getAttaUser() {
        return attaUser;
    }

    /**
     * 
     *
     * @param 
     */
    public void setAttaUser(String attaUser) {
        this.attaUser = attaUser;
    }

    /**
     * 
     *
     * @return 
     */
    public String getAttaRes1() {
        return attaRes1;
    }

    /**
     * 
     *
     * @param 
     */
    public void setAttaRes1(String attaRes1) {
        this.attaRes1 = attaRes1;
    }

    /**
     * 
     *
     * @return 
     */
    public String getAttaRes2() {
        return attaRes2;
    }

    /**
     * 
     *
     * @param 
     */
    public void setAttaRes2(String attaRes2) {
        this.attaRes2 = attaRes2;
    }

    public static final class FL {
        public static final String attaId = "ATTA_ID";

        public static final String tranCode = "TRAN_CODE";

        public static final String transId = "TRANS_ID";

        public static final String attaType = "ATTA_TYPE";

        public static final String attaName = "ATTA_NAME";

        public static final String attaUrl = "ATTA_URL";

        public static final String attaDirUrl = "ATTA_DIR_URL";

        public static final String attaSize = "ATTA_SIZE";

        public static final String attaUpdaTime = "ATTA_UPDA_TIME";

        public static final String attaUser = "ATTA_USER";

        public static final String attaRes1 = "ATTA_RES1";

        public static final String attaRes2 = "ATTA_RES2";
    }
}