package cn.com.yitong.market.mm.mhj.prodInfo.controller;

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
import cn.com.yitong.framework.core.bean.BusinessContext;
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
public class MhjProdinfoController extends BaseControl {

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

	final String BASE_PATH = "market/mm/mhj/prodInfo/";



	


	/**
	 * 分页查询
	 * 
	 * @param trans_code
	 * @param request
	 * @return
	 */
	@RequestMapping("mm/mhj/prodInfo/next/{trans_code}.do")
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

		logger.debug("iStartId:" + iStartId);
		logger.debug("iPageSize:" + iPageSize);
		// 设置查询条件
		params.put(NS.START_ROW, iStartId);// 起始记录号
		params.put(NS.END_ROW, iStartId + iPageSize + 1);
		boolean ok = false;
		try {
			//首次最低投资金额条件检索
			int num = 0;
			if(params.get("P_FIRST_AMT_Q")!=null && !params.get("P_FIRST_AMT_Q").equals("")){
				num = Integer.parseInt(params.get("P_FIRST_AMT_Q").toString());
				if(num<=5){
					params.put("NUM",50000);
				}else if(num<=10){
					params.put("NUM",100000);
				}else if(num<=20){
					params.put("NUM",200000);
				}else{
					params.put("NUMS",200000);
				}
			}
			//首次起点金额条件检索
			int count = 0;
			if(params.get("P_FIRST_AMT_S")!=null && !params.get("P_FIRST_AMT_S").equals("")){
				count = Integer.parseInt(params.get("P_FIRST_AMT_S").toString());
				if(count==1){
					params.put("NUM",50000);
				}else if(count==2){
					params.put("NUM",200000);
					params.put("NUMS",50000);
				}else if(count==3){
					params.put("NUM",1000000);
					params.put("NUMS",200000);
				}else{
					params.put("NUMS",1000000);
				}
			}
			//投资期限条件检索
			int time = 0;
			if(params.get("NOMINAL_DATE_S")!=null && !params.get("NOMINAL_DATE_S").equals("")){
				time = Integer.parseInt(params.get("NOMINAL_DATE_S").toString());
				if(time==1){
					params.put("DATE",30);
				}else if(time==2){
					params.put("DATE",90);
					params.put("DATES",30);
				}else if(time==3){
					params.put("DATE",180);
					params.put("DATES",90);
				}else{
					params.put("DATES",180);
				}
			}
			//产品名义期限
			int date = 0;
			if(params.get("NOMINAL_DATE_Q")!=null && !params.get("NOMINAL_DATE_Q").equals("")){
				date = Integer.parseInt(params.get("NOMINAL_DATE_Q").toString());
				if(date<=1){
					params.put("DATE",30);
				}else if(date<=3){
					params.put("DATE",90);
					params.put("DATES",30);
				}else if(date<=6){
					params.put("DATE",180);
					params.put("DATES",90);
				}else if(date<=12){
					params.put("DATE",360);
					params.put("DATES",180);
				}else{
					params.put("DATES",360);
				}
			}
			
			List<Map> datas = service.findList(statementName, params);
			for(Map data : datas){
				List<Map> fjList = service.findList("MHJ_PROD_INFO.getFuJian", data);
				for(Map fj : fjList){
					if(null!=fj.get("FILE_ADDR") && !"".equals(fj.get("FILE_ADDR"))){
						if(fj.get("FILE_ADDR").toString().trim().length()>0){
							fj.put("FILE_ADDR", "/download/userResource/resources.do?type=resources&fileName="+fj.get("FILE_ADDR"));
						}else{
							fj.put("FILE_ADDR", "");
						}
					}else{
						fj.put("FILE_ADDR", "");
					}
				}
				data.put("FJLIST", fjList);
			}
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
			
			
			Map  totalNum=service.load("MHJ_PROD_INFO.totalCount", params);
			
			int totalrows =Integer.parseInt(totalNum.get("CNT").toString());
			
			int totalPages=(totalrows+iPageSize-1)/iPageSize;
			
			System.out.println("-----------------------------------TOTAL_PAGES="+totalPages);
			rst.put("TOTAL_NUM", totalNum);
			rst.put(NS.LIST, datas);
			ok = true;
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

