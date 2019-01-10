package cn.com.yitong.market.mm.mgg;

import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
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
import java.util.Map;

@Controller
public class MessageMngController extends BaseControl {

	private Logger logger = YTLog.getLogger(this.getClass());

	
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
	
	final String BASE_PATH = "market/mm/mgg/message/";
	
	
	/**
	 * 获取短信验证码
	 * @param request
	 * @return
	 */
	@RequestMapping("mm/mgg/message/GetSendCode.do")
	@ResponseBody
	public Map GetSendCode(HttpServletRequest request,
			boolean debug) {
		String transCode = BASE_PATH + "GetSendCode";
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
		boolean ok = true;
		try {
			logger.info("MOBILE="+params.get("MOBILE"));
			logger.info("USER_ID="+params.get("USER_ID"));
			rst.put("SEND_CODE", "0000");
			
		} catch (Exception e) {
			// 输出错误的关键信息
			ok=false;
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}
	
	
	/**
	 * 短信验证码校验
	 * @param request
	 * @return
	 */
	@RequestMapping("mm/mgg/message/CheckSendCode.do")
	@ResponseBody
	public Map CheckSendCode(HttpServletRequest request,
			boolean debug) {
		String transCode = BASE_PATH + "CheckSendCode";
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
		boolean ok = true;
		try {
			logger.info("MOBILE="+params.get("MOBILE"));
			logger.info("SEND_CODE="+params.get("SEND_CODE"));
			
			String  sendCode=(String)params.get("SEND_CODE");
			if(sendCode.equals("0000"))	{
				ok = true;
				transAfter(ctx, transCode, rst, ok);
				rst.put("MSG", "验证成功!");
			}
			else{
				ok = false;;
				transAfter(ctx, transCode, rst, ok);
				rst.put("MSG", "验证码错误!");
			}
		} catch (Exception e) {
			// 输出错误的关键信息
			ok=false;
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		return rst;
	}
	/**
	 * 事务前置处理
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