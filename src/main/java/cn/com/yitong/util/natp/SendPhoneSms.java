package cn.com.yitong.util.natp;

/*import gfbank.bean.util.Account;*/

import java.text.SimpleDateFormat;
import java.util.Date;
public class SendPhoneSms {
	
	public static void main(String[] args) {
		
		
		System.out.println(sendcheckcode("13331029673", "132456")+"+++++++++++++++++++++");
	}


	// 验证码发送
	public static String sendcheckcode(String mobile, String verifycode) {
		int natpVersion = 16;//数据包版本
		String transCode = "MSEND";//交易代码
		String templateCode = "300001";//模板代码
		String reservedCode = "SMSP";//保留字段
		CommunicationNatp natp = new CommunicationNatp();
		natp.init(natpVersion, transCode, templateCode, reservedCode);

		try {
			// 手机验证码
			natp.pack("SMSID", "F4");//短信业务代码
			// 验证码模版
			natp.pack("TEMPLATEID", "057F400004");//短信模板代码

			natp.pack("FIXTEMPLATE", "");
			natp.pack("SERIAL", TimeFactory.getSerial());//短信流水号
			natp.pack("CHANNELCODE", "057");//系统代码（渠道代码）
			natp.pack("SENDBRANCH", "000000");//发送机构
			natp.pack("MOBILE", mobile);
			
			natp.pack("CONTENT", "");
			natp.pack("SUBBRANCH", "000000");
			natp.pack("IDTYPE", "");
			natp.pack("IDCODE", "");
			// 验证码
			natp.pack("VERIFYCODE", verifycode);
			String sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss")
					.format(new Date());
			natp.pack("DATEYYYY", sdf.substring(0, 4));
			natp.pack("DATEMM", sdf.substring(4, 6));
			natp.pack("DATEDD", sdf.substring(6, 8));
			natp.pack("TIMEHH", sdf.substring(9, 11));
			natp.pack("TIMEMM", sdf.substring(12, 14));
			return natp.exchange_result("10.2.35.86:58088:100");
		} catch (Exception e) {
			e.printStackTrace();
			return "发送失败!";
		}
	}
}
