package cn.com.yitong.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class SmsUtil {
	private static Logger log = YTLog.getLogger(SmsUtil.class);
	private final static int smsMaxLength = 60;

	/**
	 * desc:字符串分割函数,用于本系统中发送手机短信时短信字数分割,每60个字符组装为一条短信,每一个标点,数字,中文,英文都视为一个字符
	 * 
	 * @param str
	 *            传入需要分割的字符
	 * @return 分割后的字符串数组
	 * @author yangjun date 2010.1.29
	 */
	public static String[] subString(String str, String tail) {
		if (tail == null) {
			tail = "";
		}
		List<String> result = new ArrayList<String>();
		if (str != null) {
			str = str.trim();
			int maxContentLength = smsMaxLength - tail.length();
			while (str.length() > maxContentLength) {
				result.add(str.substring(0, maxContentLength) + tail);
				str = str.substring(maxContentLength);
			}
			if (!"".equals(str)) {
				result.add(str + tail);
			}
		}
		return result.toArray(new String[result.size()]);
	}

	/**
	 * 检查手机号码是移动或联通或其他手机 移动手机号码返回 false 移动外手机返回 true
	 * 
	 * @param str
	 *            传入手机号码
	 * @return true false
	 */
	public static boolean checkMobileIsLianTong(String str) {
		List<String> mobileList = new ArrayList<String>();
		mobileList.add("130");
		mobileList.add("131");
		mobileList.add("132");
		mobileList.add("133");
		mobileList.add("153");
		mobileList.add("155");
		mobileList.add("156");
		mobileList.add("186");
		mobileList.add("189");

		if (StringUtils.isNotEmpty(str) && str.length() > 4) {
			String newStr = str.substring(0, 3);

			for (String str1 : mobileList) {
				if (newStr.indexOf(str1) != -1) {
					log.info("mobile:" + newStr);
					return true;
				}
			}

		}
		return false;
	}

	/**
	 * 发送短信
	 * 
	 * @param userMobile
	 * @param smsContext
	 * @param sysTransNum
	 * @param smsBankCode
	 * @param user
	 * @param smsType
	 * @param bankCode
	 * @return
	 * @throws Exception
	 */
	public static boolean sendSms(String userMobile, String smsContext,
			String bankCode,String userId) throws Exception {
		//categoryCode待完善
		StringBuffer requestMsg = new StringBuffer();  
		requestMsg.append("<data>");
		requestMsg.append("<userId>"+userId+"</userId>");
		requestMsg.append("<bankCode>"+bankCode+"</bankCode>");
		requestMsg.append("<userMobile>"+userMobile+"</userMobile>");
		requestMsg.append("<context>"+smsContext+"</context>");
		requestMsg.append("<categoryCode>05</categoryCode>");
		requestMsg.append("</data>");
		//获得发送地址待完善
		String url="http://onlineuat.cupdata.com/PayCenter/";
		String rsponeMsg  = "";//getData(requestMsg.toString(),url+"sms/SmsOperation.action?");
		try {
			if (StringUtil.isEmpty(rsponeMsg)) {
				return true;
			}
			log.info("响应结果:"+rsponeMsg);
			Document document = DocumentHelper.parseText(rsponeMsg);
			Element root = document.getRootElement();
			
			String flag = root.element("flag").getText();
			if(flag.equals("false")){
				log.error("调用发送短信接口返回结果为:失败");
				return false;
			}
			log.info("调用发送短信接口返回结果为:成功");
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return false;
		} 
	}
	
	
	/**
	 * 根据请求获取接口返回的XML信息 
	 * 
	 * @param _path
	 * @return
	 */
	public static String getData(String requestMsg,String urlpath) {
		String posturl = urlpath;
		String requestPath = "&requestMsg="+requestMsg;
		StringBuffer sb = new StringBuffer();
		URL url;
		InputStream inputStream = null;
		InputStreamReader isr = null;
		BufferedReader in = null;
		try {
			url = new URL(posturl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			OutputStream os = conn.getOutputStream();
			os.write(requestPath.getBytes("utf-8"));
			os.close();

			inputStream = conn.getInputStream();
			isr = new InputStreamReader(inputStream, "utf-8");
			in = new BufferedReader(isr);
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				sb.append(inputLine);
				sb.append("\n");
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (null != in) {
					in.close();
				}
				if (null != isr) {
					isr.close();
				}
				if (null != inputStream) {
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				// TODO: handle exception
			}
		}
		log.info(sb.toString());
		return sb.toString();
	}
	/**
	 * 固定长度4位随机码
	 * @return
	 */
	public static String productCode(){
		StringBuffer randomCode = new StringBuffer();
		Random random = new Random();
		char[] codeSequences =  { '0','1','2', '3', '4', '5', '6', '7', '8','9'};
		for (int i = 0; i < 4; i++) {
			String strRand = String.valueOf(codeSequences[random.nextInt(10)]);
			randomCode.append(strRand);
		}
		return randomCode.toString();
	}
}