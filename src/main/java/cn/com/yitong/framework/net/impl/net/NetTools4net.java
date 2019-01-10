package cn.com.yitong.framework.net.impl.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alibaba.fastjson.JSONObject;

import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.net.impl.credit.CreditConst;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.yantai.websocket.WsServer;

/**
 * MB通讯组件
 * 
 * @author yaoym
 * 
 */
//@Component
public class NetTools4net implements Runnable {
	@Autowired
	ICrudService service;

	@Autowired
	@Qualifier("requestBuilder4net")
	IRequstBuilder requestBuilder;

	@Autowired
	@Qualifier("EBankConfParser4net")
	IEBankConfParser confParser;

	@Autowired
	@Qualifier("responseParser4net")
	protected IResponseParser responseParser;
	
	/*@Autowired
	protected MQTTClientService mqService;*/

	private static Logger logger = Logger.getLogger(NetTools4net.class);
	private int LENGTH = 7;
	private String UTF_8="UTF-8";
	private Socket client;
	private InputStream in;
	private OutputStream out;
	public void init(Socket socket) {
		this.client = socket;
	}
	/**
	 * 读取请求报文
	 * @return
	 */
	private String recRequestMessage() {
		String resultStr=null;
		try {

			byte[] byt = new byte[LENGTH];
		     in.read(byt);
			int length = Integer.parseInt(new String(byt, "UTF-8"));
		     byte []contentbyte= new byte[length];
			int nIdx = 0;
			int nReadLen = 0;
			while (nIdx < length) {
				nReadLen = in.read(contentbyte, nIdx, length - nIdx);
				if (nReadLen > 0) {
					nIdx = nIdx + nReadLen;
					logger.debug("已读字节数" + nIdx);
				} else {
					break;
				}
			}
			resultStr = new String(contentbyte, "UTF-8");
		} catch (NumberFormatException e) {
			logger.error("消息接受长度为非数字:" + e.getMessage());
		}catch (IOException e) {
			logger.error("IO数据流异常:" + e.getMessage());
			
		}
		return resultStr;
	}
	
	
	@SuppressWarnings({ "unchecked"})
	public boolean execute() {
		String resultStr=null;
		try {
            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());
			resultStr = recRequestMessage();
			Document doc = null;
				doc = DocumentHelper.parseText(resultStr);
			/*if(StringUtil.isNotEmpty(resultStr)){
					mqService.connetActionPerformed();
				}*/
			sendToMessageByWebsocket(doc);
			// 3.格式化XML
			Writer writer = new StringWriter();
			StringUtil.formateXMLStr(writer, doc);
			logger.info("格式化显示请求报文:\n" + writer.toString());
			rspMessage(doc.asXML());
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {

			close();
		}
		return true;
	}
	
	
	private void sendToMessageByWebsocket(Document doc) {
		JSONObject jsonObject = new JSONObject();

		Element request = (Element) doc.selectSingleNode("/Service/Service_Body/request");
		List<Element> elements = request.elements();
		for (Element element : elements) {

			jsonObject.put(element.getName(), element.getText());

		}

		if (doc.selectSingleNode("/Service/Service_Header/service_id").getText().equals("00080000100001")) {
			jsonObject.put("peripheralType", "1");
		}
		Element Service_Header = (Element) doc.selectSingleNode("/Service/Service_Header");
		Element Service_Body = (Element) doc.selectSingleNode("/Service/Service_Body");
		Element service_response = Service_Header.addElement("service_response");
		Element response = Service_Body.addElement("response");
		service_response.addElement("status").setText("COMPLETE");
		service_response.addElement("code").setText("1");
		service_response.addElement("desc").setText("交易成功");
		response.addElement("message").setText("发送成功");
		response.addElement("megId").setText("发送成功");
		try {
			//WsServer.sendMessageAll(jsonObject.toString());
			WsServer.sendMessageTo(jsonObject.toString(), jsonObject.getString("userId"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}

	}
	private void rspMessage(String message) {
		String lengthStr;
		try {
			lengthStr = "" + message.getBytes(CreditConst.UTF_8).length;
			String headNumSre = StringUtil.lpadString(lengthStr, CreditConst.LENGTH, "0");
			message = headNumSre + message;
			;
			logger.debug("send string message:\n " + message + "$");
			out.write(message.getBytes(CreditConst.UTF_8));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		} finally {
			try {
				client.shutdownOutput();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
			}
		}

	}
	@SuppressWarnings("unchecked")
	public String getCurrentUserId(String userId){
		Map<String,String> params = new HashMap<String, String>();
		String strUserId="";
		if(StringUtil.isNotEmpty(userId)){
			String[] userids=userId.split("\\|");
			for(int i=0;i<userids.length;i++){
				if(StringUtil.isEmpty(userids[i])){
					continue;
				}
				params.put("USER_ID", userids[i]);
				Map<String,String> data = service.load("SYS_USER.queryOfficeId",params);
				if(data==null){
					continue;
				}
				if(i==(userids.length-1)){
					if(StringUtil.isNotEmpty(data.get("ANY_OFFICE_NO"))){
						strUserId+=data.get("ANY_OFFICE_NO")+"";
					}else{
						strUserId+= userids[i]+"";
					}
				}else{
					if(StringUtil.isNotEmpty(data.get("ANY_OFFICE_NO"))){
						strUserId+=data.get("ANY_OFFICE_NO")+"|";
					}else{
						strUserId+= userids[i]+"|";
					}
				}
			}	
		}
		return strUserId;
	}
	
	public void close(){

		if (client != null) {
			try {
				client.close();
			} catch (Exception ex) {
				logger.error("error:", ex);
			}
		}
	}
	@Override
	public void run() {
		execute();
	}

}
