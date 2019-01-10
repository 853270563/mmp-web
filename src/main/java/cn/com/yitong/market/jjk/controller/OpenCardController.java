package cn.com.yitong.market.jjk.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import cn.com.yitong.ares.login.BizLogger;
import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.core.base.WebUtils;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BankcsBaseControl;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.market.jjk.model.JjkTranCard;
import cn.com.yitong.market.jjk.model.MggImageAtta;
import cn.com.yitong.market.jjk.service.CustomFileMngService;
import cn.com.yitong.market.jjk.service.JjkTranCardService;
import cn.com.yitong.market.jjk.service.MggImageAttaService;
import cn.com.yitong.util.ConfigEnum;
import cn.com.yitong.util.CustomFileType;
import cn.com.yitong.util.DateUtil;
import cn.com.yitong.util.MessageTools;
import cn.com.yitong.util.StringUtil;

@Controller
@RequestMapping("/jjk/opencard")
public class OpenCardController extends BankcsBaseControl {

	private static final String BASE_PATH = "market/jjk/tranDeCard/";

	@Resource
	private JjkTranCardService jjkTranCardService;
	
	@Resource
	private CustomFileMngService customFileMngService;
	
	@Resource
	private MggImageAttaService mggImageAttaService;
	
	@Autowired
	ICrudService service;
	final BizLogger bizLogger = BizLogger.getLogger(this.getClass());

	@RequestMapping("queryBusiCount")
	@ResponseBody
	public Map<String, Object> queryBusiCount(HttpServletRequest req) {
		String transCode = BASE_PATH + "QueryBusiCount";
		Map<String, Object> rst = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = CtxUtil.createMapContext(req);
		ctx.initParamCover(json2MapParamCover, transCode, false);
		if (!requestBuilder.buildSendMessage(ctx, confParser, transCode)) {
			// 请求报文检查失败
			MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
			return rst;
		}
		
		String userId = WebUtils.getCurrentUserId(req, ctx);
		if(null == userId) {
			ctx.setErrorInfo(AppConstants.STATUS_FAIL, "未获取到用户信息，请登录后再操作", transCode);
			MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
			return rst;
		}
		rst.put("SUCCESS_CHK_COUNT", jjkTranCardService.queryCount(userId, true));
		rst.put("FAIL_CHK_COUNT", jjkTranCardService.queryCount(userId, false));
		
		// 检查响应报文,过滤冗余输出，并可解析字典数据
		ctx.setSuccessInfo(AppConstants.STATUS_OK, "", transCode);
		ctx.setResponseEntry(rst);
		responseParser.parserResponseData(ctx, confParser, transCode);
		MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
		return rst;
	}
	
	/**
	 * 实时开卡申请提交
	 * @param req
	 * @return
	 * @throws java.sql.SQLException
	 * @throws java.io.FileNotFoundException
	 * @throws java.io.IOException
	 */
	@RequestMapping("TranDeCardAdd")
	@ResponseBody
	public Map<String, Object> cardInfoAdd(HttpServletRequest req)
			throws SQLException, FileNotFoundException, IOException {
		bizLogger.info("用户开卡申请","321100");
		String transCode = BASE_PATH + "TranDeCardAdd";
		Map<String, Object> rst = new HashMap<String, Object>();
		Map<String, Object> rst2 = new HashMap<String, Object>();
		Map<String, Object> rstNew = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext busiCtx = CtxUtil.createMapContext(req);
		busiCtx.initParamCover(json2MapParamCover, transCode, false);
		//身份证 图片信息
		String certImg= busiCtx.getParam("certImg");
		//封装开卡信息
		JjkTranCard cardInfo = new JjkTranCard();
		try {
			BeanUtils.copyProperties(cardInfo, busiCtx.getParamMap());
		} catch (Exception e) {
			logger.error("参数转换失败！", e);
			bizLogger.warn("用户开卡申请:参数转换失败！"+e.getMessage(),"321100");
			busiCtx.setErrorInfo(AppConstants.STATUS_FAIL, "参数转换有误，请联系管理员", transCode);
			e.printStackTrace();
		}
		//获取交易流水号
		Map idMap=service.load("JJK_TRAN_CARD.getSeqTranId", null);
		String transId=(String)idMap.get("TRANS_ID");
//		封装交易流水号
		cardInfo.setTransId(transId);
		//开卡和签约 时获取最小凭证号
		if(cardInfo.getSignType().equals("1") || cardInfo.getSignType().equals("2") )
		{
			busiCtx.setParam("czy", busiCtx.getParam("signUser"));
			busiCtx.setParam("jgm", busiCtx.getParam("signOrgan"));
			busiCtx.setParam("jym", "s821");
//			busiCtx.setParam("bzh", AppConstants.BZH);//币种号  人民币 11
//			busiCtx.setParam("sblx", AppConstants.SBLX); //默认值为你 1 核心接口做处理
			busiCtx.setParam("pzzl", "11"); //凭证种类 ？？？？确认开卡类别 并提供凭证种类
			String trans_code="pms821";
			//this.execute(busiCtx, req, trans_code);
			logger.info(">>>>>>获取最小凭证号接口交易代码="+trans_code);
			MessageTools.elementToMap(busiCtx.getResponseContext(trans_code), rst);
			
			rst.put("STATUS", "1");
			rst.put("pzhm", "100000000000000001");
			
			
			if (logger.isDebugEnabled()) {
				logger.debug(">> base context:\n"+ StringUtil.formatXmlStr(busiCtx.getBaseContext().asXML()));
				logger.debug(">> result info:\n" + rst.toString());
			}
			
			if (rst.get(AppConstants.STATUS).equals(AppConstants.STATUS_OK)) {
				
				String dateStr = DateUtil.todayStr().replaceAll("-|\\/", "").trim();
				trans_code="pms133"; //核心接口交易代码
				busiCtx.setParam("kh", cardInfo.getCardNo()); //卡号
//				busiCtx.setParam("qdh", AppConstants.QDH); //渠道号
//				busiCtx.setParam("xmbh", AppConstants.XMBH);//项目号
//				busiCtx.setParam("ddxh", AppConstants.DDXH); //调度序号
				
				busiCtx.setParam("dzbz", "0"); //1：对账0：不对账
				busiCtx.setParam("jym", "s133");
				busiCtx.setParam("wwrq", dateStr);//外围日期
				busiCtx.setParam("wwlsh", cardInfo.getTransId()); //外围流水号
				
				logger.info(">>>>>>占凭证号接口交易代码="+trans_code);
				//this.execute(busiCtx, req, trans_code);
				MessageTools.elementToMap(busiCtx.getResponseContext(trans_code), rst2);
				if (logger.isDebugEnabled()) {
					logger.debug(">> base context:\n"+ StringUtil.formatXmlStr(busiCtx.getBaseContext().asXML()));
					logger.debug(">> result info:\n" + rst2.toString());
				}
				
				rst2.put("STATUS", "1");
				bizLogger.warn("用户开卡成功","321101");
				if(!rst2.get(AppConstants.STATUS).equals(AppConstants.STATUS_OK)){
					return rst2;
				}
			}
			else{
				bizLogger.warn("用户开卡失败，交易状态："+rst.get(AppConstants.STATUS),"321102");
				return rst;
			}
		}

		//将开卡信息送到集中作业平台   
		
		
		//本地保存开卡信息
		cardInfo.setSignState(ConfigEnum.DICT_TRAN_DECARD_SIGN_STATE_SUBMIT
				.strVal());
		cardInfo.setCardType("01");// 01：默认，普通借记卡
		cardInfo.setInpuType("01");;//默认01
		boolean ok= jjkTranCardService.insert(cardInfo);
		
		logger.info(">>>>>>>开卡或签约信息保存本地数据库中：tranDecardService.insert(cardInfo)");
		
		
		//新增签约信息表  是否开手机银行 [flagSignMbank] 			
		if(cardInfo.getFlagSignMbank()!=null && cardInfo.getFlagSignMbank().equals("1")){ //开通
			Map<String, Object> tempmap = new HashMap<String, Object>();
			tempmap.put("TRANS_ID",cardInfo.getTransId());
			tempmap.put("CARD_NO",cardInfo.getCardNo());
			tempmap.put("SIGN_TYPE","1");   //手机银行
			tempmap.put("IS_DOWN","0"); 
			tempmap.put("CUST_MOBI_PHONE",cardInfo.getCustMobiPhone()); 
			tempmap.put("EXTEND1", ""); 
			tempmap.put("EXTEND2", ""); 
			tempmap.put("EXTEND3", ""); 
			tempmap.put("USER_ID", cardInfo.getSignUser()); 
			service.insert("JJK_SIGN_INFO.addSignInfo", tempmap);
			logger.info(">>>>>>>开通手机银行签约 [flagSignMbank] 保存数据库操作：JJK_SIGN_INFO.addSignInfo");
		}
		//是否开短信通知 [flagSignSbank]	
		if(cardInfo.getFlagSignSbank()!=null && cardInfo.getFlagSignSbank().equals("1")){ //开通
			Map<String, Object> tempmap = new HashMap<String, Object>();
			tempmap.put("TRANS_ID", cardInfo.getTransId());
			tempmap.put("CARD_NO", cardInfo.getCardNo());
			tempmap.put("SIGN_TYPE", "2");   //短信通知
			tempmap.put("IS_DOWN", "0"); 
			tempmap.put("CUST_MOBI_PHONE",cardInfo.getCustMobiPhone()); 
			tempmap.put("EXTEND1", ""); 
			tempmap.put("EXTEND2", ""); 
			tempmap.put("EXTEND3", ""); 
			tempmap.put("USER_ID", cardInfo.getSignUser()); 
			service.insert("JJK_SIGN_INFO.addSignInfo", tempmap);
			logger.info(">>>>>>>开通短信通知签约 [flagSignSbank] 保存数据库操作：JJK_SIGN_INFO.addSignInfo");
		}
		//是否开ATM转账  [flagSignRes1]
		if(cardInfo.getFlagSignRes1()!=null && cardInfo.getFlagSignRes1().equals("1")){ //开通
			Map<String, Object> tempmap = new HashMap<String, Object>();
			tempmap.put("TRANS_ID", cardInfo.getTransId());
			tempmap.put("CARD_NO", cardInfo.getCardNo());
			tempmap.put("SIGN_TYPE", "3");   //ATM转账
			tempmap.put("IS_DOWN", "0"); 
			tempmap.put("CUST_MOBI_PHONE",cardInfo.getCustMobiPhone()); 
			tempmap.put("EXTEND1", ""); 
			tempmap.put("EXTEND2", ""); 
			tempmap.put("EXTEND3", ""); 
			tempmap.put("USER_ID", cardInfo.getSignUser()); 
			service.insert("JJK_SIGN_INFO.addSignInfo", tempmap);
			logger.info(">>>>>>>开通ATM转账签约签约 [flagSignRes1] 保存数据库操作：JJK_SIGN_INFO.addSignInfo");
		}
		
		//只有开卡 和 开卡签约保存身份证图片信息
		if(cardInfo.getSignType().equals("1") || cardInfo.getSignType().equals("2") ){
			
			//M_NAME,SEX,NATION,BIRTH,ADDRESS,IDCARD,ISSUE,VALIDITY,VOUCHER_NO,SERIAL_NO,PICTURE
			//保存身份证图片信息
			
			Map<String, Object> imgMap = new HashMap<String, Object>();
			imgMap.put("M_NAME", cardInfo.getCustName());
			imgMap.put("SEX", cardInfo.getCustSex());
			imgMap.put("NATION", cardInfo.getDecaRes2());
			imgMap.put("BIRTH", cardInfo.getCustBorn());
			imgMap.put("ADDRESS", cardInfo.getCustAddrHome());
			imgMap.put("IDCARD", cardInfo.getCertNo());
			imgMap.put("ISSUE", cardInfo.getSignOrgan());
			imgMap.put("VALIDITY", cardInfo.getCertEndDate());
			imgMap.put("VOUCHER_NO",rst.get("pzhm"));//凭证号码
			imgMap.put("SERIAL_NO", cardInfo.getTransId());
			
			imgMap.put("PICTURE",certImg);

			service.insert("MGG_CUST_INFO.addIDimgInfo", imgMap);
		}
		
		
		
				
		
		
		
		if(ok){
			
			rstNew.put("transId", cardInfo.getTransId());
			rstNew.put("STATUS", "1");
			rstNew.put("MSG", "交易成功！");
			bizLogger.info("用户开卡申请成功","321101");
		}
		else{
			rstNew.put("STATUS", "0");
			rstNew.put("MSG", "交易失败！");
			bizLogger.warn("用户开卡申请失败，失败原因:信息未成功保存","321102");
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug(">> base context:\n"
					+ StringUtil.formatXmlStr(busiCtx.getBaseContext().asXML()));
			logger.debug(">> rstNew info:\n" + rstNew.toString());
		}
		
		return rstNew;
		
		
	}
	
	
	/**
	 * 实时开卡图片上传
	 * @param transId
	 * @param request
	 * @return
	 * @throws java.io.FileNotFoundException
	 * @throws java.io.IOException
	 */
	@RequestMapping("attaUpload")
	@ResponseBody
	public Map<String, Object> attaUpload(String transId,
			MultipartHttpServletRequest request){
		Assert.notNull(transId, "transId不能为空");
		logger.info("transId："+transId);
		//Assert.notNull(tranDecardService.findByPrimaryKey(transId), "找不到对应的开卡记录");
		Map<String, Object> rtn = upload(transId,request,customFileMngService, mggImageAttaService);
		/*Map<String, MultipartFile> fileMap = request.getFileMap();
		Date date = new Date();
		try{
			for (Entry<String, MultipartFile> entry : fileMap.entrySet()) {
				MultipartFile file = entry.getValue();
				
				String fileType=getFileTypeByFileName(file.getOriginalFilename());
				if(!file.isEmpty())
				{
					logger.info("上传图片文件信息："+entry.getValue());
					Collection<String> tmp = customFileMngService.saveMultipartFile(
							file, CustomFileType.getType(fileType)).values();
					for (String fileName : tmp) {
						TranAtta atta = new TranAtta();
						atta.setTransId(transId);
						atta.setTranCode(ConfigEnum.DICT_TRAN_CODE_OPEN_CARD.strVal());
						atta.setAttaDirUrl(fileName);
						atta.setAttaName(entry.getKey());
						atta.setAttaSize(new BigDecimal(file.getSize()));
						atta.setAttaType(ConfigEnum.DICT_ATTA_TYPE_IMAGE.strVal());
						atta.setAttaUpdaTime(date);
						tranAttaService.insert(atta);
					}
				}
				else{
					logger.error("上传图片为空！！");
					rtn.put("STATUS", "0");
					rtn.put("MSG", "上传图片为空！！");
					return  rtn;
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
			logger.error("上传图片失败！！");
			rtn.put("STATUS", "0");
			rtn.put("MSG", "上传图片失败！");
			return  rtn;
		}
		return  WebUtils.returnSuccessMsg(rtn, null);
		*/
		return rtn;
	}
	
	
	/**
	 * @Title: upload
	 * @Description: 实时开卡图片上传
	 * @param transId
	 * @param request
	 * @param customFileMngService
	 * @return
	 */
	public Map<String, Object> upload(String transId,
			MultipartHttpServletRequest request,CustomFileMngService customFileMngService,MggImageAttaService mggImageAttaService){
		Map<String, Object> rtn = new HashMap<String, Object>();

		Map<String, MultipartFile> fileMap = request.getFileMap();
		Date date = new Date();
		try{
			for (Entry<String, MultipartFile> entry : fileMap.entrySet()) {
				MultipartFile file = entry.getValue();
				
				String fileType=getFileTypeByFileName(file.getOriginalFilename());
				if(!file.isEmpty())
				{
					logger.info("上传图片文件信息："+entry.getValue());
					Collection<String> tmp = customFileMngService.saveMultipartFile(
							file, CustomFileType.getType(fileType)).values();
					for (String fileName : tmp) {
						MggImageAtta atta = new MggImageAtta();
						atta.setTransId(transId);
						atta.setTranCode(ConfigEnum.DICT_TRAN_CODE_OPEN_CARD.strVal());
						atta.setAttaDirUrl(fileName);
						atta.setAttaName(entry.getKey());
						atta.setAttaSize(new BigDecimal(file.getSize()));
						atta.setAttaType(ConfigEnum.DICT_ATTA_TYPE_IMAGE.strVal());
						atta.setAttaUpdaTime(date);
						mggImageAttaService.insert(atta);
					}
				}
				else{
					logger.error("上传图片为空！！");
					rtn.put("STATUS", "0");
					rtn.put("MSG", "上传图片为空！！");
					return  rtn;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("上传图片失败！！");
			rtn.put("STATUS", "0");
			rtn.put("MSG", "上传图片失败！");
			return  rtn;
		}
		return WebUtils.returnSuccessMsg(rtn, null);
	}
	
	/**
	 * 得到文件类型
	 * @param fileName
	 * @return
	 */
	public String getFileTypeByFileName(String fileName) {
		if(null == fileName || fileName.isEmpty()) {
			return null;
		}
		return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
	}

}
