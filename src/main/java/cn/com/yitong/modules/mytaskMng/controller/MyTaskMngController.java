package cn.com.yitong.modules.mytaskMng.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.net.impl.credit.CreditConst;
import cn.com.yitong.framework.net.impl.esb.ESBConst;
import cn.com.yitong.framework.service.IThirdPatryMessageService;
import cn.com.yitong.framework.util.CtxUtil;

@Controller
public class MyTaskMngController extends BaseControl{
	
	@Autowired
	private IThirdPatryMessageService thirdPatryMessageService;
	/***
	 * 查询客户经理名下业务进度
	 * @param request
	 * @return
	 */
	@RequestMapping("credit/myTask.do")
	@ResponseBody
	public Map<String,Object> busiProgQuery(HttpServletRequest request){
		String transCode = "credit/myTask";
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
