package cn.com.yitong.modules.creditInvestigate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.jcraft.jsch.Logger;

import cn.com.yitong.consts.AppConstants;

public class ParseYxXmlUtil {
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, DocumentException {
//		File file = new File("G:"+File.separator+"heightQueryExample()返回的报文.txt");
		File file = new File("G:"+File.separator+"queryExample()返回的报文.txt");
		StringReader read = null;
		InputSource source = null;
		try {
			FileInputStream input = new FileInputStream(file);
			String str ="";
			byte[] byt = new byte[1024];
			int i;
			while((i = input.read(byt)) > -1){
				str = new String(byt,0,i);
				System.out.println(str);
			}
			
			read = new StringReader(str);
	    	source = new InputSource(read);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder db = dbf.newDocumentBuilder();
	    	SAXReader saxReader = new SAXReader();  
	    	Document document;
			document =saxReader.read(new FileInputStream(file),"UTF-8");
			//heightQueryExample()返回的报文
//			Element rootElm =  document.getRootElement();
//			Element heightQueryElm = rootElm.element("HeightQuery");
//			Element customAttElm = heightQueryElm.element("customAtt");
//			Map<String, Object> map = new HashMap<String, Object>();
//		    map.put("busiStartDate",customAttElm.element("BUSI_START_DATE").element("string").getText());
//		    map.put("busiSerialNo",customAttElm.element("BUSI_SERIAL_NO").element("string").getText());
//		    map.put("groupId",heightQueryElm.element("groupId").getText());
//		    Element indexBeansElm =  heightQueryElm.element("indexBeans");
//		    Element batchIndexBeanElm =  indexBeansElm.element("BatchIndexBean");
//		    String contentId = batchIndexBeanElm.attribute("CONTENT_ID").getText();
//		    map.put("contentId", contentId);
//		    Element customMapElm = batchIndexBeanElm.element("customMap");
//		    map.put("amount",customMapElm.element("AMOUNT").element("string").getText());
//		    for(Entry<String, Object> entry : map.entrySet()){
//		    	System.out.println(entry.getValue());
//		    }
			//queryExample()返回的报文
			Element rootElm =  document.getRootElement();
			Element filesElm = rootElm.element("BatchBean").element("document_Objects").element("BatchFileBean").element("files");
			List<Element> fileBean = filesElm.elements("FileBean");
			Map<String,Object> fileBeanMap = new HashMap<String,Object>();
			for(int y=0;y<fileBean.size();y++){
				String fileNameString = fileBean.get(y).attribute("FILE_NAME").getText();
				String fileFormatString = fileBean.get(y).attribute("FILE_FORMAT").getText();
				String fileNoString = fileBean.get(y).attribute("FILE_NO").getText();
				String fileString =fileNameString+fileFormatString;
				String codeString = fileBean.get(y).element("otherAtt").element("BUSI_FILE_TYPE").element("string").getText();
				List<String> urlList = new ArrayList<String>();
				//通过sftp把影像下载到本地
//				SftpUtil handler = new SftpUtil(AppConstants.SFTP_HOST, AppConstants.SFTP_PORT, AppConstants.SFTP_USER, AppConstants.SFTP_PW);
//				handler.connect();
//		    	String result = handler.download(fileString, codeString);
//		    	handler.disconnect();
				//result 就是 /download/userResource/file.do?file=fileNoString+"."+fileFormatString
				if(y==0){
					urlList.add(fileString);
					fileBeanMap.put(codeString,urlList);
				}else{
					Iterator<Entry<String, Object>> it = fileBeanMap.entrySet().iterator();
					boolean flag =true;
					while(it.hasNext()){ 
						Entry<String, Object> itEntry = it.next(); 
						if(itEntry.getKey().equals(codeString)){
							flag =false;
							List<String> newList =  (List<String>)fileBeanMap.get(codeString);
							newList.add(fileString);
							fileBeanMap.put(codeString,newList);
						}
					}
					if(flag){
						urlList.add(fileString);
						fileBeanMap.put(codeString,urlList);
					}
				}
			}
			List<String> lis11t22 = (List<String>)fileBeanMap.get("011009_998");
			for(int yy=0;yy<lis11t22.size();yy++){
				System.out.println(lis11t22.get(yy));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				read.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
}
