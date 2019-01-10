package cn.com.yitong.modules.sessionLog.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
public class SessionLogController extends BaseControl {
	
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
	
	final String BASE_PATH = "sessionLog/manage/";
	
	/**
	 * 
	 * @param trans_code
	 * @param request
	 * @return
	 */
	@RequestMapping("sessionLog/manage/{trans_code}.do")
	@ResponseBody
	public Map addSessionLog(@PathVariable String trans_code,HttpServletRequest request){
		String transCode = BASE_PATH + trans_code;
		Map rst = new HashMap<String, Object>();

		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);

		// 检查报文定义
		if (!CtxUtil.transPrev(ctx, transCode, json2MapParamCover,
				requestBuilder, confParser, rst)) {
			return rst;
		}

		// 演示
		if (CtxUtil.debugTrans(trans_code)) {
			boolean ok = client.execute(ctx, transCode);
			CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
			return rst;
		}
		// 数据库操作区
		Map params = ctx.getParamMap();
		
		String pos = (String)params.get("POS");
		boolean ok = false;
		try {
			params.put("SESS_LOG_ID", UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20));
			
			params.remove("POS");	
			if (pos != null && pos.contains("-")) {
				String[] posArray = pos.split("-");
				params.put("POS_X", posArray[0].trim());
				params.put("POS_Y", posArray[1].trim());		
			}		
				
			// 数据库操作
			ok = service.insert("SESSION_LOG.insert", params);
		} catch (Exception e) {
			// 输出错误的关键信息
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		if (ok) {
			request.getSession().setAttribute("SESS_LOG_ID", params.get("SESS_LOG_ID"));
		}
		CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
		return rst;
	}
	
}
