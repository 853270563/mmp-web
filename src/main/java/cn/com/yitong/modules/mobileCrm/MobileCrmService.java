package cn.com.yitong.modules.mobileCrm;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.consts.Properties;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.servlet.ServerInit;
import cn.com.yitong.framework.util.CtxUtil;

public class MobileCrmService {
	private static final Logger logger = LoggerFactory.getLogger(MobileCrmService.class);
	//缓存模板
	private static Map<String, String> CACHE = new HashMap<String, String>();
	private static final String rootpath = System.getProperty(ServerInit.getString("moblieApproval")) + "/WEB-INF/conf/webservice/mobileCrm/";
	public static final String mobileCrm = "mobileCrm";
	public static final String mobileCrmXw = "mobileCrmXw";
	public static final String mobileCrmXwPic = "mobileCrmXwPic";
	
	public static final String RET_SUCCESS = "0000";
	public static final String fix="http://other.service.ws.channel.component.yitong.com.cn/";
	public static String newReturnValue="";
/**
 * 移动crm
 * @param ctx
 * @return
 * @throws Exception
 */

	public static Map<String, Object> mobileCrm(IBusinessContext ctx) throws Exception {
		Map<String, Object> rst = new HashMap<String, Object>();
		InvokeWebservice invokeWebservice = new InvokeWebservice();
		String inParamXml = "";
		if (CACHE.containsKey(mobileCrm)) {
			inParamXml = CACHE.get(mobileCrm);
		} else {
			File file = ResourceUtils.getFile(rootpath + "mobileCrmOut.xml");
			inParamXml = FileUtils.readFileToString(file, "UTF-8");
			CACHE.put(mobileCrm, inParamXml);
		}
		Map<String, String> params = ctx.getParamMap();
		String inParamXmlFinal = CtxUtil.makesoapRequestData(params, inParamXml);
		logger.info("mobileCrm 发送前报文\n" + inParamXmlFinal);
		String returnValue = "";
		try {
			returnValue = invokeWebservice.invokeHttpWebservice(Properties.getString("mobileCrm"), 
					inParamXmlFinal, 3000,fix+"queryyt0001"); 
			logger.info("mobileCrm 返回报文 \n" + returnValue);    
		} catch (Exception e) {
			rst.put(AppConstants.STATUS, "0");
			rst.put(AppConstants.MSG, "交易异常");
			return rst;
		}
		rst = MobileCrmParseReviceXml.parseResultJson(rst, "<?xml version='1.0' encoding='utf-8'?><responseBody>"+returnValue+"</responseBody>");
		return rst;
	}
	/**
	 * 移动crm小微
	 * @param ctx
	 * @return
	 * @throws Exception
	 */
	
	public static Map<String, Object> mobileCrmXw(IBusinessContext ctx) throws Exception {
		Map<String, Object> rst = new HashMap<String, Object>();
		InvokeWebservice invokeWebservice = new InvokeWebservice();
		String inParamXml = "";
		if (CACHE.containsKey(mobileCrmXw)) {
			inParamXml = CACHE.get(mobileCrmXw);
		} else {
			File file = ResourceUtils.getFile(rootpath + "mobileCrmXwOut.xml");
			inParamXml = FileUtils.readFileToString(file, "UTF-8");
			CACHE.put(mobileCrmXw, inParamXml);
		}
		Map<String, String> params = ctx.getParamMap();
		String inParamXmlFinal = CtxUtil.makesoapRequestData(params, inParamXml);
		logger.info("mobileCrmXW 发送前报文\n" + inParamXmlFinal);
		String returnValue = "";
		try {
			returnValue = invokeWebservice.invokeHttpWebservice(Properties.getString("mobileCrm"), 
					inParamXmlFinal, 3000,fix+"queryyt0001"); 
			logger.info("mobileCrm 返回报文 \n" + returnValue);    
		} catch (Exception e) {
			rst.put(AppConstants.STATUS, "0");
			rst.put(AppConstants.MSG, "交易异常");
			return rst;
		}
		rst = MobileCrmParseReviceXml.parseResultJson(rst, "<?xml version='1.0' encoding='utf-8'?><responseBody>"+returnValue+"</responseBody>");
		return rst;
	}
	
	
	/**
	 * 移动crm小微趋势图接口
	 * @param ctx
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> mobileCrmXwPic(IBusinessContext ctx) throws Exception {
		Map<String, Object> rst = new HashMap<String, Object>();
		InvokeWebservice invokeWebservice = new InvokeWebservice();
		String inParamXml = "";
		if (CACHE.containsKey(mobileCrmXwPic)) {
			inParamXml = CACHE.get(mobileCrmXwPic);
		} else {
			File file = ResourceUtils.getFile(rootpath + "mobileCrmXwPicOut.xml");
			inParamXml = FileUtils.readFileToString(file, "UTF-8");
			CACHE.put(mobileCrmXwPic, inParamXml);
		}
		Map<String, String> params = ctx.getParamMap();
		String inParamXmlFinal = CtxUtil.makesoapRequestData(params, inParamXml);
		logger.info("mobileCrmXW 发送前报文\n" + inParamXmlFinal);
		String returnValue = "";
		try {
			returnValue = invokeWebservice.invokeHttpWebservice(Properties.getString("mobileCrm"), 
					inParamXmlFinal, 3000,fix+"queryyt0002"); 
			logger.info("mobileCrm 返回报文 \n" + returnValue);    
		} catch (Exception e) {
			rst.put(AppConstants.STATUS, "0");
			rst.put(AppConstants.MSG, "交易异常");
			return rst;
		}
		rst = MobileCrmParseReviceXml.parseResultJson(rst, "<?xml version='1.0' encoding='utf-8'?><responseBody>"+returnValue+"</responseBody>");
		return rst;
	}
}