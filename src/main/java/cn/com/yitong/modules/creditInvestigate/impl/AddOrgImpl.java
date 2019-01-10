package cn.com.yitong.modules.creditInvestigate.impl;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebParam;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.InputSource;

import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.modules.creditInvestigate.IAddOrg;


public class AddOrgImpl implements IAddOrg {
	@Autowired
	ICrudService iCrudService;
	/**
     * 操作同步组织结构信息
     * @param text
     * @return
     */
    @Override
	public String editOrg(@WebParam(name="arg0")String text){
    	List<Map<String,String>> resultMapList = new ArrayList<Map<String,String>>(); 
    	Map<String,String> resultMap = new HashMap<String,String>(); 
    	try {
    		StringReader read = new StringReader(text);
	    	InputSource source = new InputSource(read);
	    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder db = dbf.newDocumentBuilder();
	    	Document document = null;
	    	SAXReader saxReader = new SAXReader();
	    	try {
				document =saxReader.read(source);
			} catch (DocumentException e1) {
				e1.printStackTrace();
			}
	    	Element rootElm =  document.getRootElement();
	    	Element item = rootElm.element("item");
			Map<String,String> map = new HashMap<String,String>();
			String name     = item.element("name").getText().trim();
			String id    = item.element("id").getText().trim();
			String status    = item.element("status").getText().trim();
			String delFlag    = item.element("delFlag").getText().trim();
			String parentId    = item.element("parentId").getText().trim();
			String grade    = item.element("grade").getText().trim();
			String remarks    = item.element("remarks").getText().trim();
			String orgValid    = item.element("orgValid").getText().trim();
			String qyngrq    = item.element("qyngrq").getText().trim();
			String xnjgbz    = item.element("xnjgbz").getText().trim();
			map.put("NAME", name);
			map.put("ID", id);
			map.put("CODE", id);
			map.put("STATUS", status);
			map.put("DEL_FLAG", delFlag);
			map.put("PARENT_ID", parentId);
			map.put("GRADE", grade);
			map.put("REMARKS", remarks);
			map.put("ORG_VALID", orgValid);
			map.put("QYNGRQ", qyngrq);
			map.put("XNJGBZ", xnjgbz);
			if(map.get("STATUS").equals("1")){
				   map.put("AREA_ID","-");
				   resultMap.put("id", map.get("ID"));
				   try{
					   Map<String,Object> mapParent = iCrudService.load("SYS_USER.getIdsByParentId",map);
					   if(mapParent==null){
						   map.put("PARENT_IDS",parentId+",");
					   }else{
						   map.put("PARENT_IDS", mapParent.get("PARENT_IDS").toString()+mapParent.get("ID").toString()+",");
					   }
					   iCrudService.insert("SYS_USER.insertOrg",map);
					   resultMap.put("status", "0");
				   }catch (Exception e){
					   resultMap.put("status", "1");
				   }
			   }
			   if(map.get("STATUS").equals("2")){
				   map.put("UPDATE_DATE", "yes");
				   resultMap.put("id", map.get("ID"));
				   try{
					   Map<String,Object> mapParent = iCrudService.load("SYS_USER.getIdsByParentId",map);
					   Map<String,Object> myself = iCrudService.load("SYS_USER.getDelFlag1",map);
					   if(mapParent==null){
						   map.put("PARENT_IDS",parentId+",");
					   }else{
						   map.put("PARENT_IDS", mapParent.get("PARENT_IDS").toString()+mapParent.get("ID").toString()+",");
					   }
					   map.put("REV_DEL_FLAG", myself.get("DEL_FLAG").toString());
					   iCrudService.update("SYS_USER.updateOrg",map);
					   resultMap.put("status", "0");
				   }catch (Exception e){
					   resultMap.put("status", "1");
				   }
			   }
			   if(map.get("STATUS").equals("3")){
				   map.put("DEL_FLAG","1");
				   map.put("UPDATE_DATE", "yes");
				   resultMap.put("id", map.get("ID"));
				   try{
					   iCrudService.update("SYS_USER.deletedOrg",map);
					   resultMap.put("status", "0");
				   }catch (Exception e){
					   resultMap.put("status", "1");
				   }
			   }
			   resultMapList.add(resultMap);
			   resultMap = new HashMap<String,String>();
			   map  = new HashMap<String,String>();
			StringBuffer resultMapStr = new StringBuffer("<?xml version='1.0' encoding='UTF-8'?>");
			resultMapStr.append("<Result>");
			for(int i=0;i<resultMapList.size();i++){
				resultMapStr.append("<item>");
				resultMapStr.append("<id>"+resultMapList.get(i).get("id")+"</id>");
				resultMapStr.append("<status>"+resultMapList.get(i).get("status")+"</status>");
				resultMapStr.append("</item>");
			}
			resultMapStr.append("</Result>");
			return resultMapStr.toString();
	   }catch (Exception e) {
		   return e.getMessage();
	   }  
    }

}
