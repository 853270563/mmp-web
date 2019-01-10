package cn.com.yitong.market.mm.mhj.fundProd.controller;

import cn.com.yitong.consts.NS;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MhjFundProdInfo extends BaseControl {

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
	 * 列表查询
	 * 
	 * @param trans_code
	 * @param request
	 * @return
	 */
	@RequestMapping("mm/mhj/prodInfo/list/{trans_code}.do")
	@ResponseBody
	public Map list(@PathVariable String trans_code, HttpServletRequest request) {

		String transCode = BASE_PATH + trans_code;
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
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
		//获取总条数，然后根据前端传回的信息，获取对应的信息条数
		//ONEMONTH_NUM:近一月1:10% 3:30% 5:50% 无限传空值
		//SIXMONTH_NUM:近六个月1:10% 3:30% 5:50% 无限传空值
		Map  totalNum=new HashMap();
		int onenum = Integer.parseInt(params.get("ONEMONTH_NUM")!=null?
				params.get("ONEMONTH_NUM").toString():"0");
		int sixnum = Integer.parseInt(params.get("SIXMONTH_NUM")!=null?
				params.get("SIXMONTH_NUM").toString():"0");
		int num=0;
		if(onenum!=0){
			num=onenum;
		}
		if(sixnum!=0){
			num=sixnum;
		}
		int iPageSize = 0;
		if(num!=0){
			totalNum=service.load("MHJ_WMS_PEPRODINFO.totalCount", params);
			int nums =Integer.parseInt(totalNum.get("CNT").toString());
			int size=0;
			if(num==1){
				size=(int)Math.round(nums*0.1);
			}else if(num==3){
				size=(int)Math.round(nums*0.3);
			}else if(num==5){
				size=(int)Math.round(nums*0.5);
			}
			iPageSize = StringUtil.getInt(params, NS.PAGE_SIZE, size);//赋值
		}else{
			iPageSize = StringUtil.getInt(params, NS.PAGE_SIZE, 10);//初始值
		}
		int iStartId = StringUtil.getInt(params, NS.NEXT_KEY, 0);
		logger.debug("iStartId:	" + iStartId);
		logger.debug("iPageSize:	" + iPageSize);
		// 设置查询条件
		params.put(NS.START_ROW, iStartId);// 起始记录号
		params.put(NS.END_ROW, iStartId + iPageSize + 1);
		
		boolean ok = false;
		try {
			// 数据库操作
			String statementName = CtxUtil.getStatement(confParser, transCode);
			//单位净值条件检索
			if(params.get("NETVALUE")!=null && !params.get("NETVALUE").equals("")){
				String NETVALUE=params.get("NETVALUE").toString();
				String[] str =NETVALUE.split("-");
				if(str.length!=2){
					params.put("StartNetValue", str[0]);
				}else{
					params.put("StartNetValue", str[0]);
					params.put("EndNetValue", str[1]);
				}
			}
			//累计分红条件检索
			if(params.get("SUMCUTAMT")!=null && !params.get("SUMCUTAMT").equals("")){
				String sumcutamt = params.get("SUMCUTAMT").toString();
				String[] str =sumcutamt.split("-");
				if(str.length!=2){
					params.put("StartSumRate", str[0]);
				}else{
					params.put("StartSumRate", str[0]);
					params.put("EndSumRate", str[1]);
				}
			}
			//最近一月回报率区间条件检索
			if(params.get("ONEMONTH")!=null && !params.get("ONEMONTH").equals("")){
				String ONEMONTH=params.get("ONEMONTH").toString();
				String[] str =ONEMONTH.split("-");  
				if(str.length!=2){
					params.put("StartOneRate", str[0]);
				}else{
					params.put("StartOneRate", str[0]);
					params.put("EndOneRate", str[1]);
				}
			}
			//最近三个月回报率区间条件检索
			if(params.get("THREEMONTH")!=null && !params.get("THREEMONTH").equals("")){
				String THREEMONTH=params.get("THREEMONTH").toString();
				String[] str =THREEMONTH.split("-");  
				if(str.length!=2){
					params.put("StartThreeRate", str[0]);
				}else{
					params.put("StartThreeRate", str[0]);
					params.put("EndThreeRate", str[1]);
					}
			}
			//最近六个月回报率区间
			if(params.get("SIXMONTH")!=null && !params.get("SIXMONTH").equals("")){
				String SIXMONTH=params.get("SIXMONTH").toString();
				String[] str =SIXMONTH.split("-");  
				if(str.length!=2){
					params.put("StartSixRate", str[0]);
				}else{
					params.put("StartSixRate", str[0]);
					params.put("EndSixRate", str[1]);
				}
			}
			
			List data = service.findList(statementName, params);
			logger.debug("datas size:" + data.size());
			int nextKey = 0;
			if (data != null && (data.size() > iPageSize)) {
				nextKey = iStartId + iPageSize;
				data.remove(data.size() - 1);
			}
			if (nextKey > 0) {
				rst.put("NEXT_KEY", String.valueOf(nextKey));
			}
			
			rst.put(NS.LIST, data);
			ok = true;
		} catch (Exception e) {
			// 输出错误的关键信息
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}
	public static void main(String[] args) {
		double a = 0.1;
		int b = 11;
		double c =b*a;
		int d = (int)Math.round(c);
		System.out.println(d);
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
		CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
	}
	
	
}
