package cn.com.yitong.modules.hg;

import java.util.Date;

import cn.com.yitong.common.persistence.BaseEntity;

/**
 * 档案管理表
 *
 * @author huangry@yitong.com.cn
 */
public class MzjPropaData extends BaseEntity {

	/**
	 * 档案编号
	 */
	private String propaId;
	/**
	 * 关联资源编号
	 */
	private String fileId;
	/**
	 * 档案名称
	 */
	private String propaName;
	/**
	 * 档案类型
	 */
	private String propaType;
	/**
	 * 封面
	 */
	private String propaPic;
	/**
	 * 重要等级
	 */
	private String propaLvl;
	/**
	 * 排序位置
	 */
	private int propaOrde;
	/**
	 * 有效时间
	 */
	private Date vaildDate;
	/**
	 * 所属机构
	 */
	private String organLimit;
	/**
	 * 编辑人
	 */
	private String editor;
	/**
	 * 编辑时间
	 */
	private Date editorTime;
	/**
	 * 审核编号
	 */
	private String audiId;
	/**
	 * 状态
	 */
	private String status;
	/**
	 * 档案业务类型
	 */
	private String proBusiType;

	private String organName;

	private String fileAddr;

	private String propaPicAddr;
	/**
	 * 展架封面类型
	 */
	private String propaPicType;

	/**
	 * 资源类型
	 */
	private String fileType;
	/**
	 * 资源说明
	 */
	private String summary;

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getPropaPicType() {
		return propaPicType;
	}

	public void setPropaPicType(String propaPicType) {
		this.propaPicType = propaPicType;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
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

	public String getPropaPicAddr() {
		return propaPicAddr;
	}

	public void setPropaPicAddr(String propaPicAddr) {
		this.propaPicAddr = propaPicAddr;
	}

	public String getPropaId() {
		return propaId;
	}

	public void setPropaId(String propaId) {
		this.propaId = propaId;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getPropaName() {
		return propaName;
	}

	public void setPropaName(String propaName) {
		this.propaName = propaName;
	}

	public String getPropaType() {
		return propaType;
	}

	public void setPropaType(String propaType) {
		this.propaType = propaType;
	}

	public String getPropaPic() {
		return propaPic;
	}

	public void setPropaPic(String propaPic) {
		this.propaPic = propaPic;
	}

	public String getPropaLvl() {
		return propaLvl;
	}

	public void setPropaLvl(String propaLvl) {
		this.propaLvl = propaLvl;
	}

	public int getPropaOrde() {
		return propaOrde;
	}

	public void setPropaOrde(int propaOrde) {
		this.propaOrde = propaOrde;
	}

	public Date getVaildDate() {
		return vaildDate;
	}

	public void setVaildDate(Date vaildDate) {
		this.vaildDate = vaildDate;
	}

	public String getOrganLimit() {
		return organLimit;
	}

	public void setOrganLimit(String organLimit) {
		this.organLimit = organLimit;
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

	public String getAudiId() {
		return audiId;
	}

	public void setAudiId(String audiId) {
		this.audiId = audiId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProBusiType() {
		return proBusiType;
	}

	public void setProBusiType(String proBusiType) {
		this.proBusiType = proBusiType;
	}

	public static class TF {

		public static String TABLE_NAME = "MZJ_PROPA_DATA"; // 表名

		public static String TABLE_SCHEMA = "ARESNEW"; // 库名

		public static String propaId = "PROPA_ID"; // 档案编号
		public static String fileId = "FILE_ID"; // 关联资源编号
		public static String propaName = "PROPA_NAME"; // 档案名称
		public static String propaType = "PROPA_TYPE"; // 档案类型
		public static String propaPic = "PROPA_PIC"; // 封面
		public static String propaLvl = "PROPA_LVL"; // 重要等级
		public static String propaOrde = "PROPA_ORDE"; // 排序位置
		public static String vaildDate = "VAILD_DATE"; // 有效时间
		public static String organLimit = "ORGAN_LIMIT"; // 所属机构
		public static String editor = "EDITOR"; // 编辑人
		public static String editorTime = "EDITOR_TIME"; // 编辑时间
		public static String status = "STATUS"; // 状态
		public static String proBusiType = "PRO_BUSI_TYPE"; // 档案业务类型
		public static String summary= "SUMMARY"; // 档案说明

	}
}
