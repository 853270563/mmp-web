package cn.com.yitong.modules.hg;

import cn.com.yitong.common.persistence.BaseEntity;

/**
 * 区域编码表
 */
public class AreaCodeData extends BaseEntity {

	/**
	 * 区域id(编码)
	 */
	private String areaId;
	/**
	 * 区域名称
	 */
	private String areaName;
	/**
	 * 上级区域id
	 */
	private String parentId;
	
	
	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public static class TF {

		public static String TABLE_NAME = "ARES_AREA_CODE"; // 表名

		public static String TABLE_SCHEMA = "hbyh"; // 库名

		public static String areaId = "AREA_ID"; // 区域id(编码)
		public static String areaName = "AREA_NAME"; // 区域名称
		public static String parentId = "PARENT_ID"; // 上级区域id
	}
}
