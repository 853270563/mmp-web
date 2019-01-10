package cn.com.yitong.market.mtt.controller;

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

import cn.com.yitong.ares.login.BizLogger;
import cn.com.yitong.common.utils.SpringContextUtils;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.util.FileUtil;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;
import net.sf.json.JSONObject;

@RequestMapping("mtt/wisdomHall")
@Controller
public class WisdomHallManager extends BaseControl {
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

	final String BASE_PATH = "market/mtt/wisdomHallData/";

    final BizLogger bizLogger = BizLogger.getLogger(this.getClass());

	@RequestMapping("busiQueuingInfo/{trans_code}.do")	
	@ResponseBody
	public Map<String,Object> ytdManager(@PathVariable String trans_code, HttpServletRequest request) {
		String transCode = BASE_PATH + trans_code;
		Map<String,Object> rst = new HashMap<String,Object>();
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
		Map<String,Object> params = ctx.getParamMap();
		boolean ok = false;
		try {
			// 数据库操作
			String statementName = CtxUtil.getStatement(confParser, transCode);
			//挡板begin
			String filePath = SpringContextUtils.getApplicationContext().getResource("").getFile().getPath() + "/WEB-INF/data/wisdomHallData/" + trans_code
					+ ".json";
			String responseStr = getContent(filePath);
			JSONObject jo = JSONObject.fromObject(responseStr);
			rst = jo;
			//挡板end
			ok=true;
			bizLogger.info("交易成功！", "100402");
		} catch (Exception e) {
			// 输出错误的关键信息
			transAfter(ctx, transCode, rst, false);
			rst.put("MSG", "交易失败!");
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}
	
	@RequestMapping("latestInfo/{trans_code}.do")
	@ResponseBody
	public Map<String,Object> latestInfo(@PathVariable String trans_code, HttpServletRequest request) {
		String transCode = BASE_PATH + trans_code;
		Map<String,Object> rst = new HashMap<String,Object>();
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
		Map<String,Object> params = ctx.getParamMap();
		boolean ok = false;
		try {
			// 数据库操作
			String statementName = CtxUtil.getStatement(confParser, transCode);
			//挡板begin
			String filePath = SpringContextUtils.getApplicationContext().getResource("").getFile().getPath() + "/WEB-INF/data/wisdomHallData/" + trans_code
					+ ".json";
			String responseStr = getContent(filePath);
			JSONObject jo = JSONObject.fromObject(responseStr);
			rst = jo;
			//挡板end
			ok=true;
			bizLogger.info("交易成功！", "100402");
		} catch (Exception e) {
			// 输出错误的关键信息
			transAfter(ctx, transCode, rst, false);
			rst.put("MSG", "交易失败!");
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}
	@RequestMapping("warnningInfo/{trans_code}.do")
	@ResponseBody
	public Map<String,Object> warningInfo(@PathVariable String trans_code, HttpServletRequest request) {
		String transCode = BASE_PATH + trans_code;
		Map<String,Object> rst = new HashMap<String,Object>();
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
		Map<String,Object> params = ctx.getParamMap();
		boolean ok = false;
		try {
			// 数据库操作
			String statementName = CtxUtil.getStatement(confParser, transCode);
			//挡板begin
			String filePath = SpringContextUtils.getApplicationContext().getResource("").getFile().getPath() + "/WEB-INF/data/wisdomHallData/" + trans_code
					+ ".json";
			String responseStr = getContent(filePath);
			JSONObject jo = JSONObject.fromObject(responseStr);
			rst = jo;
			//挡板end
			ok=true;
			bizLogger.info("交易成功！", "100402");
		} catch (Exception e) {
			// 输出错误的关键信息
			transAfter(ctx, transCode, rst, false);
			rst.put("MSG", "交易失败!");
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}
	
	@RequestMapping("queryCustInfo/{trans_code}.do")
	@ResponseBody
	public Map<String,Object> queryCustInfo(@PathVariable String trans_code, HttpServletRequest request) {
		String transCode = BASE_PATH + trans_code;
		Map<String,Object> rst = new HashMap<String,Object>();
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
		Map<String,Object> params = ctx.getParamMap();
		boolean ok = false;
		try {
			// 数据库操作
			String statementName = CtxUtil.getStatement(confParser, transCode);
			//挡板begin
			String filePath = SpringContextUtils.getApplicationContext().getResource("").getFile().getPath() + "/WEB-INF/data/wisdomHallData/" + trans_code
					+ ".json";
			String responseStr = getContent(filePath);
			JSONObject jo = JSONObject.fromObject(responseStr);
			rst = jo;
			//挡板end
			ok=true;
			bizLogger.info("交易成功！", "100402");
		} catch (Exception e) {
			// 输出错误的关键信息
			transAfter(ctx, transCode, rst, false);
			rst.put("MSG", "交易失败!");
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}
	@RequestMapping("shiftNo/{trans_code}.do")
	@ResponseBody
	public Map<String,Object> shiftNo(@PathVariable String trans_code, HttpServletRequest request) {
		String transCode = BASE_PATH + trans_code;
		Map<String,Object> rst = new HashMap<String,Object>();
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
		Map<String,Object> params = ctx.getParamMap();
		boolean ok = false;
		try {
			// 数据库操作
			String statementName = CtxUtil.getStatement(confParser, transCode);
			//挡板begin
			String filePath = SpringContextUtils.getApplicationContext().getResource("").getFile().getPath() + "/WEB-INF/data/wisdomHallData/" + trans_code
					+ ".json";
			String responseStr = getContent(filePath);
			JSONObject jo = JSONObject.fromObject(responseStr);
			rst = jo;
			//挡板end
			ok=true;
			bizLogger.info("交易成功！", "100402");
		} catch (Exception e) {
			// 输出错误的关键信息
			transAfter(ctx, transCode, rst, false);
			rst.put("MSG", "交易失败!");
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}
	@RequestMapping("abandonNo/{trans_code}.do")
	@ResponseBody
	public Map<String,Object> abandonNo(@PathVariable String trans_code, HttpServletRequest request) {
		String transCode = BASE_PATH + trans_code;
		Map<String,Object> rst = new HashMap<String,Object>();
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
		Map<String,Object> params = ctx.getParamMap();
		boolean ok = false;
		try {
			// 数据库操作
			String statementName = CtxUtil.getStatement(confParser, transCode);
			//挡板begin
			String filePath = SpringContextUtils.getApplicationContext().getResource("").getFile().getPath() + "/WEB-INF/data/wisdomHallData/" + trans_code
					+ ".json";
			String responseStr = getContent(filePath);
			JSONObject jo = JSONObject.fromObject(responseStr);
			rst = jo;
			//挡板end
			ok=true;
			bizLogger.info("交易成功！", "100402");
		} catch (Exception e) {
			// 输出错误的关键信息
			transAfter(ctx, transCode, rst, false);
			rst.put("MSG", "交易失败!");
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}
	@RequestMapping("invedNoAct/{trans_code}.do")
	@ResponseBody
	public Map<String,Object> invedNoAct(@PathVariable String trans_code, HttpServletRequest request) {
		String transCode = BASE_PATH + trans_code;
		Map<String,Object> rst = new HashMap<String,Object>();
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
		Map<String,Object> params = ctx.getParamMap();
		boolean ok = false;
		try {
			// 数据库操作
			String statementName = CtxUtil.getStatement(confParser, transCode);
			//挡板begin
			String filePath = SpringContextUtils.getApplicationContext().getResource("").getFile().getPath() + "/WEB-INF/data/wisdomHallData/" + trans_code
					+ ".json";
			String responseStr = getContent(filePath);
			JSONObject jo = JSONObject.fromObject(responseStr);
			rst = jo;
			//挡板end
			ok=true;
			bizLogger.info("交易成功！", "100402");
		} catch (Exception e) {
			// 输出错误的关键信息
			transAfter(ctx, transCode, rst, false);
			rst.put("MSG", "交易失败!");
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}
	@RequestMapping("queryWindowsList/{trans_code}.do")
	@ResponseBody
	public Map<String,Object> queryWindowsList(@PathVariable String trans_code, HttpServletRequest request) {
		String transCode = BASE_PATH + trans_code;
		Map<String,Object> rst = new HashMap<String,Object>();
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
		Map<String,Object> params = ctx.getParamMap();
		boolean ok = false;
		try {
			// 数据库操作
			String statementName = CtxUtil.getStatement(confParser, transCode);
			//挡板begin
			String filePath = SpringContextUtils.getApplicationContext().getResource("").getFile().getPath() + "/WEB-INF/data/wisdomHallData/" + trans_code
					+ ".json";
			String responseStr = getContent(filePath);
			JSONObject jo = JSONObject.fromObject(responseStr);
			rst = jo;
			//挡板end
			ok=true;
			bizLogger.info("交易成功！", "100402");
		} catch (Exception e) {
			// 输出错误的关键信息
			transAfter(ctx, transCode, rst, false);
			rst.put("MSG", "交易失败!");
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}
	@RequestMapping("changeWinPriority/{trans_code}.do")
	@ResponseBody
	public Map<String,Object> changeWinPriority(@PathVariable String trans_code, HttpServletRequest request) {
		String transCode = BASE_PATH + trans_code;
		Map<String,Object> rst = new HashMap<String,Object>();
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
		Map<String,Object> params = ctx.getParamMap();
		boolean ok = false;
		try {
			// 数据库操作
			String statementName = CtxUtil.getStatement(confParser, transCode);
			//挡板begin
			String filePath = SpringContextUtils.getApplicationContext().getResource("").getFile().getPath() + "/WEB-INF/data/wisdomHallData/" + trans_code
					+ ".json";
			String responseStr = getContent(filePath);
			JSONObject jo = JSONObject.fromObject(responseStr);
			rst = jo;
			//挡板end
			ok=true;
			bizLogger.info("交易成功！", "100402");
		} catch (Exception e) {
			// 输出错误的关键信息
			transAfter(ctx, transCode, rst, false);
			rst.put("MSG", "交易失败!");
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}
	@RequestMapping("referral/{trans_code}.do")
	@ResponseBody
	public Map<String,Object> referral(@PathVariable String trans_code, HttpServletRequest request) {
		String transCode = BASE_PATH + trans_code;
		Map<String,Object> rst = new HashMap<String,Object>();
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
		Map<String,Object> params = ctx.getParamMap();
		boolean ok = false;
		try {
			// 数据库操作
			String statementName = CtxUtil.getStatement(confParser, transCode);
			//挡板begin
			String filePath = SpringContextUtils.getApplicationContext().getResource("").getFile().getPath() + "/WEB-INF/data/wisdomHallData/" + trans_code
					+ ".json";
			String responseStr = getContent(filePath);
			JSONObject jo = JSONObject.fromObject(responseStr);
			rst = jo;
			//挡板end
			ok=true;
			bizLogger.info("交易成功！", "100402");
		} catch (Exception e) {
			// 输出错误的关键信息
			transAfter(ctx, transCode, rst, false);
			rst.put("MSG", "交易失败!");
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}
	@RequestMapping("referralInfo/{trans_code}.do")
	@ResponseBody
	public Map<String,Object> queryReferralInfo(@PathVariable String trans_code, HttpServletRequest request) {
		String transCode = BASE_PATH + trans_code;
		Map<String,Object> rst = new HashMap<String,Object>();
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
		Map<String,Object> params = ctx.getParamMap();
		boolean ok = false;
		try {
			// 数据库操作
			String statementName = CtxUtil.getStatement(confParser, transCode);
			//挡板begin
			String filePath = SpringContextUtils.getApplicationContext().getResource("").getFile().getPath() + "/WEB-INF/data/wisdomHallData/" + trans_code
					+ ".json";
			String responseStr = getContent(filePath);
			JSONObject jo = JSONObject.fromObject(responseStr);
			rst = jo;
			//挡板end
			ok=true;
			bizLogger.info("交易成功！", "100402");
		} catch (Exception e) {
			// 输出错误的关键信息
			transAfter(ctx, transCode, rst, false);
			rst.put("MSG", "交易失败!");
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}
	@RequestMapping("referralOperation/{trans_code}.do")
	@ResponseBody
	public Map<String,Object> referralOperation(@PathVariable String trans_code, HttpServletRequest request) {
		String transCode = BASE_PATH + trans_code;
		Map<String,Object> rst = new HashMap<String,Object>();
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
		Map<String,Object> params = ctx.getParamMap();
		boolean ok = false;
		try {
			// 数据库操作
			String statementName = CtxUtil.getStatement(confParser, transCode);
			//挡板begin
			String filePath = SpringContextUtils.getApplicationContext().getResource("").getFile().getPath() + "/WEB-INF/data/wisdomHallData/" + trans_code
					+ ".json";
			String responseStr = getContent(filePath);
			JSONObject jo = JSONObject.fromObject(responseStr);
			rst = jo;
			//挡板end
			ok=true;
			bizLogger.info("交易成功！", "100402");
		} catch (Exception e) {
			// 输出错误的关键信息
			transAfter(ctx, transCode, rst, false);
			rst.put("MSG", "交易失败!");
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}
	@RequestMapping("addIntention/{trans_code}.do")
	@ResponseBody
	public Map<String,Object> addIntention(@PathVariable String trans_code, HttpServletRequest request) {
		String transCode = BASE_PATH + trans_code;
		Map<String,Object> rst = new HashMap<String,Object>();
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
		Map<String,Object> params = ctx.getParamMap();
		boolean ok = false;
		try {
			// 数据库操作
			String statementName = CtxUtil.getStatement(confParser, transCode);
			//挡板begin
			String filePath = SpringContextUtils.getApplicationContext().getResource("").getFile().getPath() + "/WEB-INF/data/wisdomHallData/" + trans_code
					+ ".json";
			String responseStr = getContent(filePath);
			JSONObject jo = JSONObject.fromObject(responseStr);
			rst = jo;
			//挡板end
			ok=true;
			bizLogger.info("交易成功！", "100402");
		} catch (Exception e) {
			// 输出错误的关键信息
			transAfter(ctx, transCode, rst, false);
			rst.put("MSG", "交易失败!");
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}
	@RequestMapping("queryIntention/{trans_code}.do")
	@ResponseBody
	public Map<String,Object> queryIntention(@PathVariable String trans_code, HttpServletRequest request) {
		String transCode = BASE_PATH + trans_code;
		Map<String,Object> rst = new HashMap<String,Object>();
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
		Map<String,Object> params = ctx.getParamMap();
		boolean ok = false;
		try {
			// 数据库操作
			String statementName = CtxUtil.getStatement(confParser, transCode);
			//挡板begin
			String filePath = SpringContextUtils.getApplicationContext().getResource("").getFile().getPath() + "/WEB-INF/data/wisdomHallData/" + trans_code
					+ ".json";
			String responseStr = getContent(filePath);
			JSONObject jo = JSONObject.fromObject(responseStr);
			rst = jo;
			//挡板end
			ok=true;
			bizLogger.info("交易成功！", "100402");
		} catch (Exception e) {
			// 输出错误的关键信息
			transAfter(ctx, transCode, rst, false);
			rst.put("MSG", "交易失败!");
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}
	private String getContent(String filePath) {
		String responseStr="";
		try {
			if(StringUtil.isNotEmpty(filePath)){
				return responseStr = FileUtil.readFileAsString(filePath, "utf-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseStr;
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
