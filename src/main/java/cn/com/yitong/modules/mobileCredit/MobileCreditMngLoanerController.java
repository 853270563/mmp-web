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
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;
/***
 * 客户管理
 * @author xushuhuai
 *
 */
@Controller
public class MobileCreditMngLoanerController extends BaseControl {
	private Logger logger = YTLog.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("netTools4creditTest")
	private INetTools netTools4credit;
	
	@Autowired
	@Qualifier("requestBuilder4db")
	IRequstBuilder requestBuilder;
	
	@Autowired
	@Qualifier("responseParser4db")
	IResponseParser responseParser;

	@Autowired
	@Qualifier("EBankConfParser4db")
	IEBankConfParser confParser;
	
	@Autowired
	private IThirdPatryMessageService thirdPatryMessageService;
	private final String LOAN_INFO_QUERY = "0";
	private final String LOAN_INFO_UPDATE = "1";
	private final String LOAN_INFO_ADD = "2";
	
	
	/***
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("credit/custMngLoaner.do")
	@ResponseBody
	public Map<String,Object> custMngLoanerQuery(HttpServletRequest request){
		String transCode = "credit/custMngLoaner";
		IBusinessContext ctx = CtxUtil.createMapContext(request);
		Map<String,Object> rst = new HashMap<String,Object>();
		
		if(!CtxUtil.transPrev(ctx, transCode, json2MapParamCover, 
				requestBuilder, confParser, rst)){
			return rst;
		}
		
		String trans_type = ctx.getParam("trans_type");
		
		if(LOAN_INFO_QUERY.equals(trans_type)){
			transCode = "credit/custMngLoanerQuery";
		}else if(LOAN_INFO_UPDATE.equals(trans_type)){
			transCode = "credit/custMngLoanerUpdate";
		}else if(LOAN_INFO_ADD.equals(trans_type)){
			transCode = "credit/custMngLoanerAdd";
		}else{
			return CtxUtil.transError(ctx, transCode, rst,
					AppConstants.STATUS_FAIL, "未识别的交易类型!");
		}
		
		boolean ok = thirdPatryMessageService.creditLoanSystem(ctx, transCode, rst);
		
		CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
		return rst;
	}
}
