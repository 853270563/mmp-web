package cn.com.yitong.market.mm.mgr.prodShelf.controller;

import java.util.HashMap;
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
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.util.YTLog;

@Controller
public class ProdFavoInfoController extends BaseControl {

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

	final String BASE_PATH = "market/mm/mgr/prodshelf/";
	
	

	/**
	 * 新增
	 * 
	 * @param trans_code
	 * @param request
	 * @return
	 */
	@RequestMapping("mm/mgr/prodshelf/add/{trans_code}.do")
	@ResponseBody
	public Map add(@PathVariable String trans_code, HttpServletRequest request,
			boolean debug) {
		String transCode = BASE_PATH + trans_code;
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}
		if (debug) {
			boolean ok = client.execute(ctx, transCode);
			transAfter(ctx, transCode, rst, ok);
			return rst;
		}
		// 数据库操作区
		Map params = ctx.getParamMap();
		boolean ok = false;
		try {
			
			
			// 数据库操作
			String statementName = CtxUtil.getStatement(confParser, transCode);
			Map  tempMap= new HashMap();
			//获取序列主键PROD_FAVO_ID
			tempMap=service.load("PROD_FAVO.seqProdFavo", params);
			String  prodFavoId =  tempMap.get("PROD_FAVO_ID").toString();
            params.put("PROD_FAVO_ID", prodFavoId);
			ok = service.insert(statementName, params);
			rst.put("PROD_FAVO_ID", prodFavoId);
			
		} catch (Exception e) {
			// 输出错误的关键信息
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}
	
	/**
	 * 删除
	 * 
	 * @param trans_code
	 * @param request
	 * @return
	 */
	@RequestMapping("mm/mgr/prodshelf/delete/{trans_code}.do")
	@ResponseBody
	public Map delete(@PathVariable String trans_code,
			HttpServletRequest request, boolean debug) {
		String transCode = BASE_PATH + trans_code;
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = CtxUtil.createMapContext(request);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}
		if (debug) {
			boolean ok = client.execute(ctx, transCode);
			transAfter(ctx, transCode, rst, ok);
			return rst;
		}
		// 数据库操作区
		Map params = ctx.getParamMap();
		boolean ok = false;
		try {
			// 数据库操作
			String statementName = CtxUtil.getStatement(confParser, transCode);
			ok = service.delete(statementName, params);
			//返回数据PROD_FAVO_ID
			rst.put("PROD_FAVO_ID", params.get("PROD_FAVO_ID"));
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
