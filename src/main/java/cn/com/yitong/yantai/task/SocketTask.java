package cn.com.yitong.yantai.task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.Socket;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.alibaba.fastjson.JSONObject;

import cn.com.yitong.common.utils.SpringContextUtils;
import cn.com.yitong.core.session.Session;
import cn.com.yitong.core.session.util.SessionManagerUtils;
import cn.com.yitong.framework.net.impl.credit.CreditConst;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.yantai.websocket.WsServer;

/**
 * @author luanyu
 * @date   2018年4月24日
 */
public class SocketTask implements Runnable {
	private static Logger logger = Logger.getLogger(SocketTask.class);
	private int LENGTH = 7;
	private Socket client;
	private InputStream in;
	private OutputStream out;
	private static SqlSession sqlSession = SpringContextUtils.getBean(SqlSession.class);
	public void init(Socket socket) {
		this.client = socket;
	}

	/**
	 * 读取请求报文
	 * @return
	 */
	private String recRequestMessage() {
		String resultStr = null;
		try {

			byte[] byt = new byte[LENGTH];
			in.read(byt);
			int length = Integer.parseInt(new String(byt, "UTF-8"));
			byte[] contentbyte = new byte[length];
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
		} catch (IOException e) {
			logger.error("IO数据流异常:" + e.getMessage());

		}
		return resultStr;
	}

	public boolean execute() {
		String resultStr = null;
		try {
			in = new DataInputStream(client.getInputStream());
			out = new DataOutputStream(client.getOutputStream());
			resultStr = recRequestMessage();
			Document doc = null;
			doc = DocumentHelper.parseText(resultStr);
			// 3.格式化XML
			Writer writer = new StringWriter();
			StringUtil.formateXMLStr(writer, doc);
			logger.info("格式化显示请求报文:\n" + writer.toString());
			Element rootElement = doc.getRootElement();
			logger.debug(rootElement);
			Element service_idElement = (Element) rootElement.selectSingleNode("Service/Service_Header/service_id");
			String service_id = service_idElement.getText();
			if (service_id.equals("00970000000001")) {
				sendToMessageByWebsocket(doc);
			} else if (service_id.equals("00970000000002")) {
				parseTicket(doc);
			} else if (service_id.equals("00970000000003")) {
				parseUserModify(doc);
			}
			rspMessage(doc.asXML());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {

			close();
		}
		return true;
	}

	private void parseUserModify(Document doc) {
		// TODO Auto-generated method stub
		Element request = (Element) doc.selectSingleNode("/Service/Service_Body/request");
		Element Service_Header = (Element) doc.selectSingleNode("/Service/Service_Header");
		Element Service_Body = (Element) doc.selectSingleNode("/Service/Service_Body");
		Element service_response = Service_Header.addElement("service_response");
		Element response = Service_Body.addElement("response");
		String loginName = request.elementText("loginName");
		String sysFlag = request.elementText("sysFlag");//01-OA 02-移动驾驶舱 03-crm
		if ("03".equals(sysFlag)) {
			sqlSession.insert("crm.insert", loginName);
		}
		service_response.addElement("status").setText("COMPLETE");
		service_response.addElement("code").setText("0000");
		service_response.addElement("desc").setText("交易成功");

	}

	private void parseTicket(Document doc) {
		// TODO Auto-generated method stub
		Element request = (Element) doc.selectSingleNode("/Service/Service_Body/request");
		Element Service_Header = (Element) doc.selectSingleNode("/Service/Service_Header");
		Element Service_Body = (Element) doc.selectSingleNode("/Service/Service_Body");
		Element service_response = Service_Header.addElement("service_response");
		Element response = Service_Body.addElement("response");
		String token = request.element("token").getText();
		Session session = null;
		try {

			session = SessionManagerUtils.getDefaultManager().getSession(token);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		if (session != null) {
			response.addElement("loginName").setText(session.getUserId());
			service_response.addElement("status").setText("COMPLETE");
			service_response.addElement("code").setText("0000");
			service_response.addElement("desc").setText("交易成功");
		} else {
			service_response.addElement("status").setText("FAIL");
			service_response.addElement("desc").setText("session超时");

		}

	}

	private void sendToMessageByWebsocket(Document doc) {
		JSONObject jsonObject = new JSONObject();
		Element request = (Element) doc.selectSingleNode("/Service/Service_Body/request");
		Element title = request.element("title");
		Element content = request.element("content");
		Element msgId = request.element("msgId");
		Element totalNum = request.element("totalNum");
		Element userIds = request.element("userIds");
		String users = null;
		if (title != null) {

			jsonObject.put("title", title.getText());
		}
		if (content != null) {
			jsonObject.put("content", content.getText());

		}
		if (msgId != null) {
			jsonObject.put("msgId", msgId.getText());

		}
		if (totalNum != null) {
			jsonObject.put("totalNum", totalNum.getText());

		}
		if (userIds != null) {
			users = userIds.getText();

		}

		jsonObject.put("serviceSn", doc.selectSingleNode("/Service/Service_Header/service_sn").getText());
			jsonObject.put("peripheralType", "1");
		Element Service_Header = (Element) doc.selectSingleNode("/Service/Service_Header");
		Element Service_Body = (Element) doc.selectSingleNode("/Service/Service_Body");
		Element service_response = Service_Header.addElement("service_response");
		Element response = Service_Body.addElement("response");
		service_response.addElement("status").setText("COMPLETE");
		service_response.addElement("code").setText("0000");
		service_response.addElement("desc").setText("交易成功");
		response.addElement("message").setText("发送成功");
		response.addElement("status").setText("1");
		response.addElement("message").setText("发送成功");

		try {
			if (StringUtil.isNotEmpty(users)) {
				String[] user = users.split("\\|");
				for (String userId : user) {
					WsServer.sendMessageTo(jsonObject.toJSONString(), userId);
				}

			} else {
				WsServer.sendMessageAll(jsonObject.toJSONString());

			}
			logger.info("推送到客户端的消息为：" + jsonObject.toJSONString());
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


	public void close() {

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
