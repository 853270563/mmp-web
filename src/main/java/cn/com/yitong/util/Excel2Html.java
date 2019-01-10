package cn.com.yitong.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.poi.hssf.converter.ExcelToHtmlConverter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * @author luanyu
 * @date   2018年5月8日
 */
public class Excel2Html {
	public static void main(String[] args) throws IOException, ParserConfigurationException, TransformerException {
		convertExcel2Html("C:\\Users\\hasee\\Downloads\\差旅费.xls", "D:\\df\\excel.html");
	}
	public static void convertExcel2Html(String excelFilePath, String htmlFilePath) throws IOException, ParserConfigurationException, TransformerException {
		File excelFile = new File(excelFilePath);
		File htmlFile = new File(htmlFilePath);
		File htmlFileParent = new File(htmlFile.getParent());
		InputStream is = null;
		OutputStream out = null;
		StringWriter writer = null;
		try {
			if (excelFile.exists()) {
				if (!htmlFileParent.exists()) {
					htmlFileParent.mkdirs();
				}
				is = new FileInputStream(excelFile);
				HSSFWorkbook workBook = new HSSFWorkbook(is);
				ExcelToHtmlConverter converter = new ExcelToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());

				converter.processWorkbook(workBook);

				writer = new StringWriter();
				Transformer serializer = TransformerFactory.newInstance().newTransformer();
				serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				serializer.setOutputProperty(OutputKeys.INDENT, "yes");
				serializer.setOutputProperty(OutputKeys.METHOD, "html");
				serializer.transform(new DOMSource(converter.getDocument()), new StreamResult(writer));
				out = new FileOutputStream(htmlFile);
				out.write(writer.toString().getBytes("UTF-8"));
				out.flush();
				out.close();
				writer.close();
			}
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (out != null) {
					out.close();
				}
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
