package cn.com.yitong.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class GlobalStaticVar {
	public static Logger BusinessLog = null;
	public static Logger ExceptionLog = null;
	
	public static int QUERY_PERIOD = 5000;//每次处理的间隔
	public static int minThreadNum = 1;
	public static int maxThreadNum = 10;
	
	public static String templateCodeName = "";//交易模拟代码
	public static String simpleTradecode = "";//公民简项查询交易代码
	public static String senderID = "";//发起方标识
	public static String senderChannel = "";//源发起方渠道代码
	public static String receiverID = "";//接收方标识
	public static String businessType = "";//业务类型
	public static String businessZone = "";//业务发生地区
	
	public static int soapTimeOut = 5000;//查询超时
	public static String nciicURL = "";//公民前置URL
	
	public static String PhotoDownloadIP = "10.2.35.32";//相片文件服务器IP
	public static int PhotoDownloadPort = 65500;//相片文件服务器端口
	public static String photoFilePath = "";//相片保存目录
	
	public static Map<String, String> errCode = new HashMap<String, String>();
	static{
		errCode.put("0000", "接口返回：请求处理成功");
		errCode.put("CI01", "接口返回：不允许接入");
		errCode.put("CI02", "接口返回：内部处理出错");
		errCode.put("CI03", "接口返回：行内前置不可用或超时");
		errCode.put("CI04", "接口返回：NCIIC服务不可用");
		errCode.put("CI05", "接口返回：NCIIC调用超时");
		errCode.put("CI06", "接口返回：NCIIC内部处理异常");
		errCode.put("CI07", "接口返回：NCIIC核查失败,原因在errMsg中描述");
		errCode.put("0008", "接口返回：请求参数错误");
		errCode.put("0009", "接口返回：其它错误");
	}
}
