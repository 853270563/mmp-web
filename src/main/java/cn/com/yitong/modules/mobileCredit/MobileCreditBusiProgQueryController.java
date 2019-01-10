package cn.com.yitong.modules.mobileCredit;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.INetTools;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.net.impl.credit.CreditConst;
import cn.com.yitong.framework.net.impl.esb.ESBConst;
import cn.com.yitong.framework.service.IThirdPatryMessageService;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.util.MessageTools;
import cn.com.yitong.util.YTLog;
/***
 * 
 * @author xushuhuai
 *
 */
@Controller
public class MobileCreditBusiProgQueryController extends BaseControl {
	private Logger logger = YTLog.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("netTools4creditTest")
	private INetTools netTools4credit;
	
	@Autowired
	@Qualifier("requestBuilder4credit")
	IRequstBuilder requestBuilder;
	
	@Autowired
	@Qualifier("responseParser4credit")
	IResponseParser responseParser;

	@Autowired
	@Qualifier("EBankConfParser4credit")
	IEBankConfParser confParser;
	
	@Autowired
	private IThirdPatryMessageService thirdPatryMessageService;
	
	/***
	 * 查询客户经理名下业务进度
	 * @param request
	 * @return
	 */
	@RequestMapping("credit/creditBusiProgQuery.do")
	@ResponseBody
	public Map<String,Object> busiProgQuery(HttpServletRequest request){
		String transCode = "credit/creditBusiProgQuery";
		IBusinessContext ctx = CtxUtil.createMapContext(request);
		Map<String,Object> rst = new HashMap<String,Object>();
		
		if(!ctx.initParamCover(json2MapParamCover, transCode, false)){
			return rst;
		}
		
		boolean ok = thirdPatryMessageService.creditLoanSystem(ctx, transCode, rst);
		
		if(ok){
			ctx.setSuccessInfo(AppConstants.STATUS_OK, AppConstants.MSG_SUCC, transCode);
		}
		MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
		
		return rst;
	}
	/***
	 * 进度查询详情
	 * @param request
	 * @return
	 */
	@RequestMapping("credit/creditBusiProgDetailQuery.do")
	@ResponseBody
	public Map<String,Object> busiProgDetailQuery(HttpServletRequest request){
		String transCode = "credit/creditBusiProgDetailQuery";
		IBusinessContext ctx = CtxUtil.createMapContext(request);
		Map<String,Object> rst = new HashMap<String,Object>();
		
		if(!ctx.initParamCover(json2MapParamCover, transCode, false)){
			return rst;
		}
		
		boolean ok = thirdPatryMessageService.creditLoanSystem(ctx, transCode, rst);
		
		if(ok){
			ctx.setSuccessInfo(AppConstants.STATUS_OK, AppConstants.MSG_SUCC, transCode);
		}
		MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
		
		return rst;
	}
}
