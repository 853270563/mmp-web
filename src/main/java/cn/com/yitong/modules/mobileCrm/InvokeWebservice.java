package cn.com.yitong.modules.mobileCrm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("deprecation")
public class InvokeWebservice {
	
	private static final Logger _logger = LoggerFactory.getLogger(InvokeWebservice.class);

	public String invokeHttpWebservice(String interfaceUrl, String inParamXml, Integer soTimeOut, String action) throws Exception {
		String callResult = "";
		SoapClient client = new SoapClient(HttpClientFactory.createHttpClient(soTimeOut, soTimeOut));
		long start = System.currentTimeMillis();
		String soapResponseMsg = client.sendRequest(interfaceUrl, inParamXml, action);
		_logger.info("soapResponseMsg:"+soapResponseMsg);
		_logger.info("交易耗时："+(System.currentTimeMillis()-start));
//		org.dom4j.Document doc1 = DocumentHelper.parseText(soapResponseMsg.replace("&gt;", ">").replace("&lt;", "<").replace("&quot;", "\"").replaceAll("<\\?xml[^>]*?>", "").replace("<![CDATA[", "").replace("]]>", ""));
//		Element el = doc1.getRootElement();
//		removeAttAndNamespace(el);
//		String tmp = el.element("Body").asXML().replace("xmlns:", "");
//		tmp = tmp.replace("xmlns", "a");
//		org.dom4j.Document doc2 = DocumentHelper.parseText(tmp);
//		removeAttAndNamespace(doc2.getRootElement());
//		callResult = doc2.getRootElement().asXML();
		callResult = soapResponseMsg.substring(soapResponseMsg.indexOf("<sysHead>"), soapResponseMsg.indexOf("</ns3:WSResponse>"));
		return callResult;
	}

	public String invokeByHttpClient(String url, String input,
			Integer timeout) {
		return invokeByHttpClient(url, input, "", "UTF-8", timeout);
	}
	
	/**
	 * SOAP鏂瑰紡璋冪敤
	 * 
	 * @param url
	 * @param input
	 * @param soapAction
	 * @param charset
	 * @param timeout
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String invokeByHttpClient(String url, String input,
			String soapAction, String charset, Integer timeout) {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		post.getParams().setParameter(HttpConnectionParams.SO_TIMEOUT, timeout);
		post.setHeader("SOAPAction", soapAction);
		HttpResponse response;
		String soapenv = null;
		try {
			post.setEntity(new StringEntity(input, charset));
			response = client.execute(post);
			soapenv = EntityUtils.toString(response.getEntity());
		} catch (UnsupportedEncodingException e) {
			_logger.error(e.getMessage(), e);
		} catch (ParseException e) {
			_logger.error(e.getMessage(), e);
		} catch (ClientProtocolException e) {
			_logger.error(e.getMessage(), e);
		} catch (IOException e) {
			_logger.error(e.getMessage(), e);
		}
		return soapenv;
	}
	
	/**
	 * 灏嗚繑鍥炵殑缁撴灉涓墍鏈夊睘鎬у強鍛藉悕绌洪棿娓呴櫎
	 * @return
	 * @throws DocumentException
	 */
	private void removeAttAndNamespace(Element element){
		for (int i = 0,size = element.nodeCount(); i<size; i++ ){
			org.dom4j.Node node = element.node(i); 
			if (node instanceof Element) { 
				Element et = (Element)node;
				Iterator<?> inter = et.attributes().iterator();
				while(inter.hasNext()){
					inter.next();
					inter.remove();
				}
				String namespacePrefix = et.getNamespacePrefix();
				if(!"".equals(namespacePrefix)){
					QName qName = et.getQName();
					QName newqName = DocumentHelper.createQName(qName.getName());
					et.setQName(newqName);
				}
				removeAttAndNamespace((Element)node); 
           }
		}
	}
}
