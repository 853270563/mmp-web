package cn.com.yitong.market.jjk.openCard.controlller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.util.YTLog;

@Controller
public class CheckBussiCardPassword extends BaseControl {

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

	final String BASE_PATH = "market/jjk/openCard/";
	
	/**
	 * 列表查询
	 * 
	 * @param trans_code
	 * @param request
	 * @return
	 */
	@RequestMapping("jjk/openCard/list/CheckBussiCardPassword.do")
	@ResponseBody
	public Map list(String trans_code, HttpServletRequest request) {
		String transCode = BASE_PATH+"CheckBussiCardPassword";
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!CtxUtil.transPrev(ctx, transCode, json2MapParamCover,
				requestBuilder, confParser, rst)) {
			return rst;
		}
		// 数据库操作区
		Map params = ctx.getParamMap();
		boolean ok = false;
		try {
			// 数据库操作
			String statementName = CtxUtil.getStatement(confParser, transCode);
			String validate="1";
			if(validate.equals("1")){
				//签约客户挡板
				List<Map<String,String>> cust_info = new ArrayList<Map<String,String>>();
				Map<String,String> pb1=new HashMap<String,String>();
				pb1.put("REGISTEREDCUST", "1");
				pb1.put("CUST_ID", "123456");
				pb1.put("CUST_NAME", "战神");
				pb1.put("CARD_COUNT", "3");
				pb1.put("PHONE", "13966128322");
				pb1.put("ADDRESS", "江苏省苏州市纳米科技园C1#403");
				pb1.put("TELEPHONE", "13966128322");
				cust_info.add(pb1);
				rst.put("SIGN_INFO", cust_info);
			}
			rst.put("VALIDATE", validate);
			ok = true;
		} catch (Exception e) {
			// 输出错误的关键信息
			logger.error(ctx.getTransLogBean(transCode), e);
			ok = false;
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
