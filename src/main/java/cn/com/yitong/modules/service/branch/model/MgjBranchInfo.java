package cn.com.yitong.modules.service.branch.model;

import cn.com.yitong.common.utils.ConfigUtils;
import cn.com.yitong.common.persistence.BaseEntity;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 网点信息表
 *
 * @author zhanglong@yitong.com.cn
 */
public class MgjBranchInfo extends BaseEntity {

    /**
     * 网点编号
     */
    private String brchId;
    /**
     * 省份编号
     */
    private String provId;
    /**
     * 城市编号
     */
    private String cityId;
    /**
     * 城镇编号
     */
    private String townId;
    /**
     * 网点类型  0-综合网点  1-ATM
     */
    private String brchType;
    /**
     * 网点名称
     */
    private String brchName;
    /**
     * 网点地址
     */
    private String brchAddr;
    /**
     * 对私营业时间
     */
    private String brchServ;
    /**
     * 对公营业时间
     */
    private String brchServ1;
    /**
     * 周末营业时间
     */
    private String brchServ2;
    /**
     * 邮编
     */
    private String brchPost;
    /**
     * 交通路线
     */
    private String brchTraf;
    /**
     * 电话1
     */
    private String brchTel1;
    /**
     * 电话2
     */
    private String brchTel2;
    /**
     * 有效状态  0-无效  1-有效 可查询
     */
    private String validFlag;
    /**
     * 坐标X
     */
    private BigDecimal posX;
    /**
     * 坐标Y
     */
    private BigDecimal posY;
    /**
     * 发布日期
     */
    private Date pubDate;
    /**
     * 编辑人
     */
    private String editorUid;
    /**
     * 编辑日期
     */
    private Date editorDate;
    /**
     * 审核人
     */
    private String chkUid;
    /**
     * 审核日期
     */
    private Date chkDate;
    /**
     * ATM类型
     */
    private String atmType;

    public String getBrchId() {
        return brchId;
    }

    public void setBrchId(String brchId) {
        this.brchId = brchId;
    }
    public String getProvId() {
        return provId;
    }

    public void setProvId(String provId) {
        this.provId = provId;
    }
    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }
    public String getTownId() {
        return townId;
    }

    public void setTownId(String townId) {
        this.townId = townId;
    }
    public String getBrchType() {
        return brchType;
    }

    public void setBrchType(String brchType) {
        this.brchType = brchType;
    }
    public String getBrchName() {
        return brchName;
    }

    public void setBrchName(String brchName) {
        this.brchName = brchName;
    }
    public String getBrchAddr() {
        return brchAddr;
    }

    public void setBrchAddr(String brchAddr) {
        this.brchAddr = brchAddr;
    }
    public String getBrchServ() {
        return brchServ;
    }

    public void setBrchServ(String brchServ) {
        this.brchServ = brchServ;
    }
    public String getBrchServ1() {
        return brchServ1;
    }

    public void setBrchServ1(String brchServ1) {
        this.brchServ1 = brchServ1;
    }
    public String getBrchServ2() {
        return brchServ2;
    }

    public void setBrchServ2(String brchServ2) {
        this.brchServ2 = brchServ2;
    }
    public String getBrchPost() {
        return brchPost;
    }

    public void setBrchPost(String brchPost) {
        this.brchPost = brchPost;
    }
    public String getBrchTraf() {
        return brchTraf;
    }

    public void setBrchTraf(String brchTraf) {
        this.brchTraf = brchTraf;
    }
    public String getBrchTel1() {
        return brchTel1;
    }

    public void setBrchTel1(String brchTel1) {
        this.brchTel1 = brchTel1;
    }
    public String getBrchTel2() {
        return brchTel2;
    }

    public void setBrchTel2(String brchTel2) {
        this.brchTel2 = brchTel2;
    }
    public String getValidFlag() {
        return validFlag;
    }

    public void setValidFlag(String validFlag) {
        this.validFlag = validFlag;
    }
    public BigDecimal getPosX() {
        return posX;
    }

    public void setPosX(BigDecimal posX) {
        this.posX = posX;
    }
    public BigDecimal getPosY() {
        return posY;
    }

    public void setPosY(BigDecimal posY) {
        this.posY = posY;
    }
    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }
    public String getEditorUid() {
        return editorUid;
    }

    public void setEditorUid(String editorUid) {
        this.editorUid = editorUid;
    }
    public Date getEditorDate() {
        return editorDate;
    }

    public void setEditorDate(Date editorDate) {
        this.editorDate = editorDate;
    }
    public String getChkUid() {
        return chkUid;
    }

    public void setChkUid(String chkUid) {
        this.chkUid = chkUid;
    }
    public Date getChkDate() {
        return chkDate;
    }

    public void setChkDate(Date chkDate) {
        this.chkDate = chkDate;
    }
    public String getAtmType() {
        return atmType;
    }

    public void setAtmType(String atmType) {
        this.atmType = atmType;
    }

    public static class TF {

        public static String brchId = "BRCH_ID";  // 网点编号
        public static String provId = "PROV_ID";  // 省份编号
        public static String cityId = "CITY_ID";  // 城市编号
        public static String townId = "TOWN_ID";  // 城镇编号
        public static String brchType = "BRCH_TYPE";  // 网点类型  0-综合网点  1-ATM
        public static String brchName = "BRCH_NAME";  // 网点名称
        public static String brchAddr = "BRCH_ADDR";  // 网点地址
        public static String brchServ = "BRCH_SERV";  // 对私营业时间
        public static String brchServ1 = "BRCH_SERV1";  // 对公营业时间
        public static String brchServ2 = "BRCH_SERV2";  // 周末营业时间
        public static String brchPost = "BRCH_POST";  // 邮编
        public static String brchTraf = "BRCH_TRAF";  // 交通路线
        public static String brchTel1 = "BRCH_TEL1";  // 电话1
        public static String brchTel2 = "BRCH_TEL2";  // 电话2
        public static String validFlag = "VALID_FLAG";  // 有效状态  0-无效  1-有效 可查询
        public static String posX = "POS_X";  // 坐标X
        public static String posY = "POS_Y";  // 坐标Y
        public static String pubDate = "PUB_DATE";  // 发布日期
        public static String editorUid = "EDITOR_UID";  // 编辑人
        public static String editorDate = "EDITOR_DATE";  // 编辑日期
        public static String chkUid = "CHK_UID";  // 审核人
        public static String chkDate = "CHK_DATE";  // 审核日期
        public static String atmType = "ATM_TYPE";  // ATM类型

    }
}
