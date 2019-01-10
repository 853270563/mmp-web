package cn.com.yitong.core.model;

import java.io.Serializable;

/**
 * @author zhuzengpeng<zzp@yitong.com.cn>
 */
public class SysDict implements Serializable{

	private static final long serialVersionUID = 1409512454276970734L;
	private String type;
	private String value;
	private String label;
	private String description;
	private String sort;

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
