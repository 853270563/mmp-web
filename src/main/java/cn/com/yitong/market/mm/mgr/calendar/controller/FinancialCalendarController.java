package cn.com.yitong.market.mm.mgr.calendar.controller;

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

import cn.com.yitong.common.utils.StringUtils;
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
public class FinancialCalendarController extends BaseControl {

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

	final String BASE_PATH = "market/mm/mgr/calendar/";// 金融日历类的模板存放system目录下

	
	/**
	 * 金融日历相关查询接口
	 * @param trans_code
	 * @param request
	 * @return
	 */
	@RequestMapping("mm/mgr/calendar/query/{trans_code}.do")
	@ResponseBody
	public Map<String, Object> list(@PathVariable String trans_code,
			HttpServletRequest request) {
		String transCode = BASE_PATH + trans_code; // 路径
		Map<String, Object> rst = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!CtxUtil.transPrev(ctx, transCode, json2MapParamCover,
				requestBuilder, confParser, rst)) {
			return rst;
		}
		// 数据库操作区
		@SuppressWarnings("unchecked")
		Map<String, Object> params = ctx.getParamMap();
		boolean ok = false;
		// 数据库操作
		try {
			String statementName = CtxUtil.getStatement(confParser, transCode);
			@SuppressWarnings("unchecked")
			List<Object> datas = service.findList(statementName, params);
			rst.put(NS.LIST, datas);
			ok = true;
		} catch (Exception e) {
			// 输出错误的关键信息
			logger.error(ctx.getTransLogBean(transCode), e);
			ok = false;
		}
		CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
		return rst;
	}

	
	/**
	 * 金融日历相关添加信息接口
	 * @param trans_code
	 * @param request
	 * @return
	 */
	@RequestMapping("mm/mgr/calendar/add/{trans_code}.do")
	@ResponseBody
	public Map<String, Object> add(@PathVariable String trans_code, HttpServletRequest request) {
		String transCode = BASE_PATH + trans_code; // 路径
		Map<String, Object> rst = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		
		// 检查报文定义
		if (!CtxUtil.transPrev(ctx, transCode, json2MapParamCover,
				requestBuilder, confParser, rst)) {
			return rst;
		}
		
		// 数据库操作区
		@SuppressWarnings("unchecked")
		Map<String, Object> params = ctx.getParamMap();
		boolean ok = true;
		try {
			String statementName = CtxUtil.getStatement(confParser, transCode);
			String[] sqlArr = statementName.split(",");
			for(String sql : sqlArr) {
				ok &= service.insert(sql, params);//更新数据库记录
			}
		} catch (Exception e) {
			// 输出错误的关键信息
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
		return rst;
	}

	
	/**
	 * 金融日历相关更新信息接口
	 * @param trans_code
	 * @param request
	 * @return
	 */
	@RequestMapping("mm/mgr/calendar/update/{trans_code}.do")
	@ResponseBody
	public Map<String, Object> update(@PathVariable String trans_code,
			HttpServletRequest request) {
		String transCode = BASE_PATH + trans_code; // 路径
		Map<String, Object> rst = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!CtxUtil.transPrev(ctx, transCode, json2MapParamCover,
				requestBuilder, confParser, rst)) {
			return rst;
		}
		
		// 数据库操作区
		@SuppressWarnings("unchecked")
		Map<String, Object> params = ctx.getParamMap();
		boolean ok = true;
		try {
			String statementName = CtxUtil.getStatement(confParser, transCode);
			String[] sqlArr = statementName.split(",");
			for(String sql : sqlArr) {
				ok &= service.update(sql, params);//更新数据库记录
			}
		} catch (Exception e) {
			// 输出错误的关键信息
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
		
		return rst;
	}

	
	/**
	 * 金融日历相关删除信息接口
	 * @param trans_code
	 * @param request
	 * @return
	 */
	@RequestMapping("mm/mgr/calendar/delete/{trans_code}.do")
	@ResponseBody
	public Map<String, Object> delete(@PathVariable String trans_code,
			HttpServletRequest request) {
		String transCode = BASE_PATH + trans_code; // 路径
		Map<String, Object> rst = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!CtxUtil.transPrev(ctx, transCode, json2MapParamCover,
				requestBuilder, confParser, rst)) {
			return rst;
		}
		// 数据库操作区
		@SuppressWarnings("unchecked")
		Map<String, Object> params = ctx.getParamMap();
		boolean ok = true;
		try {
			String statementName = CtxUtil.getStatement(confParser, transCode);
			String[] sqlArr = statementName.split(",");
			for(String sql : sqlArr) {
				ok &= service.delete(sql, params);//更新数据库记录
			}
		} catch (Exception e) {
			// 输出错误的关键信息
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
		
		return rst;
	}
	
	/**
	 * 分页查询
	 * 
	 * @param trans_code
	 * @param request
	 * @return
	 */
	@RequestMapping("mm/mgr/calendar/next/{trans_code}.do")
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
		logger.debug("get ScheduleInfoQuery params:"+params.toString());
        //日程标题
//        Object subject = params.get("SCHED_SUBJECT");
//        if (null != subject) {
//            String subjectStr = String.valueOf(subject);
//			try {
//                params.put("SCHED_SUBJECT", new String(subjectStr.getBytes("iso-8859-1"), "UTF-8"));
//                logger.debug("get transfered SCHED_SUBJECT" + subjectStr);
//			} catch (UnsupportedEncodingException e) {
//				logger.error("SCHED_SUBJECT TRANS ERROR!",e);
//			}
//        } else {
//            params.put("SCHED_SUBJECT", "");
//		}
        //开始时间
        String timeBegin = (String)params.get("SCHED_STIME_BEGIN");
        if (StringUtils.isNotEmpty(timeBegin)) {
//            String timeBeginStr = String.valueOf(timeBegin);
            params.put("SCHED_STIME_BEGIN", timeBegin + " 00:00:00");
            logger.debug("get transfered SCHED_STIME_BEGIN" + timeBegin);
        } else {
            params.put("SCHED_STIME_BEGIN", "");
        }
        //结束时间
        String timeEnd = (String)params.get("SCHED_STIME_END");
        if (StringUtils.isNotEmpty(timeEnd)) {
//            String timeEndStr = String.valueOf(timeEnd);
            params.put("SCHED_STIME_END", timeEnd + " 23:59:59");
            logger.debug("get transfered SCHED_STIME_END" + timeEnd);
        } else {
            params.put("SCHED_STIME_END", "");
        }

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
