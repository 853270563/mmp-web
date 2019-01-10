package cn.com.yitong.framework.util.webservice;


import java.io.File;     
import java.io.FileWriter;     
import java.io.IOException;     
import java.io.Writer;     
import java.util.Iterator;     


import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;     
import org.dom4j.DocumentException;     
import org.dom4j.DocumentHelper;     
import org.dom4j.Element;     
import org.dom4j.io.SAXReader;     	
import org.dom4j.io.XMLWriter;     
/**   
 *    
 * Dom4j 生成XML文档与解析XML文档   
 */    
public class demo4J implements XMLdocument {     
	public void parserXml(String fileName) {     
		/*File inputXml=new File(fileName);     
		SAXReader saxReader = new SAXReader();     
		try {     
			Document document = saxReader.read(inputXml);     
			Element rootELe=document.getRootElement(); //获取根节点    
			for(Iterator i = rootELe.elementIterator(); i.hasNext();){     
				Element employee = (Element) i.next(); 
				System.out.println(employee.getText()+employee+"11111");
				for(Iterator j = employee.elementIterator(); j.hasNext();){     
					Element node=(Element) j.next();  
					System.out.println(node.getName()+":"+node.getText()+node.getTextTrim());     
				}     
			}     
		} catch (DocumentException e) {     
			System.out.println(e.getMessage());     
		}     
		System.out.println(fileName); */    
	}  


	public void testGetRoot(String file) throws Exception{  
		SAXReader sax=new SAXReader();//创建一个SAXReader对象  
		File xmlFile=new File(file);//根据指定的路径创建file对象  
		Document document=sax.read(xmlFile);//获取document对象,如果文档无节点，则会抛出Exception提前结束  
		Element root=document.getRootElement();//获取根节点  
		this.getNodes(root);//从根节点开始遍历所有节点     
	}  

	public void getNodes(Element node){  
		System.out.println("--------------------");    
		//当前节点的名称、文本内容和属性  
		System.out.println("当前节点名称："+node.getName());//当前节点名称  
		System.out.println("当前节点的内容："+node.getTextTrim());//当前节点名称  
		List<Attribute> listAttr=node.attributes();//当前节点的所有属性的list  
		for(Attribute attr:listAttr){//遍历当前节点的所有属性  
			String name=attr.getName();//属性名称  
			String value=attr.getValue();//属性的值  
			if(attr.getName().equals("CONTENT_ID")){
				System.out.println("第二次找的"+"属性名称："+name+"属性值："+value);
			}
			System.out.println("属性名称："+name+"属性值："+value);  
		}    
		//递归遍历当前节点所有的子节点  
		List<Element> listElement=node.elements();//所有一级子节点的list  
		for(Element e:listElement){//遍历所有一级子节点 
			if(e.getName().equals("BatchIndexBean")){
				this.getNodes(e);
				System.out.println("第二次查找");
			}
			this.getNodes(e);//递归  
		}  
	}  


	public static void main(String[] args) throws Exception {
		demo4J text=new demo4J();
		//text.testGetRoot("D:"+File.separator+"烟台银行材料包"+File.separator+"报文"+File.separator+"heightQueryExample()返回的报文.xml");
		text.testGetRoot("D:"+File.separator+"烟台银行材料包"+File.separator+"报文"+File.separator+"queryExample()返回的报文.xml");
		//text.parserXml("D:"+File.separator+"烟台银行材料包"+File.separator+"报文"+File.separator+"heightQueryExample()返回的报文.xml");
	}
}     
