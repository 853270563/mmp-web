package cn.com.yitong.modules.hg;

import java.math.BigDecimal;
import java.util.Date;

import cn.com.yitong.common.persistence.BaseEntity;

/**
 * 广告管理表
 *
 * @author winkie@yitong.com.cn
 */
public class MzjBannerData extends BaseEntity {

	/**
	 * 广告编号
	 */
	private String bannerId;
	/**
	 * 广告封面
	 */
	private String bannerPic;
	/**
	 * 文件编号
	 */
	private String fileId;
	/**
	 * 广告名称
	 */
	private String bannerName;
	/**
	 * 所属机构
	 */
	private String organLimit;
	/**
	 * 广告等级 数据字典取值：LVL_TYPE
	 */
	private String bannerLvl;
	/**
	 * 排序位置
	 */
	private BigDecimal bannerOrde;
	/**
	 * 有效时间
	 */
	private Date vaildDate;
	/**
	 * 编辑人
	 */
	private String editor;
	/**
	 * 编辑时间
	 */
	private Date editorTime;
	/**
	 * 状态 1：有效 0：无效
	 */
	private String status;
	/**
	 * 0：未删除 1：删除
	 */
	private String delFlag;

	private String organName;

	private String fileAddr;

	private String bannerPicAddr;

	private String editorName;
	/**
	 * 广告封面类型
	 */
	private String bannerPicType;
	/**
	 * 资源类型
	 */
	private String fileType;
	/**
	 * 广告说明
	 */
	private String summary;

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getBannerPicType() {
		return bannerPicType;
	}

	public void setBannerPicType(String bannerPicType) {
		this.bannerPicType = bannerPicType;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getBannerId() {
		return bannerId;
	}

	public void setBannerId(String bannerId) {
		this.bannerId = bannerId;
	}

	public String getBannerPic() {
		return bannerPic;
	}

	public void setBannerPic(String bannerPic) {
		this.bannerPic = bannerPic;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getBannerName() {
		return bannerName;
	}

	public void setBannerName(String bannerName) {
		this.bannerName = bannerName;
	}

	public String getOrganLimit() {
		return organLimit;
	}

	public void setOrganLimit(String organLimit) {
		this.organLimit = organLimit;
	}

	public String getBannerLvl() {
		return bannerLvl;
	}

	public void setBannerLvl(String bannerLvl) {
		this.bannerLvl = bannerLvl;
	}

	public BigDecimal getBannerOrde() {
		return bannerOrde;
	}

	public void setBannerOrde(BigDecimal bannerOrde) {
		this.bannerOrde = bannerOrde;
	}

	public Date getVaildDate() {
		return vaildDate;
	}

	public void setVaildDate(Date vaildDate) {
		this.vaildDate = vaildDate;
	}

	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	public Date getEditorTime() {
		return editorTime;
	}

	public void setEditorTime(Date editorTime) {
		this.editorTime = editorTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	public String getOrganName() {
		return organName;
	}

	public void setOrganName(String organName) {
		this.organName = organName;
	}

	public String getFileAddr() {
		return fileAddr;
	}

	public void setFileAddr(String fileAddr) {
		this.fileAddr = fileAddr;
	}

	public String getBannerPicAddr() {
		return bannerPicAddr;
	}

	public void setBannerPicAddr(String bannerPicAddr) {
		this.bannerPicAddr = bannerPicAddr;
	}

	public String getEditorName() {
		return editorName;
	}

	public void setEditorName(String editorName) {
		this.editorName = editorName;
	}

	public static class TF {

		public static String TABLE_NAME = "MZJ_BANNER_DATA"; // 表名

		public static String TABLE_SCHEMA = "ARESNEW"; // 库名

		public static String bannerId = "BANNER_ID"; // 广告编号
		public static String bannerPic = "BANNER_PIC"; // 广告封面
		public static String fileId = "FILE_ID"; // 文件编号
		public static String bannerName = "BANNER_NAME"; // 广告名称
		public static String organLimit = "ORGAN_LIMIT"; // 所属机构
		public static String bannerLvl = "BANNER_LVL"; // 广告等级 数据字典取值：LVL_TYPE
		public static String bannerOrde = "BANNER_ORDE"; // 排序位置
		public static String vaildDate = "VAILD_DATE"; // 有效时间
		public static String editor = "EDITOR"; // 编辑人
		public static String editorTime = "EDITOR_TIME"; // 编辑时间
		public static String status = "STATUS"; // 状态 1：有效 0：无效
		public static String delFlag = "DEL_FLAG"; // 0：未删除 1：删除
		public static String fileType = "FILE_TYPE"; // 广告资源类型
		public static String bannerPicType = "BANNER_PIC_TYPE"; // 广告资源类型
		public static String summary = "SUMMARY"; // 广告说明

	}
}
