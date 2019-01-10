package cn.com.yitong.framework.core.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.yitong.framework.core.vo.MBTransItem;

/**
 * 通信报文格式化解析Bean
 * 
 * @author yaoym
 * 
 */
public class MBTransConfBean {

	private String name;
	private String excode;

	/**
	 * 属性字段
	 */
	private Map<String, String> properties = new HashMap<String, String>();

	/**
	 * 请求报文头
	 */
	private List<MBTransItem> sedHeader = new ArrayList();
	/**
	 * 请求报文体
	 */
	private List<MBTransItem> sed = new ArrayList();
	/**
	 * 响应报文头
	 */
	private List<MBTransItem> rcvHeader = new ArrayList();
	/**
	 * 响应报文体
	 */
	private List<MBTransItem> rcv = new ArrayList();

	public List<MBTransItem> getSed() {
		return sed;
	}

	public void setSed(List<MBTransItem> sed) {
		this.sed = sed;
	}

	public List<MBTransItem> getRcv() {
		return rcv;
	}

	public List getSendHeader() {
		return sedHeader;
	}

	public List getrcvHeader() {
		return rcvHeader;
	}

	public void setRcv(List<MBTransItem> rcv) {
		this.rcv = rcv;
	}

	public void addSedItem(MBTransItem item) {
		sed.add(item);
	}

	public void addRcvItem(MBTransItem item) {
		rcv.add(item);
	}

	public void addSendHeaderItem(MBTransItem item) {
		sedHeader.add(item);
	}

	public void addRcvHeaderItem(MBTransItem item) {
		rcvHeader.add(item);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExcode() {
		return excode;
	}

	public void setExcode(String excode) {
		this.excode = excode;
	}

	public List<MBTransItem> getSedHeader() {
		return sedHeader;
	}

	public void setSedHeader(List<MBTransItem> sedHeader) {
		this.sedHeader = sedHeader;
	}

	public List<MBTransItem> getRcvHeader() {
		return rcvHeader;
	}

	public void setRcvHeader(List<MBTransItem> rcvHeader) {
		this.rcvHeader = rcvHeader;
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

	public String toString() {
		StringBuffer bf = new StringBuffer();
		bf.append("name:").append(this.name).append(",properties=");
		bf.append(this.properties.toString());
		return bf.toString();
	}
}