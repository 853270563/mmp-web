package cn.com.yitong.util.common;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.SAXValidator;
import org.dom4j.io.XMLWriter;
import org.dom4j.util.XMLErrorHandler;

public class XmlUtil {
	public static String readXml(String fileName) throws MalformedURLException,
			DocumentException {
		return readFile(fileName).asXML();
	}

	public static Document readFile(String fileName)
			throws MalformedURLException, DocumentException {
		SAXReader reader = new SAXReader();
		File file = new File(fileName);
		return reader.read(file);
	}

	public static Document readText(String text) throws MalformedURLException,
			DocumentException {
		return DocumentHelper.parseText(text);
	}

	/**
	 * 格式化xml数据
	 * 
	 * @param stringWriter
	 * @param doc
	 * @throws IOException
	 */
	public static void formateXMLStr(Writer stringWriter, Element elem)
			throws IOException {
		OutputFormat of = new OutputFormat();
		of.setIndent(true);
		of.setNewlines(true);
		XMLWriter xmlWriter = new XMLWriter(stringWriter, of);
		xmlWriter.write(elem);
		xmlWriter.close();
	}

	/**
	 * 格式化xml数据
	 * 
	 * @param xmlStr
	 * @return
	 */
	public static String formatXmlStr(String xmlStr) {
		return formatXmlStr(xmlStr, "utf-8");
	}

	/**
	 * 格式化xml数据
	 * 
	 * @param stringWriter
	 * @param doc
	 * @throws IOException
	 */
	public static String formatXmlStr(String xmlStr, String encoding) {
		Document doc;
		try {
			doc = DocumentHelper.parseText(xmlStr);
		} catch (DocumentException e) {
			e.printStackTrace();
			return xmlStr;
		}
		doc.setXMLEncoding(encoding);
		Writer stringWriter = new StringWriter();
		OutputFormat of = new OutputFormat();
		of.setIndent(true);
		of.setNewlines(true);
		XMLWriter xmlWriter = new XMLWriter(stringWriter, of);
		try {
			xmlWriter.write(doc);
			xmlWriter.close();
		} catch (IOException e) {

			e.printStackTrace();
			return xmlStr;
		}
		return stringWriter.toString();
	}

	/**
	 * 通过XSD（XML Schema）校验XML
	 */
	public static boolean validateXMLByXSD(Document xmlDocument,
			String xsdFileName) {
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
				System.out.println("XML文件通过XSD文件校验失败！\n"
						+ errorHandler.getErrors().asXML());
				return false;
			} else {
				System.out.println("Good! XML文件通过XSD文件校验成功！");
			}
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public static void main(String[] args) throws MalformedURLException,
			DocumentException {
		String xmlfile = "D:/works/tfb_space/ibanking/WebContent/WEB-INF/data/xsd/cancleUser.xml";
		String xsdfile = "D:/works/tfb_space/ibanking/WebContent/WEB-INF/conf/xsd/cancleUser.xsd";

		Document doc = readFile(xmlfile);
		System.out.println("document is :\t\n" + doc.asXML());
		validateXMLByXSD(doc, xsdfile);

	}
}