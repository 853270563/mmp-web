package cn.com.yitong.util;

import net.sf.json.JSON;
import net.sf.json.xml.XMLSerializer;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.SAXValidator;
import org.dom4j.io.XMLWriter;
import org.dom4j.util.XMLErrorHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;

public class XmlUtil {

	private static Logger logger = LoggerFactory.getLogger(XmlUtil.class);

	public static String readXml(String fileName) throws MalformedURLException, DocumentException {
		return readFile(fileName).asXML();
	}

	public static Document readFile(String fileName) throws MalformedURLException, DocumentException {
		SAXReader reader = new SAXReader();
		File file = new File(fileName);
		System.out.println(file.getAbsolutePath());
		return reader.read(new File(fileName));
	}

	public static Document readText(String text) throws MalformedURLException, DocumentException {
		return DocumentHelper.parseText(text);
	}

	public static String formatXml(String xml) {
		if (null != xml) {
			return xml;
		}
		XMLWriter writer = null;
		String output = null;
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			writer = new XMLWriter(format);
			writer.write(readText(xml));
			output = writer.toString();
			writer.close();
			writer = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		}
		return output;
	}

	/**
	 * XML 转成 JSON
	 */
	public static String xmlStrToJsonStr(String xmlStr) {
		XMLSerializer xml = new XMLSerializer();
		JSON json = xml.read(xmlStr);
		return json.toString();
	}

	/**
	 * JSON 转入 XML
	 */
	public static void jsonStrToXmlStr(String jsonStr, Element parent) {
		try {
			JSONObject json = new JSONObject(jsonStr);
			jsonToElement(json, parent);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void jsonToElement(JSONObject json, Element parent) throws JSONException {
		if (json == null || parent == null) {
			return;
		}
		for (Iterator iter = json.keys(); iter.hasNext();) {
			String key = (String) iter.next();
			Object item = json.get(key);
			Element childElem = parent.addElement(key);
			if (item instanceof JSONObject) {
				jsonToElement((JSONObject) item, childElem);
			} else if (item instanceof JSONArray) {
				jsonToElement((JSONArray) item, "map", childElem);
			} else {
				childElem.setText(item.toString());
			}
		}
	}

	public static void jsonToElement(JSONArray jsonArray, String key, Element parent) throws JSONException {
		if (jsonArray == null || parent == null) {
			return;
		}
		for (int i = 0, length = jsonArray.length(); i < length; i++) {
			Object item = (Object) jsonArray.get(i);
			Element childElem = parent.addElement(key);
			if (item instanceof JSONObject) {
				jsonToElement((JSONObject) item, childElem);
			} else if (item instanceof JSONArray) {
				jsonToElement((JSONArray) item, "map", childElem);
			} else {
				childElem.setText(item.toString());
			}
		}
	}

	/**
	 * 通过XSD（XML Schema）校验XML
	 */
	public static boolean validateXMLByXSD(Document xmlDocument, String xsdFileName) {
		// String xsdFileName = "Q:\\_dev_stu\\xsdtest\\src\\note.xsd";
		try {
			// 创建默认的XML错误处理器
			XMLErrorHandler errorHandler = new XMLErrorHandler();
			// 获取基于 SAX 的解析器的实例
			SAXParserFactory factory = SAXParserFactory.newInstance();
			// 解析器在解析时验证 XML 内容。
			factory.setValidating(true);
			// 指定由此代码生成的解析器将提供对 XML 名称空间的支持。
			factory.setNamespaceAware(true);
			// 使用当前配置的工厂参数创建 SAXParser 的一个新实例。
			SAXParser parser = factory.newSAXParser();
			// 创建一个读取工具
			// 设置 XMLReader 的基础实现中的特定属性。核心功能和属性列表可以在
			// [url]http://sax.sourceforge.net/?selected=get-set[/url] 中找到。
			parser.setProperty(
					"http://java.sun.com/xml/jaxp/properties/schemaLanguage",
					"http://www.w3.org/2001/XMLSchema");
			parser.setProperty(
					"http://java.sun.com/xml/jaxp/properties/schemaSource",
					"file:" + xsdFileName);
			// 创建一个SAXValidator校验工具，并设置校验工具的属性
			SAXValidator validator = new SAXValidator(parser.getXMLReader());
			// 设置校验工具的错误处理器，当发生错误时，可以从处理器对象中得到错误信息。
			validator.setErrorHandler(errorHandler);
			// 校验
			validator.validate(xmlDocument);
			if (errorHandler.getErrors().hasContent()) {
				logger.error("XML文件通过XSD文件校验失败！\n", errorHandler.getErrors().asXML());
				return false;
			} else {
				logger.debug("Good! XML文件通过XSD文件校验成功！");
			}
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
}