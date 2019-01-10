package cn.com.yitong.ares.appManage.service;

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.File;
import java.io.FileOutputStream;

public class CreatePlist {
	/**
	 * 自动生成plist文件方法
	 * 
	 * @param resFileDownLoadUrl 资源文件下载URL
	 * @return plist文件
	 * @throws java.io.IOException
	 */
	public static File createPlist(String resFileDownLoadUrl, String plistFilePath) throws Exception {
		// 创建文档类型
		DocType docType = new DocType("plist");
		docType.setPublicID("-//Apple//DTD PLIST 1.0//EN");
		docType.setSystemID("http://www.apple.com/DTDs/PropertyList-1.0.dtd");
		// 创建根节点 plist
		Element root = new Element("plist");
		root.setAttribute("version", "1.0");
		//
		Element rootDict = new Element("dict");
		rootDict.addContent(new Element("key").setText("items"));
		Element rootDictArray = new Element("array");
		Element rootDictArrayDict = new Element("dict");
		rootDictArrayDict.addContent(new Element("key").setText("assets"));

		Element rootDictArrayDictArray1 = new Element("array");
		Element rootDictArrayDictArrayDict1 = new Element("dict");
		rootDictArrayDictArrayDict1.addContent(new Element("key").setText("kind"));
		rootDictArrayDictArrayDict1.addContent(new Element("string").setText("software-package"));
		rootDictArrayDictArrayDict1.addContent(new Element("key").setText("url"));
		rootDictArrayDictArrayDict1.addContent(new Element("string").setText(resFileDownLoadUrl));
		rootDictArrayDictArray1.addContent(rootDictArrayDictArrayDict1);
		rootDictArrayDict.addContent(rootDictArrayDictArray1);

		rootDictArray.addContent(rootDictArrayDict);
		rootDict.addContent(rootDictArray);
		root.addContent(rootDict);
		// 根节点添加到文档中;
		Document Doc = new Document(root, docType);
		Format format = Format.getPrettyFormat();
		XMLOutputter XMLOut = new XMLOutputter(format);
		// 输出 user.xml 文件；
		FileOutputStream fos;
		try {
			File plistFile = createNewFile(plistFilePath);
			fos = new FileOutputStream(plistFile);
			XMLOut.output(Doc, fos);
			fos.close();
			return plistFile;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 创建文件
	 * @param fileName
	 * @throws Exception
	 */
	private static File createNewFile(String fileName) throws Exception{
		fileName = fileName.replaceAll("\\\\", "/");
		String[] pathes = fileName.split("/");
		int length = pathes.length;
		String basePath = "";
		for (int i = 0; i < length; i ++) {
			if (i == length - 1) {
				basePath += pathes[i];
			} else {
				basePath += pathes[i] + File.separator;
			}
			File file = new File(basePath);
			if (i == length - 1) {
				if (!file.exists()) {
					file.createNewFile();
				}
				return file;
			} else if (!file.exists()) {
				file.mkdir();
			}
		}
		return null;
	}
}
