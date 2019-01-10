package cn.com.yitong.market.jjk.controller;

import java.io.BufferedReader;
import java.io.Reader;
import java.sql.Clob;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.yitong.consts.NS;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.MBTransConfBean;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.market.jjk.service.CustomFileMngService;
import cn.com.yitong.market.jjk.service.JjkTranCardService;
import cn.com.yitong.market.jjk.service.MggImageAttaService;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

@Controller
public class OpenCardListController extends BaseControl {

	private Logger logger = YTLog.getLogger(this.getClass());
	@Autowired
	ICrudService service;
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

	private static final String BASE_PATH = "tranDeCard/";
	@Resource
	private JjkTranCardService jjkTranCardService;
	@Resource
	private CustomFileMngService customFileMngService;
	@Resource
	private MggImageAttaService mggImageAttaService;
	/**
	 * 分页查询
	 * 
	 * @param trans_code
	 * @param request
	 * @return
	 */
	@RequestMapping("jjk/opencard/next/{trans_code}.do")
	@ResponseBody
	public Map next(@PathVariable String trans_code, HttpServletRequest request) {
		String transCode = BASE_PATH + trans_code;
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = CtxUtil.createMapContext(request);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}
		if (CtxUtil.debugTrans(trans_code)) {
			boolean ok = client.execute(ctx, transCode);
			transAfter(ctx, transCode, rst, ok);
			return rst;
		}
		MBTransConfBean conf = confParser.findTransConfById(transCode);
		String statementName = conf.getProperty(NS.IBATIS_STATEMENT);
		// 数据库操作区
		Map params = ctx.getParamMap();
		int iStartId = StringUtil.getInt(params, NS.NEXT_KEY, 0);
		int iPageSize = StringUtil.getInt(params, NS.PAGE_SIZE, 10);

		logger.debug("iStartId:	" + iStartId);
		logger.debug("iPageSize:	" + iPageSize);
		// 设置查询条件
		params.put(NS.START_ROW, iStartId);// 起始记录号
		params.put(NS.END_ROW, iStartId + iPageSize + 1);
		boolean ok = false;
		try {
			
			List datas = service.findList(statementName, params);
			// 数据库操作// 数据
			logger.debug("datas size:" + datas.size());
			int nextKey = 0;
			if (datas != null && (datas.size() > iPageSize)) {
				nextKey = iStartId + iPageSize;
				datas.remove(datas.size() - 1);
			}
			if (nextKey > 0) {
				rst.put("NEXT_KEY", String.valueOf(nextKey));
			}
			rst.put(NS.LIST, datas);
			ok = true;
		} catch (Exception e) {

			// 输出错误的关键信息
			logger.error(ctx.getTransLogBean(transCode), e);
			rst.put("STATUS", "0");
			rst.put("MSG", "交易失败！");
			return rst;
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}
	
	
	
	/**
	 * 详情查询
	 * 
	 * @param trans_code
	 * @param request
	 * @return
	 */
	@RequestMapping("jjk/opencard/data/{trans_code}.do")
	@ResponseBody
	public Map data(@PathVariable String trans_code, HttpServletRequest request) {
		String transCode = BASE_PATH + trans_code;
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = CtxUtil.createMapContext(request);

		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}
		if (CtxUtil.debugTrans(trans_code)) {
			boolean ok = client.execute(ctx, transCode);
			transAfter(ctx, transCode, rst, ok);
			return rst;
		}
		// 数据库操作区
		Map params = ctx.getParamMap();
		boolean ok = false;
		Reader is = null;
	    BufferedReader br = null;
		try {
			// 数据库操作
			String statementName = CtxUtil.getStatement(confParser, transCode);
			rst= service.load(statementName, params);
			
			Clob clob =(Clob)rst.get("certImg");
		    String certImg = ""; 
		  
		    if(clob!=null)
		    {
		    	is = clob.getCharacterStream();// 得到流 
		    	br = new BufferedReader(is); 
		        String s = br.readLine(); 
		        StringBuffer sb = new StringBuffer(); 
		        while (s != null) {// 执行循环将字符串全部取出付值给StringBuffer由StringBuffer转成STRING 
		            sb.append(s); 
		            s = br.readLine(); 
		        } 
		        certImg = sb.toString(); 

		    }
			rst.put("certImg", certImg);
			
			//详情不现实图片 
			
/*			String transId =(String)rst.get("transId");
			Map tempMap= new HashMap();
			tempMap.put("transId", transId);
			//tempMap.put("tranCode", "01");
			List<Map> attrList = service.findList("MGG_IMAGE_ATTA.queryImageAttaByTransId", tempMap);
			if (null != attrList) {
				for (Map atta :  ) {
					
					String attaUrl = CustomFileMngController.ZIP_PARENT_URL+ atta.get("attaDirUrl");
					atta.put("attaUrl", attaUrl);
				}
			}
			rst.put("attrList", attrList);*/
			ok = true;
		} catch (Exception e) {
			// 输出错误的关键信息
			logger.error(ctx.getTransLogBean(transCode), e);
			ok=false;
		}finally{
			if(is!=null){
				try {
					is.close();
				} catch (Exception e2) {
					logger.error(e2.getMessage(),e2);
				}
			}
			
			if(br!=null){
				try {
					br.close();
				} catch (Exception e2) {
					logger.error(e2.getMessage(),e2);
				}
			}
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
	
/*	@RequestMapping("TranDeCardList")
	@ResponseBody
	public Map<String, Object> TranDeCardList(HttpServletRequest request) {
		String transCode = BASE_PATH + "TranDeCardList";
		Map<String, Object> rst = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = CtxUtil.createMapContext(request);
		ctx.initParamCover(json2MapParamCover, transCode, false);
		if (!requestBuilder.buildSendMessage(ctx, confParser, transCode)) {
			// 请求报文检查失败
			MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
			return rst;
		}
		Date startDate = StringUtil.isBlank(ctx.getParam("startDate")) ? new Date() : DateUtil.parseDate(ctx.getParam("startDate"), "yyyy-MM-dd");
		Date endDate = StringUtil.isBlank(ctx.getParam("endDate")) ? new Date() : DateUtil.parseDate(ctx.getParam("endDate"), "yyyy-MM-dd");
		endDate = new Date(endDate.getTime()+24*60*60*1000);
		String loginId = WebUtils.getCurrentUserId(request, ctx);
		
		String signType =ctx.getParam("signType");
		
		CriteriaExample<TranDecard> example = new CriteriaExample<TranDecard>();
		Criteria crit = example.createCriteria();
		crit.between(TranDecard.FL.signTime, startDate, endDate);
		if(loginId!=null && !loginId.isEmpty())
		{
			crit.equalTo(TranDecard.FL.signUser, loginId) ;
		}
		if(signType !=null && !signType.isEmpty()){
			crit.equalTo(TranDecard.FL.signType, signType) ;
		}
		List<TranDecard> list = tranDecardService.queryByExampleExt(example);
		
		for(int i=0;i<list.size();i++)
		{
			TranDecard cardList=list.get(i);
			
			String transId =cardList.getTransId();
//			Assert.hasText(transId, "transId 不能为空");
//			TranDecard card = tranDecardService.findByPrimaryKey(transId);
//			Assert.notNull(card, "数据库查不到对应记录");
//			//Map<String, Object> rtn = new HashMap<String, Object>();
//			rst.put("entity", card);
			CriteriaExample<TranAtta> example1 = new CriteriaExample<TranAtta>();
			Criteria crit1 = example1.createCriteria();
			crit1.equalTo(TranAtta.FL.transId, transId);
			crit1.equalTo(TranAtta.FL.tranCode,
					ConfigEnum.DICT_TRAN_CODE_OPEN_CARD.strVal());
			List<TranAtta> attrList = tranAttaService.queryByExample(example1);
			if (null != attrList) {
				for (TranAtta atta : attrList) {
					atta.setAttaUrl(CustomFileMngController.IMG_PARENT_URL
							+ atta.getAttaDirUrl());
				}
			}
			cardList.setAttrList(attrList);
		}
		// 检查响应报文,过滤冗余输出，并可解析字典数据
		ctx.setSuccessInfo(AppConstants.STATUS_OK, "", transCode);
		ctx.setResponseEntry(rst);
		responseParser.parserResponseData(ctx, confParser, transCode);
		MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
		
		rst.put("LIST", list);
		return rst;
	}*/
	

	
	
	

}
