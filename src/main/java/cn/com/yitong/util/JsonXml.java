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
	 * 
	 * @param xmlStr
	 * @return
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
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static String jsonStrToXmlStr(String jsonStr) {
		XMLSerializer xml = new XMLSerializer();
		JSONObject json = JSONObject.fromObject(jsonStr);
		return xml.write(json);
	}

	/**
	 * 测试
	 * 
	 * @param args
	 * @throws org.dom4j.DocumentException
	 * @throws java.net.MalformedURLException
	 */
	public static void main(String[] args) throws MalformedURLException,
			DocumentException {
		// String file =
		// "D://works/cmbc_space/MobileAppServer/src/data/jsnx/ECIP00001000001.xml";
		// String xmlStr = XmlUtil.readFile(file).getRootElement().asXML();
		// System.out.println(xmlStr);
		//
		// String jsonStr = xmlStrToJsonStr(xmlStr);
		// System.out.println(jsonStr);
		// xmlStr = jsonStrToXmlStr(jsonStr);
		// System.out.println(xmlStr);
		String xml = "{'port':null,'dse_operationName':'IPP0502Op','encryptAccount':'4155990133495959','dse_ErrorFlag':false,'dse_parentContextName':null,'userId':'90190019','TransId':'IPP0502Op','encryptPassword':'\u00a9\u00ea\u00dajIt\b\0','dse_errorMessages':null,'custId':null,'dse_errorPage':'','respData':{'message':'\u8c03\u7528\u52a0\u5bc6\u8bbe\u5907\u5931\u8d25','code':'EVC9000'},'dse_replyPage':null,'dse_processorId':null,'dse_applicationId':'-1','ip':null,'dse_pageId':'-1','passwordVerify':'2','organNo':'9019','ebank_exitEventName':'ok','sequence':'1098871','dse_sessionId':'D826C60670A855B114A68142D959E64C','accountType':'1','btt_isOperation':true,'dse_processorState':null}";
		System.out.println(jsonStrToXmlStr(xml));
	}

}
