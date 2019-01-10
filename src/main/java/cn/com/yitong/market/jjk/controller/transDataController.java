package cn.com.yitong.market.jjk.controller;
import java.io.BufferedReader;
import java.io.Reader;
import java.sql.Clob;
import java.util.ArrayList;
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
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

@Controller
public class transDataController extends BaseControl {

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

	private static final String BASE_PATH = "market/jjk/transData/";
	/**
	 * 分页查询
	 * 
	 * @param trans_code
	 * @param request
	 * @return
	 */
	@RequestMapping("jjk/transData/next/{trans_code}.do")
	@ResponseBody
	public Map next(@PathVariable String trans_code, HttpServletRequest request) {
		String transCode = BASE_PATH + "TransData";
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
	@RequestMapping("jjk/transData/data/{trans_code}.do")
	@ResponseBody
	public Map data(@PathVariable String trans_code, HttpServletRequest request) {
		String transCode = BASE_PATH + "TransDataDetail";
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
		Map<String,Object> consumerInfo = new HashMap<String,Object>();
		Map<String,Object> businessInfo = new HashMap<String,Object>();
		Map<String,Object> consumer = new HashMap<String,Object>();
		Map<String,Object> business = new HashMap<String,Object>();
		
		Reader is = null;
		BufferedReader br = null;
		try {
			String certImg = ""; 
			// 数据库操作
			if(null != params.get("SIGN_TYPE")){
				if(!params.get("SIGN_TYPE").toString().equals("4")){
					params.put("transId", params.get("TRANS_ID"));
					rst= service.load("JJK_TRAN_CARD.queryTranDecardById", params);
					Clob clob =(Clob)rst.get("certImg");
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
				}else{
					params.put("BUSINESSNUM", params.get("TRANS_ID"));
					 // 数据库操作
		        	//获取信贷基本信息以及个人基本信息
		        	Map<Object,Object> recheck = service.load("CREDIT_APPLY.getCreditDetail", params);
		        	if (recheck == null) {
		    			request.setAttribute("MSG","无此记录信息");
		    			request.setAttribute("STATUS",0);
		    			return recheck;
		    		}
		    		if(String.valueOf(recheck.get("EXAM_TYPE")).equals("1")){
		    			Map<String,Object> details = service.load("CREDIT_APPLY.loadCreditType", params);//获取信贷信息
		    			String creditType= String.valueOf(details.get("CREDITTYPE"));
		    			recheck.put("CREDITTYPE", creditType);
		    			if(null!=details){
		        			recheck.putAll(details);
		    			}
		    			Map<String,Object> assest = new HashMap<String,Object>();
		    			List<String> assesttype = new ArrayList<String>();
		    			assesttype.add("1");
		    			assesttype.add("2");
		    			assest.put("ASSESTTYPE", assesttype);
		    			assest.put("BUSINESSNUM", recheck.get("BUSINESSNUM"));
		    			consumer=service.load("CREDIT_APPLY.loadAssestInfo",assest);//获取个人资产
		    			if(null!=consumer){
		    				recheck.putAll(consumer);
		    			}
		    			assesttype.add("3");
		    			assesttype.add("4");
		    			assest.put("ASSESTTYPE", assesttype);
		    			business=service.load("CREDIT_APPLY.loadAssestInfo",assest);//获取企业资产
		    			recheck.put("COM_ASSESTTYPE", business.get("ASSESTTYPE"));
		    			recheck.put("COM_ASSESTAGREEDVALUE", business.get("ASSESTAGREEDVALUE"));
		    			recheck.put("COM_ASSESTOWNEDVALUE", business.get("ASSESTOWNEDVALUE"));
		    			recheck.put("COM_ASSESTSHAREVALUE", business.get("ASSESTSHAREVALUE"));
		    			recheck.put("COM_ASSESTNETVALUE", business.get("ASSESTNETVALUE"));
		    			recheck.put("COM_ISCOMASSEST", business.get("ISCOMASSEST"));
		    			Map<String,Object> info = new HashMap<String,Object>();
		    			info.put("BUSINESSNUM", recheck.get("BUSINESSNUM"));
		    			if("0".equals(creditType)){
		    				businessInfo = service.load("CREDIT_APPLY.loadBusinessInfo",info);//获取企业贷信息
		    				if(null!=businessInfo){
		    					recheck.putAll(businessInfo);
		    				}
		    			}else{
		    				consumerInfo = service.load("CREDIT_APPLY.loadConsumerInfo",info);//获取消费贷信息
		    				if(null!=consumerInfo){
		    					recheck.putAll(consumerInfo);
		    				}
		    			}
		    		}
		    		rst.putAll(recheck);
		    		ok=true;
				}
			}
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
}
