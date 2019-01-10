package cn.com.yitong.modules.esb.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
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
import cn.com.yitong.framework.service.IThirdPatryMessageService;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.util.YTLog;

/**
 * @author luanyu
 * @date   2018年1月23日
 */
@Controller
public class TailBox extends BaseControl {
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
	@Autowired
	private IThirdPatryMessageService thirdPatryMessageService;
	final String BASE_PATH = "esb/";

	@RequestMapping("esb/tailBoxBalanceQuery.do")
	@ResponseBody
	public Map<String, Object> add(HttpServletRequest request) {
		String transCode = BASE_PATH + "tailBoxBalanceQuery";
		Map<String, Object> rst = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}

		boolean ok = false;
		try {
			// 数据库操作
			ok = thirdPatryMessageService.esbUnionSystem(ctx, "00010000004401", rst);
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
	private boolean transPrev(IBusinessContext ctx, String transCode, Map<String, Object> rst) {
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
	private void transAfter(IBusinessContext ctx, String transCode, Map<String, Object> rst, boolean ok) {
		// 生成交易结果
		CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
	}
}
