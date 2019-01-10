package cn.com.yitong.framework.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.axis.Message;
import org.apache.axis.soap.MessageFactoryImpl;
import org.apache.axis.soap.SOAPConnectionImpl;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import cn.com.yitong.consts.AppConstants;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class InterfaceUtil {

	/**
	 * 調用接口的公用方法
	 * @param xmlString
	 * @param urlStr
	 * @return
	 * @throws java.io.IOException
	 * @throws org.dom4j.DocumentException
	 * @throws javax.xml.soap.SOAPException
	 */
	public static Document CallInterface(String xmlString)
			throws IOException, DocumentException, SOAPException{

		byte[] xmlData = xmlString.getBytes("GBK");
		Document doc = null;
		ByteArrayOutputStream os = null;
		InputStream is = null;
		SOAPConnectionImpl scon = null;
		
		MessageFactoryImpl mf = new MessageFactoryImpl();
		SOAPMessage smsg = mf.createMessage(new MimeHeaders(),
				new ByteArrayInputStream(xmlData));
		scon = new SOAPConnectionImpl();
		scon.setTimeout(50000);
		Message msg = (Message)smsg;
		SOAPMessage response = null;
		response = scon.call(msg, AppConstants.CREDIT_GATEWAY_IP_PORT);		
		// SOAPMessage转dom4j的Document
		os = new ByteArrayOutputStream();
		response.writeTo(os);
		is = new ByteArrayInputStream(os.toByteArray());
		SAXReader sa = new SAXReader();
		doc = sa.read(is);
		return doc;
	}

	/**
	 * 獲取FREEMARKER 的Configuration對象
	 * @param directory
	 * @return
	 * @throws java.io.IOException
	 */
	public static Configuration buildConfiguration(String directory)
			throws IOException {
		Configuration cfg = new Configuration();
		Resource path = new DefaultResourceLoader().getResource(directory);
		cfg.setDirectoryForTemplateLoading(path.getFile());
		return cfg;
	}

	/**
	 * 生成XML報文
	 * @param template
	 * @param model
	 * @return
	 * @throws freemarker.template.TemplateException
	 * @throws java.io.IOException
	 */
	public static String renderTemplate(Template template, Object model) throws TemplateException, IOException {

		StringWriter result = new StringWriter();
		template.process(model, result);
		return result.toString();

	}
}
