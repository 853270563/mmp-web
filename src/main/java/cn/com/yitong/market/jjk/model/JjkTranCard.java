package cn.com.yitong.market.jjk.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import cn.com.yitong.framework.util.CustomJsonDateDeserializer;
import cn.com.yitong.util.ConfigEnum;
import cn.com.yitong.util.StringUtil;

public class JjkTranCard {
    /**
     * TRANS_ID
     */
    private String transId;

    /**
     * SIGN_TYPE
     */
    private String signType;

    /**
     * CARD_TYPE
     */
    private String cardType;

    /**
     * CARD_NO
     */
    private String cardNo;

    /**
     * INPU_TYPE
     */
    private String inpuType;

    /**
     * CERT_TYPE
     */
    private String certType;

    /**
     * CERT_NO
     */
    private String certNo;

    /**
     * CERT_ORGAN
     */
    private String certOrgan;

    /**
     * CERT_STAR_DATE
     */
    private String certStarDate;

    /**
     * CERT_END_DATE
     */
    private String certEndDate;

    /**
     * CUST_NAME
     */
    private String custName;

    /**
     * CUST_PINX
     */
    private String custPinx;

    /**
     * CUST_PINM
     */
    private String custPinm;

    /**
     * CUST_SEX
     */
    private String custSex;

    /**
     * CUST_BORN
     */
    private String custBorn;

    /**
     * CUST_ADDR_HOME
     */
    private String custAddrHome;

    /**
     * CUST_ADDR_WORK
     */
    private String custAddrWork;

    /**
     * CUST_MOBI_PHONE
     */
    private String custMobiPhone;

    /**
     * CUST_TELE_PHONE
     */
    private String custTelePhone;

    /**
     * CUST_NATI
     */
    private String custNati;

    /**
     * CUST_JOB
     */
    private String custJob;

    /**
     * BANK_KZHY
     */
    private String bankKzhy;

    /**
     * BANK_YBTH
     */
    private String bankYbth;

    /**
     * FLAG_STAT
     */
    private String flagStat;

    /**
     * FLAG_PWD_ENVE
     */
    private String flagPwdEnve;

    /**
     * CUST_POST
     */
    private String custPost;

    /**
     * CUST_EMAIL
     */
    private String custEmail;

    /**
     * FLAG_OVER
     */
    private String flagOver;

    /**
     * ENSU_TYPE
     */
    private String ensuType;

    /**
     * ENSU_NO
     */
    private String ensuNo;

    /**
     * OVER_AMT
     */
    private BigDecimal overAmt;

    /**
     * MODI_PWD_TYPE
     */
    private String modiPwdType;

    /**
     * CARD_CONT
     */
    private String cardCont;

    /**
     * CARD_TRAC2
     */
    private String cardTrac2;

    /**
     * CARD_TRAC3
     */
    private String cardTrac3;

    /**
     * VOUC_NO
     */
    private String voucNo;

    /**
     * VOUC_TYPE
     */
    private String voucType;

    /**
     * VOUC_BANO
     */
    private String voucBano;

    /**
     * VOUC_SENO
     */
    private String voucSeno;

    /**
     * FLAG_SIGN_EBANK
     */
    private String flagSignEbank;

    /**
     * FLAG_SIGN_MBANK
     */
    private String flagSignMbank;

    /**
     * FLAG_SIGN_SBANK
     */
    private String flagSignSbank;

    /**
     * FLAG_SIGN_DEPO
     */
    private String flagSignDepo;

    /**
     * FLAG_SIGN_RES1
     */
    private String flagSignRes1;

    /**
     * FLAG_SIGN_RES2
     */
    private String flagSignRes2;

    /**
     * TOKEN_TYPE
     */
    private String tokenType;

    /**
     * TOKEN_NO
     */
    private String tokenNo;

    /**
     * SIGN_ORGAN
     */
    private String signOrgan;

    /**
     * SIGN_USER
     */
    private String signUser;

    /**
     * SIGN_DEVI_TYPE
     */
    private String signDeviType;

    /**
     * SIGN_DEVI_NO
     */
    private String signDeviNo;

    /**
     * SIGN_TIME
     */
	private Date signTime;

    /**
     * SIGN_GPS
     */
    private String signGps;

    /**
     * SIGN_STATE
     */
    private String signState;

    /**
     * SIGN_APPR_MARK
     */
    private String signApprMark;

    /**
     * DECA_RES1
     */
    private String decaRes1 = ConfigEnum.DICT_TRAN_DECARD_READ_STATE_NO.strVal();

    /**
     * DECA_RES2
     */
    private String decaRes2;

    /**
     * DECA_RES3
     */
    private String decaRes3;
    
    private String certImg;
    

    
    public String getCertImg() {
		return certImg;
	}

	public void setCertImg(String certImg) {
		this.certImg = certImg;
	}

	private List<MggImageAtta> attrList;
    


	public List<MggImageAtta> getAttrList() {
		return attrList;
	}

	public void setAttrList(List<MggImageAtta> attrList) {
		this.attrList = attrList;
	}

	/**
     * APPR_RETN_INFO
     */
    private String apprRetnInfo;

    public String getApprRetnInfo() {
        return apprRetnInfo;
    }

    public void setApprRetnInfo(String apprRetnInfo) {
        this.apprRetnInfo = apprRetnInfo;
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
    public String getSignType() {
        return signType;
    }

    /**
     * 
     *
     * @param 
     */
    public void setSignType(String signType) {
        this.signType = signType;
    }

    /**
     * 
     *
     * @return 
     */
    public String getCardType() {
        return cardType;
    }
    
    public String getCardTypeLabel() {
    	return StringUtil.showLabel(cardType, ConfigEnum.TRAN_DECARD_CARD_TYPE.strVal());
    }

    /**
     * 
     *
     * @param 
     */
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    /**
     * 
     *
     * @return 
     */
    public String getCardNo() {
        return cardNo;
    }

    /**
     * 
     *
     * @param 
     */
    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    /**
     * 
     *
     * @return 
     */
    public String getInpuType() {
        return inpuType;
    }

    /**
     * 
     *
     * @param 
     */
    public void setInpuType(String inpuType) {
        this.inpuType = inpuType;
    }

    /**
     * 
     *
     * @return 
     */
    public String getCertType() {
        return certType;
    }
    
    public String getCertTypeLabel() {
        return StringUtil.showLabel(certType, ConfigEnum.TRAN_DECARD_CERT_TYPE.strVal());
    }

    /**
     * 
     *
     * @param 
     */
    public void setCertType(String certType) {
        this.certType = certType;
    }

    /**
     * 
     *
     * @return 
     */
    public String getCertNo() {
        return certNo;
    }

    /**
     * 
     *
     * @param 
     */
    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    /**
     * 
     *
     * @return 
     */
    public String getCertOrgan() {
        return certOrgan;
    }

    /**
     * 
     *
     * @param 
     */
    public void setCertOrgan(String certOrgan) {
        this.certOrgan = certOrgan;
    }

    /**
     * 
     *
     * @return 
     */
    public String getCertStarDate() {
        return certStarDate;
    }

    /**
     * 
     *
     * @param 
     */
    public void setCertStarDate(String certStarDate) {
        this.certStarDate = certStarDate;
    }

    /**
     * 
     *
     * @return 
     */
    public String getCertEndDate() {
        return certEndDate;
    }

    /**
     * 
     *
     * @param 
     */
    public void setCertEndDate(String certEndDate) {
        this.certEndDate = certEndDate;
    }

    /**
     * 
     *
     * @return 
     */
    public String getCustName() {
        return custName;
    }

    /**
     * 
     *
     * @param 
     */
    public void setCustName(String custName) {
        this.custName = custName;
    }

    /**
     * 
     *
     * @return 
     */
    public String getCustPinx() {
        return custPinx;
    }

    /**
     * 
     *
     * @param 
     */
    public void setCustPinx(String custPinx) {
        this.custPinx = custPinx;
    }

    /**
     * 
     *
     * @return 
     */
    public String getCustPinm() {
        return custPinm;
    }

    /**
     * 
     *
     * @param 
     */
    public void setCustPinm(String custPinm) {
        this.custPinm = custPinm;
    }

    /**
     * 
     *
     * @return 
     */
    public String getCustSex() {
        return custSex;
    }

    /**
     * 
     *
     * @param 
     */
    public void setCustSex(String custSex) {
        this.custSex = custSex;
    }

    /**
     * 
     *
     * @return 
     */
    public String getCustBorn() {
        return custBorn;
    }

    /**
     * 
     *
     * @param 
     */
    public void setCustBorn(String custBorn) {
        this.custBorn = custBorn;
    }

    /**
     * 
     *
     * @return 
     */
    public String getCustAddrHome() {
        return custAddrHome;
    }

    /**
     * 
     *
     * @param 
     */
    public void setCustAddrHome(String custAddrHome) {
        this.custAddrHome = custAddrHome;
    }

    /**
     * 
     *
     * @return 
     */
    public String getCustAddrWork() {
        return custAddrWork;
    }

    /**
     * 
     *
     * @param 
     */
    public void setCustAddrWork(String custAddrWork) {
        this.custAddrWork = custAddrWork;
    }

    /**
     * 
     *
     * @return 
     */
    public String getCustMobiPhone() {
        return custMobiPhone;
    }

    /**
     * 
     *
     * @param 
     */
    public void setCustMobiPhone(String custMobiPhone) {
        this.custMobiPhone = custMobiPhone;
    }

    /**
     * 
     *
     * @return 
     */
    public String getCustTelePhone() {
        return custTelePhone;
    }

    /**
     * 
     *
     * @param 
     */
    public void setCustTelePhone(String custTelePhone) {
        this.custTelePhone = custTelePhone;
    }

    /**
     * 
     *
     * @return 
     */
    public String getCustNati() {
        return custNati;
    }

    /**
     * 
     *
     * @param 
     */
    public void setCustNati(String custNati) {
        this.custNati = custNati;
    }

    /**
     * 
     *
     * @return 
     */
    public String getCustJob() {
        return custJob;
    }

    /**
     * 
     *
     * @param 
     */
    public void setCustJob(String custJob) {
        this.custJob = custJob;
    }

    /**
     * 
     *
     * @return 
     */
    public String getBankKzhy() {
        return bankKzhy;
    }

    /**
     * 
     *
     * @param 
     */
    public void setBankKzhy(String bankKzhy) {
        this.bankKzhy = bankKzhy;
    }

    /**
     * 
     *
     * @return 
     */
    public String getBankYbth() {
        return bankYbth;
    }

    /**
     * 
     *
     * @param 
     */
    public void setBankYbth(String bankYbth) {
        this.bankYbth = bankYbth;
    }

    /**
     * 
     *
     * @return 
     */
    public String getFlagStat() {
        return flagStat;
    }

    /**
     * 
     *
     * @param 
     */
    public void setFlagStat(String flagStat) {
        this.flagStat = flagStat;
    }

    /**
     * 
     *
     * @return 
     */
    public String getFlagPwdEnve() {
        return flagPwdEnve;
    }

    /**
     * 
     *
     * @param 
     */
    public void setFlagPwdEnve(String flagPwdEnve) {
        this.flagPwdEnve = flagPwdEnve;
    }

    /**
     * 
     *
     * @return 
     */
    public String getCustPost() {
        return custPost;
    }

    /**
     * 
     *
     * @param 
     */
    public void setCustPost(String custPost) {
        this.custPost = custPost;
    }

    /**
     * 
     *
     * @return 
     */
    public String getCustEmail() {
        return custEmail;
    }

    /**
     * 
     *
     * @param 
     */
    public void setCustEmail(String custEmail) {
        this.custEmail = custEmail;
    }

    /**
     * 
     *
     * @return 
     */
    public String getFlagOver() {
        return flagOver;
    }

    /**
     * 
     *
     * @param 
     */
    public void setFlagOver(String flagOver) {
        this.flagOver = flagOver;
    }

    /**
     * 
     *
     * @return 
     */
    public String getEnsuType() {
        return ensuType;
    }

    /**
     * 
     *
     * @param 
     */
    public void setEnsuType(String ensuType) {
        this.ensuType = ensuType;
    }

    /**
     * 
     *
     * @return 
     */
    public String getEnsuNo() {
        return ensuNo;
    }

    /**
     * 
     *
     * @param 
     */
    public void setEnsuNo(String ensuNo) {
        this.ensuNo = ensuNo;
    }

    /**
     * 
     *
     * @return 
     */
    public BigDecimal getOverAmt() {
        return overAmt;
    }

    /**
     * 
     *
     * @param 
     */
    public void setOverAmt(BigDecimal overAmt) {
        this.overAmt = overAmt;
    }

    /**
     * 
     *
     * @return 
     */
    public String getModiPwdType() {
        return modiPwdType;
    }

    /**
     * 
     *
     * @param 
     */
    public void setModiPwdType(String modiPwdType) {
        this.modiPwdType = modiPwdType;
    }

    /**
     * 
     *
     * @return 
     */
    public String getCardCont() {
        return cardCont;
    }

    /**
     * 
     *
     * @param 
     */
    public void setCardCont(String cardCont) {
        this.cardCont = cardCont;
    }

    /**
     * 
     *
     * @return 
     */
    public String getCardTrac2() {
        return cardTrac2;
    }

    /**
     * 
     *
     * @param 
     */
    public void setCardTrac2(String cardTrac2) {
        this.cardTrac2 = cardTrac2;
    }

    /**
     * 
     *
     * @return 
     */
    public String getCardTrac3() {
        return cardTrac3;
    }

    /**
     * 
     *
     * @param 
     */
    public void setCardTrac3(String cardTrac3) {
        this.cardTrac3 = cardTrac3;
    }

    /**
     * 
     *
     * @return 
     */
    public String getVoucNo() {
        return voucNo;
    }

    /**
     * 
     *
     * @param 
     */
    public void setVoucNo(String voucNo) {
        this.voucNo = voucNo;
    }

    /**
     * 
     *
     * @return 
     */
    public String getVoucType() {
        return voucType;
    }

    /**
     * 
     *
     * @param 
     */
    public void setVoucType(String voucType) {
        this.voucType = voucType;
    }

    /**
     * 
     *
     * @return 
     */
    public String getVoucBano() {
        return voucBano;
    }

    /**
     * 
     *
     * @param 
     */
    public void setVoucBano(String voucBano) {
        this.voucBano = voucBano;
    }

    /**
     * 
     *
     * @return 
     */
    public String getVoucSeno() {
        return voucSeno;
    }

    /**
     * 
     *
     * @param 
     */
    public void setVoucSeno(String voucSeno) {
        this.voucSeno = voucSeno;
    }

    /**
     * 
     *
     * @return 
     */
    public String getFlagSignEbank() {
        return flagSignEbank;
    }

    /**
     * 
     *
     * @param 
     */
    public void setFlagSignEbank(String flagSignEbank) {
        this.flagSignEbank = flagSignEbank;
    }

    /**
     * 
     *
     * @return 
     */
    public String getFlagSignMbank() {
        return flagSignMbank;
    }

    /**
     * 
     *
     * @param 
     */
    public void setFlagSignMbank(String flagSignMbank) {
        this.flagSignMbank = flagSignMbank;
    }

    /**
     * 
     *
     * @return 
     */
    public String getFlagSignSbank() {
        return flagSignSbank;
    }

    /**
     * 
     *
     * @param 
     */
    public void setFlagSignSbank(String flagSignSbank) {
        this.flagSignSbank = flagSignSbank;
    }

    /**
     * 
     *
     * @return 
     */
    public String getFlagSignDepo() {
        return flagSignDepo;
    }

    /**
     * 
     *
     * @param 
     */
    public void setFlagSignDepo(String flagSignDepo) {
        this.flagSignDepo = flagSignDepo;
    }

    /**
     * 
     *
     * @return 
     */
    public String getFlagSignRes1() {
        return flagSignRes1;
    }

    /**
     * 
     *
     * @param 
     */
    public void setFlagSignRes1(String flagSignRes1) {
        this.flagSignRes1 = flagSignRes1;
    }

    /**
     * 
     *
     * @return 
     */
    public String getFlagSignRes2() {
        return flagSignRes2;
    }

    /**
     * 
     *
     * @param 
     */
    public void setFlagSignRes2(String flagSignRes2) {
        this.flagSignRes2 = flagSignRes2;
    }

    /**
     * 
     *
     * @return 
     */
    public String getTokenType() {
        return tokenType;
    }

    /**
     * 
     *
     * @param 
     */
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    /**
     * 
     *
     * @return 
     */
    public String getTokenNo() {
        return tokenNo;
    }

    /**
     * 
     *
     * @param 
     */
    public void setTokenNo(String tokenNo) {
        this.tokenNo = tokenNo;
    }

    /**
     * 
     *
     * @return 
     */
    public String getSignOrgan() {
        return signOrgan;
    }

    /**
     * 
     *
     * @param 
     */
    public void setSignOrgan(String signOrgan) {
        this.signOrgan = signOrgan;
    }

    /**
     * 
     *
     * @return 
     */
    public String getSignUser() {
        return signUser;
    }

    /**
     * 
     *
     * @param 
     */
    public void setSignUser(String signUser) {
        this.signUser = signUser;
    }

    /**
     * 
     *
     * @return 
     */
    public String getSignDeviType() {
        return signDeviType;
    }

    /**
     * 
     *
     * @param 
     */
    public void setSignDeviType(String signDeviType) {
        this.signDeviType = signDeviType;
    }

    /**
     * 
     *
     * @return 
     */
    public String getSignDeviNo() {
        return signDeviNo;
    }

    /**
     * 
     *
     * @param 
     */
    public void setSignDeviNo(String signDeviNo) {
        this.signDeviNo = signDeviNo;
    }

    /**
     * 
     *
     * @return 
     */
    @JsonSerialize(using=CustomJsonDateDeserializer.class)
    public Date getSignTime() {
        return signTime;
    }

    /**
     * 
     *
     * @param 
     */
    public void setSignTime(Date signTime) {
        this.signTime = signTime;
    }

    /**
     * 
     *
     * @return 
     */
    public String getSignGps() {
        return signGps;
    }

    /**
     * 
     *
     * @param 
     */
    public void setSignGps(String signGps) {
        this.signGps = signGps;
    }

    /**
     * 
     *
     * @return 
     */
    public String getSignState() {
        return signState;
    }
    
    public String getSignStateLabel() {
        return StringUtil.showLabel(signState, ConfigEnum.TRAN_DECARD_SIGN_STATE.strVal());
    }

    /**
     * 
     *
     * @param 
     */
    public void setSignState(String signState) {
        this.signState = signState;
    }

    /**
     * 
     *
     * @return 
     */
    public String getSignApprMark() {
        return signApprMark;
    }

    /**
     * 
     *
     * @param 
     */
    public void setSignApprMark(String signApprMark) {
        this.signApprMark = signApprMark;
    }

    /**
     * 
     *
     * @return 
     */
    public String getDecaRes1() {
        return decaRes1;
    }

    /**
     * 
     *
     * @param 
     */
    public void setDecaRes1(String decaRes1) {
        this.decaRes1 = decaRes1;
    }

    /**
     * 
     *
     * @return 
     */
    public String getDecaRes2() {
        return decaRes2;
    }

    /**
     * 
     *
     * @param 
     */
    public void setDecaRes2(String decaRes2) {
        this.decaRes2 = decaRes2;
    }

    /**
     * 
     *
     * @return 
     */
    public String getDecaRes3() {
        return decaRes3;
    }

    /**
     * 
     *
     * @param 
     */
    public void setDecaRes3(String decaRes3) {
        this.decaRes3 = decaRes3;
    }

    public static final class FL {
        public static final String transId = "TRANS_ID";

        public static final String signType = "SIGN_TYPE";

        public static final String cardType = "CARD_TYPE";

        public static final String cardNo = "CARD_NO";

        public static final String inpuType = "INPU_TYPE";

        public static final String certType = "CERT_TYPE";

        public static final String certNo = "CERT_NO";

        public static final String certOrgan = "CERT_ORGAN";

        public static final String certStarDate = "CERT_STAR_DATE";

        public static final String certEndDate = "CERT_END_DATE";

        public static final String custName = "CUST_NAME";

        public static final String custPinx = "CUST_PINX";

        public static final String custPinm = "CUST_PINM";

        public static final String custSex = "CUST_SEX";

        public static final String custBorn = "CUST_BORN";

        public static final String custAddrHome = "CUST_ADDR_HOME";

        public static final String custAddrWork = "CUST_ADDR_WORK";

        public static final String custMobiPhone = "CUST_MOBI_PHONE";

        public static final String custTelePhone = "CUST_TELE_PHONE";

        public static final String custNati = "CUST_NATI";

        public static final String custJob = "CUST_JOB";

        public static final String bankKzhy = "BANK_KZHY";

        public static final String bankYbth = "BANK_YBTH";

        public static final String flagStat = "FLAG_STAT";

        public static final String flagPwdEnve = "FLAG_PWD_ENVE";

        public static final String custPost = "CUST_POST";

        public static final String custEmail = "CUST_EMAIL";

        public static final String flagOver = "FLAG_OVER";

        public static final String ensuType = "ENSU_TYPE";

        public static final String ensuNo = "ENSU_NO";

        public static final String overAmt = "OVER_AMT";

        public static final String modiPwdType = "MODI_PWD_TYPE";

        public static final String cardCont = "CARD_CONT";

        public static final String cardTrac2 = "CARD_TRAC2";

        public static final String cardTrac3 = "CARD_TRAC3";

        public static final String voucNo = "VOUC_NO";

        public static final String voucType = "VOUC_TYPE";

        public static final String voucBano = "VOUC_BANO";

        public static final String voucSeno = "VOUC_SENO";

        public static final String flagSignEbank = "FLAG_SIGN_EBANK";

        public static final String flagSignMbank = "FLAG_SIGN_MBANK";

        public static final String flagSignSbank = "FLAG_SIGN_SBANK";

        public static final String flagSignDepo = "FLAG_SIGN_DEPO";

        public static final String flagSignRes1 = "FLAG_SIGN_RES1";

        public static final String flagSignRes2 = "FLAG_SIGN_RES2";

        public static final String tokenType = "TOKEN_TYPE";

        public static final String tokenNo = "TOKEN_NO";

        public static final String signOrgan = "SIGN_ORGAN";

        public static final String signUser = "SIGN_USER";

        public static final String signDeviType = "SIGN_DEVI_TYPE";

        public static final String signDeviNo = "SIGN_DEVI_NO";

        public static final String signTime = "SIGN_TIME";

        public static final String signGps = "SIGN_GPS";

        public static final String signState = "SIGN_STATE";

        public static final String signApprMark = "SIGN_APPR_MARK";

        public static final String decaRes1 = "DECA_RES1";

        public static final String decaRes2 = "DECA_RES2";

        public static final String decaRes3 = "DECA_RES3";
    }
}