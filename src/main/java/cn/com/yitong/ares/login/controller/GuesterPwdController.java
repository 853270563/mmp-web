package cn.com.yitong.ares.login.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.yitong.ares.login.service.LoginService;
import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.core.util.DictUtils;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

@Controller
public class GuesterPwdController extends BaseControl {
	private Logger logger = YTLog.getLogger(this.getClass());
	
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
	ICrudService service;
	@Autowired
	LoginService loginService;
	private final String BATH_PATH = "ares/gstPwd/";
	
	/***
	 * 查询手势密码是否存在
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("ares/gstPwd/queryGstInfo")
	@ResponseBody
	public Map<String,Object> queryGstInfo(HttpServletRequest request){
		String transCode = BATH_PATH + "queryGstInfo";
		
		IBusinessContext ctx = CtxUtil.createMapContext(request);
		
		Map<String,Object> rst = new HashMap<String,Object>();
		
		if(!CtxUtil.transPrev(ctx, transCode, json2MapParamCover, 
				requestBuilder, confParser, rst)){
			return rst;
		}
		
		Map<String,Object> paramsMap = ctx.getParamMap();
		
		Map gstInfo = service.load("SYS_USER.queryGstInfo", paramsMap);
			rst.putAll(gstInfo);
		if(gstInfo==null || gstInfo.isEmpty() || gstInfo.get("GUESTER_FLAG")==null ){
			rst.put("GUESTER_FLAG", "0");//不存在
		}else{
			rst.put("GUESTER_FLAG", "1");
		}
		
		CtxUtil.transAfter(ctx, transCode, rst, true, responseParser, confParser);
		
		return rst;
	}
	/***
	 * 设置手势密码
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("ares/gstPwd/updateGstInfo")
	@ResponseBody
	public Map<String,Object> updateGstInfo(HttpServletRequest request){
		String transCode = BATH_PATH + "updateGstInfo";
		
		IBusinessContext ctx = CtxUtil.createMapContext(request);
		
		Map<String,Object> rst = new HashMap<String,Object>();
		
		if(!CtxUtil.transPrev(ctx, transCode, json2MapParamCover, 
				requestBuilder, confParser, rst)){
			return rst;
		}
		
		Map<String,Object> paramsMap = ctx.getParamMap();
		paramsMap.put("GUESTER_FLAG", "1");
		
		boolean ok = service.update("SYS_USER.updateGstInfo", paramsMap);
		
		CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
		
		return rst;
	}
	
	/***
	 * 校验手势密码
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("ares/gstPwd/checkGstInfo")
	@ResponseBody
	public Map<String,Object> checkGstInfo(HttpServletRequest request){
		String transCode = BATH_PATH + "checkGstInfo";
		
		IBusinessContext ctx = CtxUtil.createMapContext(request);
		
		Map<String,Object> rst = new HashMap<String,Object>();
		
		if(!CtxUtil.transPrev(ctx, transCode, json2MapParamCover, 
				requestBuilder, confParser, rst)){
			return rst;
		}
		
		Map<String,Object> paramsMap = ctx.getParamMap();
		
		//查询手势密码
		Map<String,Object> gstInfo = service.load("SYS_USER.queryGstInfo", paramsMap);
		
		if(gstInfo==null || gstInfo.isEmpty() || gstInfo.get("GUESTER_PWD")==null || StringUtil.isEmpty((String)gstInfo.get("GUESTER_PWD"))){
			return CtxUtil.transError(ctx, transCode, rst,
					AppConstants.STATUS_FAIL, "您尚未设置手势密码！");
		}
		Map<String, Object> userInfo = loginService.loadUserById(paramsMap.get("USER_ID").toString());// 获取用户信息
		int errLgnCnt = StringUtil.getInt(userInfo, "ERR_LGN_CNT", 0);
		int errTimes = Integer.parseInt(DictUtils.getDictValue("SYS_PARAM", "ERR_LGN_CNT", "5"));
		if (errLgnCnt >= errTimes) {// 
			return CtxUtil.transError(ctx, transCode, rst, AppConstants.STATUS_USER_LOCKED_ERROR, "错误次数超限，请联系系统管理员！");
		}
		//手势密码参数
		String paramGst = (String) paramsMap.get("GUESTER_PWD");
		
		//校验手势密码
		if(!paramGst.equals(gstInfo.get("GUESTER_PWD"))){
			errLgnCnt++;
			int hasLgnCnt = errTimes - errLgnCnt;
			paramsMap.put(AppConstants.STATUS, AppConstants.STATUS_PWD_ERROR);// 密码错误
			paramsMap.put("ERR_LGN_CNT", errLgnCnt);
			loginService.updateErrorLoginCnt(paramsMap);
			return CtxUtil.transError(ctx, transCode, rst, AppConstants.STATUS_PWD_ERROR, "登录失败!手势错误" + errLgnCnt + "次，剩余" + hasLgnCnt + "次");
		}
		
		CtxUtil.transAfter(ctx, transCode, rst, true, responseParser, confParser);
		
		return rst;
	}
}
