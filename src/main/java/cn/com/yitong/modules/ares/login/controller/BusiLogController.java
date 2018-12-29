package cn.com.yitong.modules.ares.login.controller;

import cn.com.yitong.common.utils.JsonUtils;
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
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * LOG---业务处理相关
 * @author yaoym
 */
@Controller
public class BusiLogController extends BaseControl {

	@Autowired
	private ICrudService service;

	@Autowired
	@Qualifier("urlClient4db")
	private IClientFactory client;

	@Autowired
	@Qualifier("EBankConfParser4db")
	IEBankConfParser confParser;// 报文装载器

	@Autowired
	@Qualifier("requestBuilder4db")
	IRequstBuilder requestBuilder;// 请求报文生成器

	@Autowired
	@Qualifier("responseParser4db")
	IResponseParser responseParser;// 响应报文解析器

	private Logger logger = YTLog.getLogger(this.getClass());

	/*一般日志，重要日志，重大日志*/
	private static enum LogLevel {
		INFO,WARN,ERROR;
	}

	/**
	 * 行为日志新增
	 * @return
	 */
	@RequestMapping("ares/login/BusiLogAdd.do")
	@ResponseBody
	public Map<String, Object> add(HttpServletRequest request) {
		String transCode = "ares/login/BusiLogAdd";
		Map rst = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}
		// 演示
		if (CtxUtil.debugTrans(transCode)) {
			boolean ok = client.execute(ctx, transCode);
			transAfter(ctx, transCode, rst, ok);
			return rst;
		}
		// 数据库操作区
		Map params = ctx.getParamMap();
		params.put("LOG_LEVEL", mappingLogLevel(params.get("LOG_LEVEL")));
		boolean ok = false;
		try {
			if(null != params.get("MSG_ID") && null != params.get("DEVICE_ID")) {
				params.put("LOG_ID", UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20));
				// 数据库操作
				ok = service.insert("LOG.insert", params);
			}
		} catch (Exception e) {
			// 输出错误的关键信息
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}

	@RequestMapping("ares/login/BusiLogBatch.do")
	@ResponseBody
	public Map<String, Object> batchAdd(HttpServletRequest request) {
		String transCode = "ares/login/BusiLogBatch";
		// 输出报文结果
		Map<String, Object> rst = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}
		// 演示
		if (CtxUtil.debugTrans(transCode)) {
			boolean ok = client.execute(ctx, transCode);
			transAfter(ctx, transCode, rst, ok);
			return rst;
		}
		//--- 获取报文数组
		Map params = ctx.getParamMap();
		List<Map> logLists = JsonUtils.jsonToList(params.get("logList").toString());
		//--- 获取报文数组结束
		// 数据库操作区
		boolean ok = true;
		try {
			// 对每个日志，进行数据库操作
			for (Map<String, Object> log : logLists) {
				if(null == log || null == log.get("MSG_ID") || null == log.get("DEVICE_ID")) {
					continue;
				}
				log.put("LOG_LEVEL", mappingLogLevel(log.get("LOG_LEVEL")));
				log.put("LOG_ID", UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20));
				// 数据库操作
				ok &= service.insert("LOG.insert", log);
			}
		} catch (Exception e) {
			// 输出错误的关键信息
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		// 日志操作结束

		transAfter(ctx, transCode, rst, ok);
		return rst;
	}
	
	/**
	 * @param levelObj 值为1，2，3时返回映射值INFO,WARN,ERROR；若为空或其他不识别的值统一返回INFO
	 * @author hry@yitong.com.cn
	 * @date 2015年4月18日
	 */
	private static String mappingLogLevel(Object levelObj){
		if(levelObj == null){
			return String.valueOf(LogLevel.INFO);
		}
		String level = levelObj.toString();
		if(org.springframework.util.StringUtils.isEmpty(level)){
			return String.valueOf(LogLevel.INFO);
		}
		for (LogLevel logLevel : LogLevel.values()) {
			if(level.equals(logLevel.name()) || level.equals(String.valueOf(logLevel.ordinal()+1)))
				return logLevel.name();
		}
		return String.valueOf(LogLevel.INFO);
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
		// 生成交易结果
		CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
		// 保存交易日志
		commonService.saveJsonTransLog(ctx, transCode, rst);
	}
}