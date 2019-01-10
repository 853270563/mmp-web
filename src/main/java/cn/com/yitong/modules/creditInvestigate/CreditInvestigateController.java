package cn.com.yitong.modules.creditInvestigate;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.InputSource;

import Decoder.BASE64Encoder;
import cn.com.yitong.ares.login.BizLogger;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.service.IThirdPatryMessageService;
import cn.com.yitong.framework.servlet.ServerInit;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.util.MessageTools;
import cn.com.yitong.util.SunEcmClient;
import cn.com.yitong.util.YTLog;

@Controller
public class CreditInvestigateController extends BaseControl {
	private Logger logger = YTLog.getLogger(this.getClass());
	// 缓存base64地址
	private static Map<String, Object> CACHE = new HashMap<String, Object>();
	public static final String upload_files_path_yxInfo = ServerInit.getString("upload_files_path_yxInfo");
	public static final String YX_IP = ServerInit.getString("YX_IP");
	public static final int YX_SOCKETPORT = ServerInit.getInt("YX_SOCKETPORT");
	public static final int YX_HTTPPORT = ServerInit.getInt("YX_HTTPPORT");
	public static final String YX_SERVERNAME = ServerInit.getString("YX_SERVERNAME");
	public static final String YX_GROUPNAME = ServerInit.getString("YX_GROUPNAME");
	@Resource
	private ICrudService service;
	private static final String YT_CREDIT = "credit" + "/";
	@Autowired
	private IThirdPatryMessageService thirdPatryMessageService;
	final String BASE_PATH = "creditInvestigate/";
	final BizLogger bizLogger = BizLogger.getLogger(this.getClass());
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
	@Qualifier("requestBuilder4credit")
	IRequstBuilder requestBuilder4credit;

	@Autowired
	@Qualifier("EBankConfParser4credit")
	IEBankConfParser confParser4credit;
	@Autowired
	@Qualifier("socketFactory4credit")
	protected IClientFactory clientFactory;

	/**
	 * 信贷待办任务列表
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("creditTaskList/creditTaskList.do")
	@ResponseBody
	public Map creditTaskList(HttpServletRequest request) {
		String transCode = "waittingDone";
		transCode = YT_CREDIT + transCode;
		Map<String, Object> result = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
		// 加载参数
		if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
			return result;
		}

		Map<String,String> map1 = new HashMap<String,String>();
		List<Map<String,String>> listMap = service.findList("TRANS_DATA.findAllWhiteCode", map1);
		if (!listMap.isEmpty()) {
			StringBuffer businessType = new StringBuffer();
			for (int i = 0; i < listMap.size(); i++) {
				if (i < listMap.size() - 1) {
					businessType.append(listMap.get(i).get("BUSINESS_CODE")).append(",");
				}
				else {

					businessType.append(listMap.get(i).get("BUSINESS_CODE"));
				}
			}

			ctx.getParamMap().put("businessType", businessType.toString());
		}


		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);

		return result;
	
	}

	/**
	 * 公司、小企业客户概况
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("custInfo/custInfo.do")
	@ResponseBody
	public Map custInfo(HttpServletRequest request) {
		logger.info("客户信息...start...");
		String transCode = YT_CREDIT+"custInfo";
		Map result = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 加载参数
		 if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
			return result;
		}

		boolean ok = false;
		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);
		return result;
	}
	/**
	 * 个人客户概况
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("personalCustInfo/personalCustInfo.do")
	@ResponseBody
	public Map personalCustInfo(HttpServletRequest request) {
		logger.info("客户信息...start...");
		String transCode = YT_CREDIT+"personalCustInfo";
		Map result = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 加载参数
		if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
			return result;
		}

		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);
		return result;
	}
	/**
	 * 集团客户概况 适用客户类型0210
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("groupCustInfo/groupCustInfo.do")
	@ResponseBody
	public Map groupCustInfo(HttpServletRequest request) {
		logger.info("客户信息...start...");
		String transCode = YT_CREDIT+"groupCustInfo";
		Map result = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 加载参数
		if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
			return result;
		}

		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);
		return result;
	}
	/**
	 * 抵押物信息详情
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("guaranteeInfo/guaranteeInfo.do")
	@ResponseBody
	public Map guaranteeInfo(HttpServletRequest request) {
		String transCode = "guaranteeInfo";
		transCode = YT_CREDIT + transCode;
		Map<String, Object> result = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
		// 加载参数
		if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
			return result;
		}

		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);
		return result;
	
	}

	/**
	 * 是否显示签署意见
	 * @param request
	 * @return
	 */
	@RequestMapping("showSign/showSign.do")
	@ResponseBody
	public Map showSign(HttpServletRequest request) {
		String transCode = YT_CREDIT + "isShowSignOut";
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 加载参数
		if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
			return rst;
		}
		// 数据库操作区	
		thirdPatryMessageService.creditLoanSystem(ctx, transCode, rst);
		return rst;
	}
	/**
	 * 是否显示贷审会意见
	 * @param request
	 * @return
	 */
	@RequestMapping("showJudgeSign/showJudgeSign.do")
	@ResponseBody
	public Map showJudgeSign(HttpServletRequest request) {
		logger.info("查询贷审会意见...start...");
		String transCode = YT_CREDIT+"showJudgeSign";
		Map result = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 加载参数
		if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
			return result;
		}

		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);

		return result;
	}
	/***
	 * 
	 * 
	 * 客户法人信息
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("custLegalRep/custLegalRep.do")
	@ResponseBody
	public Map custLegalRep(HttpServletRequest request) {
		logger.info("客户法人信息...start...");
		String transCode = YT_CREDIT+"custLegalRep";
		Map result = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 加载参数
		if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
			return result;
		}

		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);

		return result;
	}
	/**
	 * 股东
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("custStockHolder/custStockHolder.do")
	@ResponseBody
	public Map custStockHolder(HttpServletRequest request) {
		logger.info("客户股东信息...start...");
		String transCode = YT_CREDIT+"custStockHolder";
		Map result = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 加载参数
		if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
			return result;
		}

		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);

		return result;
	}
	/**
	 * 
	 * 客户高管信息
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("custExecutives/custExecutives.do")
	@ResponseBody
	public Map custExecutives(HttpServletRequest request) {
		logger.info("客户高管信息...start...");
		String transCode = YT_CREDIT+"custExecutives";
		Map result = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 加载参数
		if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
			return result;
		}

		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);
		return result;
	}

	/**
	 * 基本信息
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("baseInfo/baseInfo.do")
	@ResponseBody
	public Map baseInfo(HttpServletRequest request) {
		logger.info("基本信息...start...");
		String transCode = "baseInfo";
		transCode = YT_CREDIT + transCode;
		Map<String, Object> result = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 加载参数
		if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
			return result;
		}
		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);
			String vouchType1="";
			List list = (List)result.get("item");
			Map<String, Object> itemMap = (Map<String, Object>)list.get(0);
			for(Map.Entry<String, Object> entry :itemMap.entrySet()){
				if(entry.getKey().equals("VouchType1")){
					vouchType1 = entry.getValue().toString().replaceAll("010", "保证").replaceAll("020", "抵押").replaceAll("030", "保证金").replaceAll("040", "质押");
				}
			}
			itemMap.put("VouchType1", vouchType1);

		return result;

	}
	/**
	 * 授信额度分配信息
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("amountAllocate/amountAllocate.do")
	@ResponseBody
	public Map amountAllocate(HttpServletRequest request) {
		logger.info("授信额度分配信息...start...");
		String transCode = YT_CREDIT + "amountAllocate";
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 加载参数
		if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
			return rst;
		}
		thirdPatryMessageService.creditLoanSystem(ctx, transCode, rst);
		return rst;

	}
	/**
	 * 授信额度担保信息
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("amountAssurance/amountAssurance.do")
	@ResponseBody
	public Map amountAssurance(HttpServletRequest request) {
		logger.info("授信额度担保信息...start...");
		String transCode = YT_CREDIT+"amountAssurance";
		Map result = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 加载参数
		if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
			return result;
		}

		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);

		return result;
	}
	/**
	 * 授信额度担保详情信息
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("amountAssuranceDetail/amountAssuranceDetail.do")
	@ResponseBody
	public Map amountAssuranceDetail(HttpServletRequest request) {
		logger.info("授信额度担保信息...start...");
		String transCode = YT_CREDIT+"amountAssuranceDetail";
		Map result = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 加载参数
		if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
			return result;
		}
		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);
		return result;
	}
	/**
	 * 业务文档信息
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("bussDocument/bussDocument.do")
	@ResponseBody
	public Map bussDocument(HttpServletRequest request) {
		logger.info("业务文档信息...start...");
		String transCode = YT_CREDIT + "bussDocument";
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 加载参数
		if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
			return rst;
		}
		if (!requestBuilder4credit.buildSendMessage(ctx, confParser4credit, transCode)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
			return rst;
		}
		if (!clientFactory.execute(ctx, transCode)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
			return rst;
		}
		try {
			YtParseReviceMsg.parseMessege(rst, (String) ctx.getResponseEntry(), "bussDocumentOut");
		} catch (Exception e1) {
			logger.error("业务文档信息查询：" + e1);
			rst.put("TRANS_STATUS", "0001");
			rst.put("TRANS_MSG", "系统查询数据异常，请联系移动金融管理员。");
		}
		logger.info("授信额度担保信息查询...end...");

		return rst;

	}
	/**
	 * 查看意见信息
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("allOpition/allOpition.do")
	@ResponseBody
	public Map allOpition(HttpServletRequest request) {
		logger.info("业务文档信息...start...");
		String transCode = YT_CREDIT + "allOpition";
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 加载参数
		if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
			return rst;
		}
		thirdPatryMessageService.creditLoanSystem(ctx, transCode, rst);

		return rst;

	}
	/**
	 * 当前意见选择
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("currentOpitions/currentOpitions.do")
	@ResponseBody
	public Map currentOpitions(HttpServletRequest request) {
		logger.info("当前意见选择信息...start...");
		String transCode = YT_CREDIT + "currentOpitions";
		@SuppressWarnings("rawtypes")
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 加载参数
		if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
			return rst;
		}
		thirdPatryMessageService.creditLoanSystem(ctx, transCode, rst);
		return rst;

	}
	/**
	 *下一步信息
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("nextInfo/nextInfo.do")
	@ResponseBody
	public Map nextInfo(HttpServletRequest request) {
		logger.info("下一步信息...start...");
		String transCode = YT_CREDIT+"nextInfo";
		@SuppressWarnings("rawtypes")
		Map result = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 加载参数
		if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
			return result;
		}
		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);
		return result;

	}
	/**
	 *影像首页列表信息
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("yxPicInfo/yxPicInfo.do")
	@ResponseBody
	public Map yxPicnfo(HttpServletRequest request) {
		logger.info("影像首页列表...start...");
		String transCode = YT_CREDIT + "yxPicInfoOut";
		@SuppressWarnings("rawtypes")
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 加载参数
		if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
			return rst;
		}
		if (!requestBuilder4credit.buildSendMessage(ctx, confParser4credit, transCode)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
			return rst;
		}
		if (!clientFactory.execute(ctx, transCode)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
			return rst;
		}
	
		try {
			YtParseReviceMsg.parseMessege(rst, (String) ctx.getResponseEntry(), "yxPicInfoOut");
		} catch (Exception e1) {
			logger.error("影像首页列表信息查询：" + e1);
			rst.put("STATUS", "0");
			rst.put("MSG", "系统查询数据异常，请联系移动金融管理员。");
		}
		logger.info("影像信息查询...end...");
		
		return rst;

	}
	/**
	 * 已办任务
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("alreadyDone/alreadyDone.do")
	@ResponseBody
	public Map alreadyDone(HttpServletRequest request) {
		logger.info("已办代办任务列表...start...");
		String transCode = YT_CREDIT+"alreadyDone";
		Map result = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 加载参数
		if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
			return result;
		}

		// 数据库操作区	
		Map<String,String> map1 = new HashMap<String,String>();
		List<Map<String,String>> listMap = service.findList("TRANS_DATA.findAllWhiteCode", map1);
		if (!listMap.isEmpty()) {
			StringBuffer businessType = new StringBuffer();
			for (int i = 0; i < listMap.size(); i++) {
				if (i < listMap.size() - 1) {
					businessType.append(listMap.get(i).get("BUSINESS_CODE")).append(",");
				} else {

					businessType.append(listMap.get(i).get("BUSINESS_CODE"));
				}
			}

			ctx.getParamMap().put("businessType", businessType.toString());
		}

		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);

		return result;
	}
	/**
	 *影像图片列表信息
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("downPicList/downPicList.do")
	@ResponseBody
	public Map downPicList(HttpServletRequest request) {
		logger.info("影像图片列表信息...start...");
		String transCode = BASE_PATH+"downPicList";
		@SuppressWarnings("rawtypes")
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 加载参数
		if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
			return rst;
		}

		// 数据库操作区
		Map params = ctx.getParamMap();
		logger.info(params);
		boolean successCode=true;
		//Map yxPicnfo =null;
		SunEcmClient client = new SunEcmClient();
		List<Map<String,Object>> queryExampleMapList;
		//点击加载图片列表之前先清除缓存，缓存用于储存大图片
		try{			
			//有数据放开
			String queryExampleStr = client.query(params.get("contentId").toString(), params.get("objectName").toString(),
					params.get("busiStartDate").toString());
			//测试数据
			//String queryExampleStr = client.queryExample("20170223_12_913_D0E347EA-0BEB-3017-11AB-039F4902845A-1","XDKHZL","20081015");
			queryExampleStr = queryExampleStr.substring(queryExampleStr.indexOf("<?xml"));
			//有数据放开
			queryExampleMapList = parseYxFangHuiMess1(queryExampleStr,params.get("objectName").toString(),params.get("serialNo").toString(),params.get("codeString").toString(),params.get("userId").toString());
			//测试数据
			//queryExampleMapList = parseYxFangHuiMess1(queryExampleStr,"XDKHZL","20081015",params.get("codeString").toString());
			//yxPicnfo.put("allPicture",queryExampleMapList);
			rst.put("LIST",queryExampleMapList);
			CACHE.put(params.get("contentId").toString()+params.get("codeString").toString()+params.get("userId").toString(),queryExampleMapList);
		}catch(Exception e1){
			successCode = false;
			logger.error("影像图片列表信息查询：" + e1);
			rst.put("STATUS", "0");
			rst.put("MSG", "系统查询数据异常，请联系移动金融管理员。");
		}
		logger.info("影像信息查询...end...");
		if(successCode){
			rst.put("STATUS", "1");
			rst.put("MSG", "影像信息查询成功");
		}
		return rst;
	}



	/**
	 * 
	 * @param str
	 * @param objectName
	 * @param serialNo
	 * @param codeString1
	 * @return
	 */

	@SuppressWarnings("unchecked")
	public List parseYxFangHuiMess1(String str, String objectName, String serialNo, String codeString1, String userId) {
		List<Map<String,Object>> listUrl=new ArrayList<Map<String,Object>>();
		Map<String,String> map = new HashMap<String,String>();
		StringReader read = new StringReader(str);
		InputSource source = new InputSource(read);
		SAXReader saxReader = new SAXReader();
		Document document = null;
		try {
			document =saxReader.read(source);
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
		Element rootElm =  document.getRootElement();
		Element filesElm = rootElm.element("BatchBean").element("document_Objects").element("BatchFileBean").element("files");
		List<Element> fileBean = filesElm.elements("FileBean");
		//List<Map<String,Object>> fullPaths = new ArrayList<Map<String,Object>>();
		List<String> pathList = new ArrayList<String>();
		for(int y=0;y<fileBean.size();y++){
			Map<String,Object> fileBeanMap = new HashMap<String,Object>();
			String saveNameString = fileBean.get(y).attribute("SAVE_NAME").getText();
			map.put("saveNameString", saveNameString);
			String fileNameString = fileBean.get(y).attribute("FILE_NAME").getText();
			String fileFormatString = fileBean.get(y).attribute("FILE_FORMAT").getText();
			map.put("fileFormatString", fileFormatString);
			String fileString =fileNameString+fileFormatString;
			map.put("fileString", fileString);
			String codeString = fileBean.get(y).element("otherAtt").element("BUSI_FILE_TYPE").element("string").getText();
			if(codeString.equals(codeString1)){
				Map map1 = new HashMap();
				map1.put("OBJECT_NAME_EN", objectName);
				map1.put("FILE_TYPE_BARCODE", codeString);
				String picURL=fileBean.get(y).attribute("URL").getText();
				if(y==0){
					pathList.add(picURL);
				}else{
					for(String strUint : pathList){
						if(picURL.equals(strUint)){
							continue;
						}
					}
					pathList.add(picURL);
				}
				fileBeanMap.put("picUrBig",picURL);
				picURL=picURL+"&SMALL";
				stringTransBase64(picURL,fileBeanMap,"imagine");
				List<Map<String,String>> listMap = service.findList("TRANS_DATA.findList", map1);
				//List<String> urlList = new ArrayList<String>();
				if(null !=listMap&&listMap.size()>0){
					fileBeanMap.put("pic", listMap.get(0).get("FILE_TYPE_NAME"));
				}else{
					fileBeanMap.put("pic","其他资料");
				}
				listUrl.add(fileBeanMap);
			} 
		}
		return listUrl;
	}
	/**
	 *点击小图片去取大图片
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("downBigPic/downBigPic.do")
	@ResponseBody
	public Map downBigPic(HttpServletRequest request) {
		logger.info("影像图片列表信息...start...");
		String transCode = YT_CREDIT+"downBigPic";
		@SuppressWarnings("rawtypes")
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 加载参数
		if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
			return rst;
		}

		// 数据库操作区
		Map params = ctx.getParamMap();
		boolean successCode=true;
		try{
			List<Map<String,Object>> listUrl = (List<Map<String,Object>>)CACHE.get(params.get("contentId").toString()+params.get("codeString").toString()+params.get("userId").toString());
			Map<String, Object> map = listUrl.get(Integer.valueOf(params.get("index").toString()));
			stringTransBase64(map.get("picUrBig").toString(),rst,"imagine");
		}catch(Exception e1){
			successCode = false;
			logger.error("图片加载失败：" + e1);
			rst.put("STATUS", "0");
			rst.put("MSG", "图片加载失败，请联系移动金融管理员。");
		}
		logger.info("图片加载...end...");
		if(successCode){
			rst.put("STATUS", "1");
			rst.put("MSG", "图片加载成功");
		}
		return rst;
	}
		/**
		 * 调查报告
		 * @param request
		 * @return
		 */
		@RequestMapping("invReport/invReport.do")
		@ResponseBody
		public Map invReport(HttpServletRequest request) {
			logger.info("调查报表任务列表...start...");
		String transCode = YT_CREDIT + "invReport";
			Map rst = new HashMap();
			// 初始化数据总线
			IBusinessContext ctx = new BusinessContext(request,
					IBusinessContext.PARAM_TYPE_MAP);
			// 加载参数
			if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
				MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
				return rst;
			}

			// 数据库操作区	
			Map params = ctx.getParamMap();
			if(null==params.get("customerName")||"".equals(params.get("customerName").toString())){
				params.remove("customerName");
			}
			if(null==params.get("businessName")||"全部类型".equals(params.get("businessName").toString())){
				params.remove("businessName");
			}
		if (!requestBuilder4credit.buildSendMessage(ctx, confParser4credit, transCode)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
			return rst;
		}
		if (!clientFactory.execute(ctx, transCode)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
			return rst;
		}
		try {
			YtParseReviceMsg.parseMessege(rst, (String) ctx.getResponseEntry(), "invReport");
		} catch (Exception e1) {
			logger.error("调查报表列表：" + e1.getMessage(), e1);
			rst.put("STATUS", "0");
			rst.put("MSG", e1.getMessage());
		}

			return rst;
		}
		/**
		 * 保存意见接口
		 * @param request
		 * @return
		 */

		@RequestMapping("keepAdivice/keepAdivice.do")
		@ResponseBody
		public Map keepAdivice(HttpServletRequest request) {
			logger.info("保存意见...start...");
			String transCode = YT_CREDIT+"keepAdivice";
			Map result = new HashMap();
			// 初始化数据总线
			IBusinessContext ctx = new BusinessContext(request,
					IBusinessContext.PARAM_TYPE_MAP);
			// 加载参数
			if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
				MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
				return result;
			}


		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);

			return result;
		}
		/**
		 * 保存评级意见接口
		 * @param request
		 * @return
		 */

		@RequestMapping("keepPjAdvice/keepPjAdvice.do")
		@ResponseBody
		public Map keepPjAdvice(HttpServletRequest request) {
			logger.info("保存意见...start...");
			String transCode = YT_CREDIT+"keepPjAdvice";
			Map result = new HashMap();
			boolean successCode = true;
			// 初始化数据总线
			IBusinessContext ctx = new BusinessContext(request,
					IBusinessContext.PARAM_TYPE_MAP);
			// 加载参数
			if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
				MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
				return result;
			}


		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);

			return result;
		}
		/**
		 * 评级终审确认接口
		 * @param request
		 * @return
		 */

		@RequestMapping("assureTrue/assureTrue.do")
		@ResponseBody
		public Map assureTrue(HttpServletRequest request) {
			logger.info("保存意见...start...");
			String transCode = YT_CREDIT+"assureTrue";
			Map result = new HashMap();
			// 初始化数据总线
			IBusinessContext ctx = new BusinessContext(request,
					IBusinessContext.PARAM_TYPE_MAP);
			// 加载参数
			if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
				MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
				return result;
			}

		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);

			return result;
		}
		/**
		 * 提交接口
		 * @param request
		 * @return
		 */

		@RequestMapping("submitAction/submitAction.do")
		@ResponseBody
		public Map submitAction(HttpServletRequest request) {
			String transCode = YT_CREDIT+"submitAction";
			Map result = new HashMap();
			// 初始化数据总线
			IBusinessContext ctx = new BusinessContext(request,
					IBusinessContext.PARAM_TYPE_MAP);
			// 加载参数
			if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
				MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
				return result;
			}
		bizLogger.info(ctx.getParam("CURRENT_ADDR"), "8160000");
		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);

			return result;
		}
		/**
		 * 点击签署意见判断是否可以签署意见
		 * @param request
		 * @return
		 */
		@SuppressWarnings("unchecked")
		@RequestMapping("clickGet/clickGet.do")
		@ResponseBody
		public Map clickGet(HttpServletRequest request) {
			String transCode = "clickGet";
			transCode = YT_CREDIT + transCode;
			Map<String, Object> result = new HashMap<String, Object>();
			// 初始化数据总线
			IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
			// 加载参数
			if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
				MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
				return result;
			}

		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);

			return result;
		
		}
		/**
		 * 关联额度信息
		 * @param request
		 * @return
		 */
		@SuppressWarnings("unchecked")
		@RequestMapping("relateLimitInfo/relateLimitInfo.do")
		@ResponseBody
		public Map relateLimitInfo(HttpServletRequest request) {
			String transCode = "relateLimitInfo";
			transCode = YT_CREDIT + transCode;
			Map<String, Object> result = new HashMap<String, Object>();
			// 初始化数据总线
			IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
			// 加载参数
			if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
				MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
				return result;
			}

		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);

			return result;
		
		}
		/**
		 * 集团授信额度分配
		 * @param request
		 * @return
		 */
		@SuppressWarnings("unchecked")
		@RequestMapping("groupCreditDisInfo/groupCreditDisInfo.do")
		@ResponseBody
		public Map groupCreditDisInfo(HttpServletRequest request) {
			String transCode = "groupCreditDisInfo";
			transCode = YT_CREDIT + transCode;
			Map<String, Object> result = new HashMap<String, Object>();
			// 初始化数据总线
			IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
			// 加载参数
			if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
				MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
				return result;
			}

		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);

			return result;
		
		}
		/**
		 * 集团授信额度分配列表
		 * @param request
		 * @return
		 */
		@SuppressWarnings("unchecked")
		@RequestMapping("groupCreditDisList/groupCreditDisList.do")
		@ResponseBody
		public Map groupCreditDisList(HttpServletRequest request) {
			String transCode = "groupCreditDisList";
			transCode = YT_CREDIT + transCode;
			Map<String, Object> result = new HashMap<String, Object>();
			// 初始化数据总线
			IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
			// 加载参数
			if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
				MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
				return result;
			}
		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);
			return result;
		
		}
		/**
		 * 共同申请人信息
		 * @param request
		 * @return
		 */
		@SuppressWarnings("unchecked")
		@RequestMapping("publicCreditPerson/publicCreditPerson.do")
		@ResponseBody
		public Map publicCreditPerson(HttpServletRequest request) {
			String transCode = "publicCreditPerson";
			transCode = YT_CREDIT + transCode;
			Map<String, Object> result = new HashMap<String, Object>();
			// 初始化数据总线
			IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
			// 加载参数
			if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
				MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
				return result;
			}
		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);
			return result;
		
		}
		/**
		 * 房产按揭类型
		 * @param request
		 * @return
		 */
		@SuppressWarnings("unchecked")
		@RequestMapping("estateMortgageType/estateMortgageType.do")
		@ResponseBody
		public Map estateMortgageType(HttpServletRequest request) {
			String transCode = "estateMortgageType";
			transCode = YT_CREDIT + transCode;
			Map<String, Object> result = new HashMap<String, Object>();
			// 初始化数据总线
			IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
			// 加载参数
			if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
				MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
				return result;
			}
		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);
			return result;
		
		}
		/**
		 * 项目许可证信息
		 * @param request
		 * @return
		 */
		@SuppressWarnings("unchecked")
		@RequestMapping("projectLicenseInfo/projectLicenseInfo.do")
		@ResponseBody
		public Map projectLicenseInfo(HttpServletRequest request) {
			String transCode = "projectLicenseInfo";
			transCode = YT_CREDIT + transCode;
			Map<String, Object> result = new HashMap<String, Object>();
			// 初始化数据总线
			IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
			// 加载参数
			if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
				MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
				return result;
			}

		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);

			return result;
		
		}
		/**
		 * 汽车按揭类型信息
		 * @param request
		 * @return
		 */
		@SuppressWarnings("unchecked")
		@RequestMapping("carMortgagetype/carMortgagetype.do")
		@ResponseBody
		public Map carMortgagetype(HttpServletRequest request) {
			String transCode = "carMortgagetype";
			transCode = YT_CREDIT + transCode;
			Map<String, Object> result = new HashMap<String, Object>();
			// 初始化数据总线
			IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
			// 加载参数
			if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
				MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
				return result;
			}
		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);

			return result;
		
		}
		/**
		 * 担保公司额度分配方案
		 * @param request
		 * @return
		 */
		@SuppressWarnings("unchecked")
		@RequestMapping("compEdFpPlan/compEdFpPlan.do")
		@ResponseBody
		public Map compEdFpPlan(HttpServletRequest request) {
			String transCode = "compEdFpPlan";
			transCode = YT_CREDIT + transCode;
			Map<String, Object> result = new HashMap<String, Object>();
			// 初始化数据总线
			IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
			// 加载参数
			if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
				MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
				return result;
			}

		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);

			return result;
		
		}
		/**
		 * 同业授信额度分配
		 * @param request
		 * @return
		 */
		@SuppressWarnings("unchecked")
		@RequestMapping("sameBussEdFp/sameBussEdFp.do")
		@ResponseBody
		public Map sameBussEdFp(HttpServletRequest request) {
			String transCode = "sameBussEdFp";
			transCode = YT_CREDIT + transCode;
			Map<String, Object> result = new HashMap<String, Object>();
			// 初始化数据总线
			IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
			// 加载参数
			if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
				MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
				return result;
			}
		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);
			return result;
		
		}
		/**
		 * 个人工作履历
		 * @param request
		 * @return
		 */
		@SuppressWarnings("unchecked")
		@RequestMapping("peopleWorkList/peopleWorkList.do")
		@ResponseBody
		public Map peopleWorkList(HttpServletRequest request) {
			String transCode = "peopleWorkList";
			transCode = YT_CREDIT + transCode;
			Map<String, Object> result = new HashMap<String, Object>();
			// 初始化数据总线
			IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
			// 加载参数
			if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
				MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
				return result;
			}

		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);

			return result;
		
		}
		/**
		 * 配偶或家庭主要成员情况
		 * @param request
		 * @return
		 */
		@SuppressWarnings("unchecked")
		@RequestMapping("wifeAndFamliy/wifeAndFamliy.do")
		@ResponseBody
		public Map wifeAndFamliy(HttpServletRequest request) {
			String transCode = "wifeAndFamliy";
			transCode = YT_CREDIT + transCode;
			Map<String, Object> result = new HashMap<String, Object>();
			// 初始化数据总线
			IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
			// 加载参数
			if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
				MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
				return result;
			}

		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);

			return result;
		
		}
		/**
		 *个人学业履历
		 * @param request
		 * @return
		 */
		@SuppressWarnings("unchecked")
		@RequestMapping("peopleEducation/peopleEducation.do")
		@ResponseBody
		public Map peopleEducation(HttpServletRequest request) {
			String transCode = "peopleEducation";
			transCode = YT_CREDIT + transCode;
			Map<String, Object> result = new HashMap<String, Object>();
			// 初始化数据总线
			IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
			// 加载参数
			if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
				MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
				return result;
			}

		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);

			return result;
		
		}
		/**
		 *信用等级评级结果
		 * @param request
		 * @return
		 */
		@SuppressWarnings("unchecked")
		@RequestMapping("creditLevelResult/creditLevelResult.do")
		@ResponseBody
		public Map creditLevelResult(HttpServletRequest request) {
			String transCode = "creditLevelResult";
			transCode = YT_CREDIT + transCode;
			Map<String, Object> result = new HashMap<String, Object>();
			// 初始化数据总线
			IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
			// 加载参数
			if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
				MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
				return result;
			}
		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);
			return result;
		
		}
		/**
		 *退回上一步
		 * @param request
		 * @return
		 */
		@SuppressWarnings("unchecked")
		@RequestMapping("fallBank/fallBank.do")
		@ResponseBody
		public Map fallBank(HttpServletRequest request) {
			String transCode = "fallBank";
			transCode = YT_CREDIT + transCode;
			Map<String, Object> result = new HashMap<String, Object>();
			// 初始化数据总线
			IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
			// 加载参数
			if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
				MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
				return result;
			}
		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);
			return result;
		
		}
		/**
		 *是否显示退回上一步
		 * @param request
		 * @return
		 */
		@SuppressWarnings("unchecked")
		@RequestMapping("isOrNotfallBank/isOrNotfallBank.do")
		@ResponseBody
		public Map isOrNotfallBank(HttpServletRequest request) {
			String transCode = "isOrNotfallBank";
			transCode = YT_CREDIT + transCode;
			Map<String, Object> result = new HashMap<String, Object>();
			// 初始化数据总线
			IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
			// 加载参数
			if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
				MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
				return result;
			}
		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);
			return result;
		
		}
		/**
		 *判断法人，股东，高管是个人还是集团
		 * @param request
		 * @return
		 */
		@SuppressWarnings("unchecked")
		@RequestMapping("isPersionOrGroup/isPersionOrGroup.do")
		@ResponseBody
		public Map isPersionOrGroup(HttpServletRequest request) {
			String transCode = "isPersionOrGroup";
			transCode = YT_CREDIT + transCode;
			Map<String, Object> result = new HashMap<String, Object>();
			// 初始化数据总线
			IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
			// 加载参数
			if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
				MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
				return result;
			}
		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);
			return result;
		
		}
		
		public static void stringTransBase64(String picURL,Map<String,Object>fileBeanMap,String key) {
			HttpURLConnection httpURLConnection = null;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			String picStr=null;
			try {
				URL url = new URL(picURL);
				httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setConnectTimeout(8000);
				httpURLConnection.setReadTimeout(8000);
				httpURLConnection.setDoInput(true);
				// 获取从httpurlconnection得来的输入流
				InputStream inputStream = httpURLConnection.getInputStream();	
				int len = 0;
				byte[] b = new byte[1024];
				while ((len = inputStream.read(b, 0, b.length)) != -1) {                     
					baos.write(b, 0, len);
				}
				byte[] buffer =  baos.toByteArray();			
				inputStream.close();
				baos.close();
				BASE64Encoder encoder = new BASE64Encoder();
				picStr= encoder.encode(buffer);
				fileBeanMap.put(key, picStr);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (httpURLConnection != null) {
					httpURLConnection.disconnect();
				}
			}
	}
		/**
		 *最终审批意见
		 * @param request
		 * @return
		 */
		@SuppressWarnings("unchecked")
		@RequestMapping("lastOpinion/lastOpinion.do")
		@ResponseBody
		public Map lastOpinion(HttpServletRequest request) {
			String transCode = "lastOpinion";
			transCode = YT_CREDIT + transCode;
			Map<String, Object> result = new HashMap<String, Object>();
			// 初始化数据总线
			IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
			// 加载参数
			if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
				MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
				return result;
			}

		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);

			return result;
		
		}
		/**
		 *额度使用查询
		 * @param request
		 * @return
		 */
		@SuppressWarnings("unchecked")
		@RequestMapping("eduFound/eduFound.do")
		@ResponseBody
		public Map eduFound(HttpServletRequest request) {
			String transCode = "eduFound";
			transCode = YT_CREDIT + transCode;
			Map<String, Object> result = new HashMap<String, Object>();
			// 初始化数据总线
			IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
			// 加载参数
			if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
				MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
				return result;
			}

		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);

			return result;
		
		}

	/**
	 *贷款申请进度列表查询
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("loanApplicationProgress/qry.do")
	@ResponseBody
	public Map loanApplicationProgress(HttpServletRequest request) {
		String transCode = BASE_PATH + "loanApplicationProgress";
		Map<String, Object> rst = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}

		boolean ok = false;
		try {
			// 数据库操作
			ok = thirdPatryMessageService.creditLoanSystem(ctx, "00080000200001", rst);
		} catch (Exception e) {
			// 输出错误的关键信息
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;

	}

	/**
	 *贷款申请详情查询
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("loanApplicationDetail/qry.do")
	@ResponseBody
	public Map loanApplicationDetail(HttpServletRequest request) {
		String transCode = BASE_PATH + "loanApplicationDetail";
		Map<String, Object> rst = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}

		boolean ok = false;
		try {
			// 数据库操作
			ok = thirdPatryMessageService.creditLoanSystem(ctx, "00080000200002", rst);
		} catch (Exception e) {
			// 输出错误的关键信息
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;

	}
	
	/**
	 *预警事项查询
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("earlyWarningQuery/qry.do")
	@ResponseBody
	public Map earlyWarningQuery(HttpServletRequest request) {
		String transCode = BASE_PATH + "00080000200003";
		Map<String, Object> rst = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}

		boolean ok = false;
		try {
			// 数据库操作
			ok = thirdPatryMessageService.creditLoanSystem(ctx, "00080000200003", rst);
		} catch (Exception e) {
			// 输出错误的关键信息
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;

	}

	/**
	 *客户风险查询
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("customeRiskquery/qry.do")
	@ResponseBody
	public Map customeRiskquery(HttpServletRequest request) {
		String transCode = BASE_PATH + "00080000200004";
		Map<String, Object> rst = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}

		boolean ok = false;
		try {
			// 数据库操作
			ok = thirdPatryMessageService.creditLoanSystem(ctx, "00080000200004", rst);
		} catch (Exception e) {
			// 输出错误的关键信息
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;

	}

	/**
	 *客户列表查询
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("customeListQuery/qry.do")
	@ResponseBody
	public Map customeListQuery(HttpServletRequest request) {
		String transCode = BASE_PATH + "00080000200005";
		Map<String, Object> rst = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}

		boolean ok = false;
		try {
			// 数据库操作
			ok = thirdPatryMessageService.creditLoanSystem(ctx, "00080000200005", rst);
		} catch (Exception e) {
			// 输出错误的关键信息
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;

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
		return CtxUtil.transPrev(ctx, transCode, json2MapParamCover, requestBuilder, confParser, rst);
	}

	/**
	 * 事务之后处理
	 * 
	 * @param ctx
	 * @param transCode
	 * @param rst
	 * @param ok
	 */
	private void transAfter(IBusinessContext ctx, String transCode, Map rst, boolean ok) {
		CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
	}
}
