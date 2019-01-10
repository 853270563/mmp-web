package cn.com.yitong.market.mm.mhj.fundValue.controller;

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
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.datediff.DateTool;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.service.IAppVersionService;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.util.YTLog;

@Controller
public class MhjFundValue extends BaseControl {

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

	final String BASE_PATH = "crud/";
	
	/**
	 * 列表查询
	 * 
	 * @param trans_code
	 * @param request
	 * @return
	 */
	@RequestMapping("mm/mhj/value/list/{trans_code}.do")
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
		String[] cLabel=null;
		String[] vLabel=null;
		String[] flow = null;
		String[] accFlow=null;
		boolean ok = false;
		try {
			// 数据库操作
			String statementName = CtxUtil.getStatement(confParser, transCode);
			int monNumer=1;
			if(params.get("NUM")!=null){
				monNumer= Integer.parseInt(params.get("NUM").toString());
			}
			String endDate = DateTool.toDateString(new Date());
			//获取N个月前的时间
			String startDate = DateTool.getDate(endDate, monNumer,DateTool.MON);
			//起始时间
			params.put("START_DATE", startDate);
			rst.put("START_DATE", startDate);
			//结束时间
			params.put("END_DATE", endDate);
			rst.put("END_DATE", endDate);
			Map datas = service.load("MHJ_FUND_VALUE.queryFundNet", params);
			String maxNetValue="0.0";
			String maxTotalValue="0.0";
			if(params.get("MAXNETVALUE")!=null && params.get("MAXTOTALVALUE")!=null){
				maxNetValue=datas.get("MAXNETVALUE").toString();
				maxTotalValue=datas.get("MAXTOTALVALUE").toString();
			}
			List data = service.findList(statementName, params);
			//计算数组,用于界面计算
//			List cl=new ArrayList();
//			cl.add(oldStr);
			cLabel = DateTool.getFLabel(startDate, endDate);
			//X轴数组,用于界面展现
			vLabel = DateTool.getXLable(startDate, endDate, cLabel);
			//输出字段为数组
			flow = DateTool.getSegOutArr(cLabel, data, "PRICEDATE", "NETVALUE");
			accFlow = DateTool.getSegOutArr(cLabel, data, "PRICEDATE", "TOTALVALUE");
			//X轴数组,用于界面展现
			vLabel = DateTool.getXLable(startDate, endDate, cLabel);
			//起始度量
			rst.put("START_SCALE", 0);
			//终止度量
			rst.put("END_SCALE", maxNetValue); //最大净值
			//度量步长
			double d = Double.parseDouble(maxNetValue);
			
			rst.put("SCALE_SPACE", d/10);//最大净值/10
			
			rst.put("V_LABEL", vLabel);
			rst.put("C_LABEL", cLabel);
			//净值
			rst.put("FLOW", flow);
			//累计净值
			rst.put("ACC_FLOW",accFlow);
			
			//rst.put(NS.LIST, data);
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
