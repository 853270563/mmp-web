package cn.com.yitong.market.mm.mgg;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.net.impl.temp.ClientFactory4Test;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.util.MessageTools;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;
@Controller
public class SendMessageController extends BaseControl {

	private Logger logger = YTLog.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("requestBuilder4db")
	IRequstBuilder requestBuilder;// 请求报文生成器
	@Autowired
	@Qualifier("responseParser4db")
	IResponseParser responseParser;// 响应报文解析器
	@Autowired
	@Qualifier("urlClient4db")
	IClientFactory client;// 响应报文解析器
	@Autowired
	@Qualifier("EBankConfParser4db")
	IEBankConfParser confParser;// 报文装载器
	
	@Autowired
	ICrudService service;
	final String BASE_PATH = "market/mm/mgg/message/";
//	{CHANNEL_CODE:'MMP',MOBILE:$phone.val()
//        ,MSG_SEND_TYPE:'1',FLAG:'0',MSG_TYPE:'0',Message_Type:'0'/}
	/**
	 * 
	 * @param trans_code 交易所码
	 * @param request
	 * @return
	 */
	@RequestMapping("mm/mgg/message/{trans_code}.do")
	@ResponseBody
	public Map sendCardMessage(@PathVariable String trans_code, HttpServletRequest request)
	{
		String transCode = BASE_PATH + trans_code;
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = CtxUtil.createMapContext(request);
		
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}

		boolean ok = true;
		/*if (CtxUtil.debugTrans(trans_code)) {
			boolean ok = client.execute(ctx, transCode);
			transAfter(ctx, transCode, rst, ok);
			return rst;
		}
		// 数据库操作区
		Map params = ctx.getParamMap();
		try {
			// 数据库操作
			String statementName = CtxUtil.getStatement(confParser, transCode);
			ok = service.insert(statementName, params);
		} catch (Exception e) {
			// 输出错误的关键信息
			logger.error(ctx.getTransLogBean(transCode), e);
		}*/
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}
	/**
	 * 发送短信
	 * @param trans_code
	 * @param request
	 * @return
	 */
	@RequestMapping("mm/mgg/SendMessage.do")
	@ResponseBody
	public Map sendMessage(HttpServletRequest request,
			String trans_code) {
		Map randomMap = new HashMap();
		String transCode=BASE_PATH + "sendMessage";
		Map<String, Object> rst = new HashMap<String, Object>();
		int randNum = randomNum(); //6位数随机码
		int BIAOSHI = randomNum(); //6位数标识
		// 初始化数据总线
		IBusinessContext ctx = CtxUtil.createMapContext(request);
		ctx.initParamCover(json2MapParamCover, transCode, false);
		String type = ctx.getParam("Message_Type");
		ctx.setParam("SERVICE_CODE", "11002000001");
		ctx.setParam("SERVICE_SCENE", "01");
		ctx.setParam("SOURCE_TYPE", "G");
		
		String oneStr = "";
		String twoStr = "";
		String threeStr = "";
		String fourStr = "";
		String fiveStr = "";

		
		String sumTol = ctx.getParam("sumNum");
		
		if(sumTol != null && sumTol != ""){
			
			if("1".equals(String.valueOf(sumTol.charAt(0)))){
				oneStr = "签约个人短信通知业务;"; 
			}
		
			if("1".equals(String.valueOf(sumTol.charAt(1)))){
				twoStr = "签约手机银行;"; 
			}
		
			if("1".equals(String.valueOf(sumTol.charAt(2)))){
				threeStr = "签约ATM;"; 
			}
			
			if("1".equals(String.valueOf(sumTol.charAt(3)))){
				fourStr = "签约理财签约;"; 
			}
			
			if("1".equals(String.valueOf(sumTol.charAt(4)))){
				fiveStr = "签约网银签约;"; 
			}
		}
		
		ctx.setParam("MSG_TYPE", "4");
		if("0".equals(type)){
			if("0000".equals(sumTol)){
				ctx.setParam("MSG","尊敬的客户,正在为您办理的开卡业务,短信验证码为" + randNum
					+ ",有效期120秒,请输入验证码进行确认。");
			}else{
				ctx.setParam("MSG","尊敬的客户,正在为您办理开卡加签约业务,"+oneStr+twoStr+threeStr+fourStr+fiveStr
						+",短信验证码为" + randNum
					+ "有效期120秒,请输入验证码进行确认。");
			}
			
		}else if("3".equals(type)){
			//需要传入的参数有：
			//MOBILE   Message_Type NEW_QUEUE_NO
			ctx.setParam("CHANNEL_CODE", "MMP");
			ctx.setParam("MSG_SEND_TYPE", "1");
			ctx.setParam("FLAG", "0");
			ctx.setParam("MSG_TYPE", "4");
			ctx.setParam("MSG", "尊敬的客户,您的转移号码业务已经转受理成功，新的号码为" + ctx.getParam("NEW_QUEUE_NO"));
		}else if("4".equals(type)){
			//需要传入的参数有：
			//MOBILE   Message_Type  NEW_QUEUE_NO
			ctx.setParam("CHANNEL_CODE", "MMP");
			ctx.setParam("MSG_SEND_TYPE", "1");
			ctx.setParam("FLAG", "0");
			ctx.setParam("MSG_TYPE", "4");
			ctx.setParam("MSG", "尊敬的客户,您已经转为绿色通道，新的号码为" + ctx.getParam("NEW_QUEUE_NO"));
		}else{
			ctx.setParam("MSG","尊敬的客户,正在为您办理签约业务,"+oneStr+twoStr+threeStr+fourStr+fiveStr
					+",短信验证码为" + randNum
				+ "有效期120秒,请输入验证码进行确认。");
		}
		
//		this.execute(ctx, request, transCode);
//		MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
		{
			rst = ClientFactory4Test.getResultMap(transCode, "esb");
		}
		if(rst.get(AppConstants.STATUS).equals(AppConstants.STATUS_OK)){
			if (logger.isDebugEnabled()) {
				logger.info(" 短信发送 ESB base context:\n"
						+ StringUtil.formatXmlStr(ctx.getBaseContext().asXML()));
			}
			Date curDate = new Date();
			final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
			String  curDateL = (String)sdf.format(new Date());
			//long curDateL = curDate.getTime(); //获取发送短信的时间
			randomMap.put("RANDOMNUM", randNum);
			String phone = ctx.getParam("MOBILE");
			randomMap.put("BIAOSHI", BIAOSHI);
			randomMap.put("SEND_TIME", curDateL);
			randomMap.put("PHONE", phone);
			service.insert("VALUEDATA_MSG.insertmessage", randomMap);
			rst.put("SEND_TIME", curDateL);
			rst.put("BIAOSHI", BIAOSHI);
			logger.debug("rst info:\n" + rst.toString());
			return rst;
		}else{
			rst.put("STATUS", "0");
			rst.put("MSG", "发送短信失败!");
			return rst;
		}
	}
	
	/**
	 * 验证短信
	 * @param trans_code
	 * @param request
	 * @return
	 */
	@RequestMapping("mm/mgg/checkMessage.do")
	@ResponseBody
	public Map checkMessage(HttpServletRequest request,
			String trans_code) {
		Map rspMap = new HashMap();
		String transCode=BASE_PATH + "checkMessage";
		Map<String, Object> rst = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = CtxUtil.createMapContext(request);
		ctx.initParamCover(json2MapParamCover, transCode, false);
			String send_time = ctx.getParam("SEND_TIME");
			String MOBILE = ctx.getParam("MOBILE");
			String BIAOSHI = ctx.getParam("BIAOSHI");
			String RANDOMNUM = ctx.getParam("RANDOMNUM");
			final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyyMMddHHmmssSSSS");
			String  end_time = (String)sdf.format(new Date());
			Long start = Long.valueOf(send_time);
			Long end = Long.valueOf(end_time);
			Long result = end - start;
			logger.info("start   "+start +"      end "+end);
			if(result > 2000000){
				rst.put("MSG", "短信验证码已超时，请重新发送");
				rst.put("STATUS", "0");
				return rst;
			}
			rspMap.put("PHONE", MOBILE);
			rspMap.put("BIAOSHI", BIAOSHI);
			rspMap.put("RANDOMNUM", RANDOMNUM);
			List list = service.findList("VALUEDATA_MSG.checkMessage", rspMap);
			if(list == null || list.size() == 0){
				rst.put("MSG", "验证码错误，请重新输入");
				rst.put("STATUS", "0");
				return rst;
			}else{
				rst.put("MSG", "验证成功!");
				rst.put("STATUS", "1");
				logger.debug("rst 验证成功:\n" + rst.toString());
				return rst;
			}
	}
	
	private static int randomNum(){
		return (int) ((Math.random()*9+1)*100000);
	}
	
	/**
	 * 事务前置处理
	 * 
	 * @param ctx
	 * @param transCode
	 * @param rst
	 * @return
	 */
	private boolean transPrev(IBusinessContext ctx, String transCode, Map rst) {
		// 交易开始，设置交易流水
		commonService.generyTransLogSeq(ctx, transCode);
		return CtxUtil.transPrev(ctx, transCode, json2MapParamCover,
				requestBuilder, confParser, rst);
	}

	/**
	 * 事务之后处理
	 * 
	 * @param ctx
	 * @param transCode
	 * @param rst
	 * @param ok
	 */
	private void transAfter(IBusinessContext ctx, String transCode, Map rst,
			boolean ok) {
		// 生成交易结果
		CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
		// 保存交易日志
		commonService.saveJsonTransLog(ctx, transCode, rst);
	}
}