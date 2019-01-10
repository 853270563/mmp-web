package cn.com.yitong.modules.mobileCrm;


import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

public class MobileCrmParseReviceXml {
	@SuppressWarnings("unchecked")
	public static Map<String,Object> parseResultJson(Map map,String xml){
		StringReader read = new StringReader(xml);
		InputSource source = new InputSource(read);
		SAXReader saxReader = new SAXReader();
		Document document = null;
		try {
			document =saxReader.read(source);
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
		Element rootElm =  document.getRootElement();
		String status    = rootElm.element("commDomain").element("transRespCode").getText();//成功标志
		Element servicbody= rootElm.element("body");
		String transRespDesc= rootElm.element("commDomain").element("transRespDesc").getText();//
		String serviceHeader = rootElm.element("sysHead").element("senderSrvName").getText();
		System.out.println(status+":status  这是状态码--------------"+transRespDesc+serviceHeader);
		List<Map<String,String>> resultList = new ArrayList<Map<String,String>>();
		if(status.equals("0000")){
			List<Element> responseList =servicbody.elements("kpidata");
			for(Element e : responseList){
				Map<String,String> newMap = new HashMap<String,String>();
				newMap.put("kpiName",e.element("kpiName").getText());
				newMap.put("kpiNo",e.element("kpiNo").getText());
				newMap.put("kpiValue",e.element("kpiValue").getText());
				newMap.put("kpiValueld",e.element("kpiValueld").getText());
				newMap.put("kpiValuelm",e.element("kpiValuelm").getText());
				newMap.put("kpiValuely",e.element("kpiValuely").getText());
				resultList.add(newMap);
			}			
		}
		map.put("LIST", resultList);
		return map;
	}
}
