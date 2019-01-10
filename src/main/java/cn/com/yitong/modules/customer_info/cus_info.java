package cn.com.yitong.modules.customer_info;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.yitong.ares.login.BizLogger;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.util.MessageTools;
import cn.com.yitong.util.YTLog;

public class cus_info extends BaseControl{
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
	
	final String BASE_PATH = "customer_info/";
	final BizLogger bizLogger = BizLogger.getLogger(this.getClass());
	
	
	/**
	 * 用户列表
	 * @param request
	 * @return
	 */
	@RequestMapping("showCusList/showCusList.do")
	@ResponseBody
	public Map showCus(HttpServletRequest request ){
		logger.info("用户信息展示...start...");
		String transCode = BASE_PATH+"customer_info";
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 加载参数
		if (!ctx.initParamCover(json2MapParamCover, transCode,false)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
			return rst;
		}
		// 检查报文定义
		if (!requestBuilder.buildSendMessage(ctx, confParser, transCode)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
			return rst;
		}
		String userId = ctx.getSessionText("USER_ID").toString() != null ? ctx.getSessionText("USER_ID").toString() : "";
		// 数据库操作区	
		Map params = ctx.getParamMap();
		params.put("modifyUser",userId);
		logger.info(params);
		boolean isOK=false;
		logger.info("用户信息展示...end...");
		return rst;
	}	
}
