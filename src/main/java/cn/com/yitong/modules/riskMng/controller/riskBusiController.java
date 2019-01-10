package cn.com.yitong.modules.riskMng.controller;

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
 * 风险管理
 * @author xushuhuai
 *
 */
@Controller
public class riskBusiController extends BaseControl {
private Logger logger = YTLog.getLogger(this.getClass());
	
	@Autowired
	private IThirdPatryMessageService thirdPatryMessageService;
	
	/***
	 * 预警事项查询
	 * @param request
	 * @return
	 */
	@RequestMapping("risk/riskInfoQuery.do")
	@ResponseBody
	public Map<String,Object> riskInfoQuery(HttpServletRequest request){
		String transCode = "risk/riskInfoQuery";
		IBusinessContext ctx = CtxUtil.createMapContext(request);
		Map<String,Object> rst = new HashMap<String,Object>();
		
		if(!ctx.initParamCover(json2MapParamCover, transCode, false)){
			return rst;
		}
		
		thirdPatryMessageService.esbUnionSystem(ctx, transCode, rst);
		
		if(CreditConst.RS_COMPLETE.equals(rst.get(ESBConst.RS_STATUS))){
			rst.put("MSG",AppConstants.MSG_SUCC);
			rst.put("STATUS", AppConstants.STATUS_OK);
		}
		
		return rst;
	}
	/***
	 * 预警事项查询
	 * @param request
	 * @return
	 */
	@RequestMapping("risk/riskBusiQuery.do")
	@ResponseBody
	public Map<String,Object> riskBusiQuery(HttpServletRequest request){
		String transCode = "risk/riskBusiQuery";
		IBusinessContext ctx = CtxUtil.createMapContext(request);
		Map<String,Object> rst = new HashMap<String,Object>();
		
		if(!ctx.initParamCover(json2MapParamCover, transCode, false)){
			return rst;
		}
		
		thirdPatryMessageService.esbUnionSystem(ctx, transCode, rst);
		
		if(CreditConst.RS_COMPLETE.equals(rst.get(ESBConst.RS_STATUS))){
			rst.put("MSG",AppConstants.MSG_SUCC);
			rst.put("STATUS", AppConstants.STATUS_OK);
		}
		
		return rst;
	}
	
	/***
	 * 客户预警查询
	 * @param request
	 * @return
	 */
	@RequestMapping("risk/riskCustQuery.do")
	@ResponseBody
	public Map<String,Object> riskCustQuery(HttpServletRequest request){
		String transCode = "risk/riskCustQuery";
		IBusinessContext ctx = CtxUtil.createMapContext(request);
		Map<String,Object> rst = new HashMap<String,Object>();
		
		if(!ctx.initParamCover(json2MapParamCover, transCode, false)){
			return rst;
		}
		
		boolean ok = thirdPatryMessageService.esbUnionSystem(ctx, transCode, rst);
		
		if(!CreditConst.RS_COMPLETE.equals(rst.get(ESBConst.RS_STATUS))){
			rst.put(AppConstants.MSG,rst.get("desc"));
			rst.put(AppConstants.STATUS, AppConstants.STATUS_FAIL);
			return rst;
		}
		rst.put(AppConstants.MSG,AppConstants.MSG_SUCC);
		rst.put(AppConstants.STATUS, AppConstants.STATUS_OK);
		return rst;
	}
}
