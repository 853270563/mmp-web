package cn.com.yitong.modules.mobileCredit;

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
 * 贷后管理
 * @author xushuhuai
 *
 */
@Controller
public class MobileCreditLoanedTaskInfoController extends BaseControl {
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
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("credit/{trans_code}.do")
	@ResponseBody
	public Map<String,Object> loanExpired(@PathVariable String trans_code, HttpServletRequest request){
		String transCode = "credit/" + trans_code;
		IBusinessContext ctx = CtxUtil.createMapContext(request);
		Map<String,Object> rst = new HashMap<String,Object>();
		
		if(!ctx.initParamCover(json2MapParamCover, transCode, false)){
			return rst;
		}
		
		thirdPatryMessageService.creditLoanSystem(ctx, transCode, rst);
		
		if(CreditConst.RS_COMPLETE.equals(rst.get(ESBConst.RS_STATUS))){
			rst.put(AppConstants.MSG,AppConstants.MSG_SUCC);
			rst.put(AppConstants.STATUS, AppConstants.STATUS_OK);
		}
		
		return rst;
	}
}
