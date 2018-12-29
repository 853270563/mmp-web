package cn.com.yitong.framework.core.vo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置节点
 * 
 * @author yaoym
 * 
 */
public class MBTransItem {
	private String name;
	private String targetName;
	private String type;
	private boolean requred;
	private String desc;
	private String xmlPath;
	private String client;
	// 第一级 分隔符
	private String delimiter;
	// 第二级分隔符
	private String itemDelimiter;

	private int length;// 内容长度
	private int dolt;// 小数位数
	// 别名：用于定义ALISE
	private String aliseName;
	// 用于定义SEQ的TYPE.
	private String seqType;
	// MAP类型
	private String childType;
	// 数据字典类型
	private String mapKey;
	// 解析字段名称，不能重复
	private String descName;
	// 缺省功能
	private String defaultValue;
	// 变长数据的指定长度的字段
	private String exchange;
	// 扩展字段
	private String plugin;
	
	private String sizeField;//hl add 请求list长度
	/**
	 * 属性字段
	 */
	private Map<String, String> properties = new HashMap<String, String>();
	
	public String getSizeField() {
		return sizeField;
	}
	
	public void setSizeField(String sizeField) {
		this.sizeField = sizeField;
	}	

	private List<MBTransItem> children;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public boolean isRequred() {
		return requred;
	}

	public void setRequred(boolean requred) {
		this.requred = requred;
	}

	public List<MBTransItem> getChildren() {
		return children;
	}

	public void setChildren(List<MBTransItem> map) {
		this.children = map;
	}

	public String getMapKey() {
		return mapKey;
	}

	public void setMapKey(String mapKey) {
		this.mapKey = mapKey;
	}

	public String getDescName() {
		return descName;
	}

	public void setDescName(String descName) {
		this.descName = descName;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getChildType() {
		return childType;
	}

	public void setChildType(String childType) {
		this.childType = childType;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getXmlPath() {
		return xmlPath;
	}

	public void setXmlPath(String xmlPath) {
		this.xmlPath = xmlPath;
	}

	public String getAliseName() {
		return aliseName;
	}

	public void setAliseName(String aliseName) {
		this.aliseName = aliseName;
	}

	public String getPlugin() {
		return plugin;
	}

	public void setPlugin(String plugin) {
		this.plugin = plugin;
	}

	public String getSeqType() {
		return seqType;
	}

	public void setSeqType(String seqType) {
		this.seqType = seqType;
	}

	public int getDolt() {
		return dolt;
	}

	public void setDolt(int dolt) {
		this.dolt = dolt;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getItemDelimiter() {
		return itemDelimiter;
	}

	public void setItemDelimiter(String itemDelimiter) {
		this.itemDelimiter = itemDelimiter;
	}
	
	public Map<String, String> getProperties() {
		return properties;
	}

	public void setPropery(String key, String value) {
		properties.put(key, value);
	}

	public String getProperty(String key) {
		return properties.get(key);
	}
	
	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	@Override
	public String toString() {
		StringBuffer bf = new StringBuffer();
		bf.append("[name:").append(name);
		bf.append(",\t targetName:").append(targetName);
		bf.append(",\t type:").append(type);
		bf.append(",\t desc:").append(desc);
		bf.append(",\t client:").append(client);
		bf.append(",\t length:").append(length);
		bf.append(",\t dolt:").append(dolt);
		bf.append(",\t required:").append(requred);
		bf.append(",\t default:").append(defaultValue);
		bf.append(",\t mapKey:").append(mapKey);
		bf.append(",\t descName:").append(descName);
		bf.append(",\t xpath:").append(xmlPath);
		bf.append(",\t plugin:").append(plugin);
		bf.append(",\t aliseName:").append(aliseName);
		bf.append(",\t seqType:").append(seqType);
		bf.append("]");
		return bf.toString();
	}

}