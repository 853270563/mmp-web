package cn.com.yitong.util;

import java.net.MalformedURLException;
import java.util.Map;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

import org.dom4j.DocumentException;

public class JsonXml {

	/**
	 * XML 转成 JSON
	 */
	@SuppressWarnings("unchecked")
	public static String xmlStrToJsonStr(String xmlStr) {
		XMLSerializer xml = new XMLSerializer();
		JSONObject json = (JSONObject) xml.read(xmlStr); 
		return json.toString();
	}

	public static String mapToJsonStr(Map map) {
		JSON json = JSONObject.fromObject(map);
		return json.toString();
	}

	/**
	 * JSON to XML
	 */
	public static String jsonStrToXmlStr(String jsonStr) {
		XMLSerializer xml = new XMLSerializer();
		JSONObject json = JSONObject.fromObject(jsonStr);
		return xml.write(json);
	}
}
