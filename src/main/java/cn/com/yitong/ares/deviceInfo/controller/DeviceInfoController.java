package cn.com.yitong.ares.deviceInfo.controller;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.yitong.ares.deviceInfo.service.DeviceInfoService;
import cn.com.yitong.ares.login.service.LoginService;
import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

/**
 * 
 * @author winkie 2015/4/3
 *
 */
@Controller
@SuppressWarnings({"unused", "unchecked"})
public class DeviceInfoController extends BaseControl{
	
	private Logger logger = YTLog.getLogger(this.getClass());
	
	private static final SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	final String BASE_PATH = "ares/deviceInfo/";
	
	private static String DEVICE_UUID = "DEVICE_UUID";
	private static String RESULT = "RESULT";
    
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
    
    @Autowired
    private DeviceInfoService deviceInfoService;
    
    @Autowired
    private LoginService loginService;
    
    /**
     * 设备擦除检查
     * @param request
     * @return
     */
	@RequestMapping("ares/deviceInfo/eraseDeviceCheck.do")
    @ResponseBody
    public Map<String, Object> eraseDeviceCheck(HttpServletRequest request) {
		if(logger.isInfoEnabled()) {
			logger.info("设备擦除检查=================");
		}
		String trans_code = "eraseDeviceCheck";
    	String transCode = BASE_PATH + trans_code;
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}
		if (CtxUtil.debugTrans(trans_code)) {
			boolean ok = client.execute(ctx, transCode);
			transAfter(ctx, transCode, rst, ok);
			return rst;
		}
		// 数据库操作区
		Map params = ctx.getParamMap();
		if(logger.isInfoEnabled()) {
			logger.info("设备擦除参数=================" + params.toString());
		}
		boolean ok = false;
		try {
			// 数据库操作
			rst = deviceInfoService.isNeedEraseAndLock(params);
	        ok = true;
		} catch (Exception e) {
			// 输出错误的关键信息
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
    }
	
	/**
     * 擦除结果反馈
     * @param request
     * @return
     */
	@RequestMapping("ares/deviceInfo/eraseDeviceResultRe.do")
    @ResponseBody
    public Map<String, Object> eraseDeviceResultRe(HttpServletRequest request) {
		if(logger.isInfoEnabled()) {
			logger.info("设备擦除结果反馈=================");
		}
		String trans_code = "eraseDeviceResultRe";
    	String transCode = BASE_PATH + trans_code;
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}
		if (CtxUtil.debugTrans(trans_code)) {
			boolean ok = client.execute(ctx, transCode);
			transAfter(ctx, transCode, rst, ok);
			return rst;
		}
		// 数据库操作区
		Map params = ctx.getParamMap();
		if(logger.isInfoEnabled()) {
			logger.info("设备擦除结果反馈参数=================" + params.toString());
		}
		boolean ok = false;
		try {
			// 数据库操作
			String deviceUuid = null==params.get(DEVICE_UUID)?"":params.get(DEVICE_UUID).toString();
			String result = null==params.get(RESULT)?"":params.get(RESULT).toString();
			if(StringUtil.isEmpty(deviceUuid) || StringUtil.isEmpty(result)) {
				if(logger.isInfoEnabled()) {
					logger.info("设备擦除结果反馈，设备UUID或擦除结果为空");
				}
				rst.put("MSG", "参数为空，请检查");
				return rst;
			}
			Boolean flag = deviceInfoService.eraseDeviceResultReturn(deviceUuid, result);
			if(flag) {
				ok = true;
			}
		} catch (Exception e) {
			// 输出错误的关键信息
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
    }
	
	/**
     * 设备解绑
     * @param request
     * @return
     */
	@RequestMapping("ares/deviceInfo/undoBoundDevice.do")
    @ResponseBody
    public Map<String, Object> undoBoundDevice(HttpServletRequest request) {
		if(logger.isInfoEnabled()) {
			logger.info("设备解绑=================");
		}
		String trans_code = "undoBoundDevice";
    	String transCode = BASE_PATH + trans_code;
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}
		if (CtxUtil.debugTrans(trans_code)) {
			boolean ok = client.execute(ctx, transCode);
			transAfter(ctx, transCode, rst, ok);
			return rst;
		}
		// 数据库操作区
		Map params = ctx.getParamMap();
		if(logger.isInfoEnabled()) {
			logger.info("设备解绑参数=================" + params.toString());
		}
		boolean ok = false;
		try {
			String deviceUuid = null==params.get("DEVICE_UUID")?"":params.get("DEVICE_UUID").toString();
			if(StringUtil.isEmpty(deviceUuid)) {
				if(logger.isInfoEnabled()) {
					logger.info("设备解绑：设备UUID为空");
				}
				rst.put("MSG", "参数为空，请检查");
				return rst;
			}
			// 数据库操作
			Boolean flag = deviceInfoService.undoBoundDevice(deviceUuid);
			if(flag) {
				ok = true;
			}
		} catch (Exception e) {
			// 输出错误的关键信息
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
    }
	
	
	/**
     * 设备注册
     * @param request
     * @return
     */
	@RequestMapping("ares/deviceInfo/boundDevice.do")
    @ResponseBody
    public Map<String, Object> boundDevice(HttpServletRequest request) {
		if(logger.isInfoEnabled()) {
			logger.info("设备注册=================");
		}
		String trans_code = "boundDevice";
    	String transCode = BASE_PATH + trans_code;
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}
		if (CtxUtil.debugTrans(trans_code)) {
			boolean ok = client.execute(ctx, transCode);
			transAfter(ctx, transCode, rst, ok);
			return rst;
		}
		// 数据库操作区
		Map params = ctx.getParamMap();
		if(logger.isInfoEnabled()) {
			logger.info("设备注册参数=================" + params.toString());
		}
		boolean ok = false;
		boolean flag = false;
		try {
			String userId = (String)params.get("CR_STAFF");
			String userPwd = (String)params.get("PASS_WORD");
			if(StringUtil.isEmpty(userId) || StringUtil.isEmpty(userPwd)){
				return CtxUtil.transError(ctx, transCode, rst,
						AppConstants.STATUS_USER_ERROR, "用户名或密码为空！");
			}
			Map<String, Object> userInfo = loginService.loadUserById(userId);// 获取用户信息
            // 如果用户不存在或被注销了，提示用户不存在
			if (userInfo == null || "0".equals(String.valueOf(userInfo
                    .get("USER_STAUS")))) {
				// 用户名不存在
				return CtxUtil.transError(ctx, transCode, rst,
						AppConstants.STATUS_USER_ERROR, "用户不存在！");
			}
			String curPwd = (String) userInfo.get("USER_PSW");// 验证密码
			//加密后验证
			userPwd=new Md5Hash(userPwd).toString().toUpperCase();
			if (!userPwd.equalsIgnoreCase(curPwd)) {
				return CtxUtil.transError(ctx, transCode, rst,
						AppConstants.STATUS_PWD_ERROR, "密码错误, 登录失败!");
			}
			/*if (!userPwd.equalsIgnoreCase(curPwd)) {
				return CtxUtil.transError(ctx, transCode, rst,
						AppConstants.STATUS_PWD_ERROR, "密码错误, 登录失败!");
			}*/
			
			// 数据库操作
			flag = deviceInfoService.boundDevice(params, rst);
			if(flag) {
				ok = true;
			}
		} catch (Exception e) {
			// 输出错误的关键信息
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		String msg = (String)rst.get("MSG");
		transAfter(ctx, transCode, rst, ok);
		if(!flag && StringUtil.isNotEmpty(msg)) {
			rst.put("MSG", msg);
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
		// 交易开始，设置交易流水
		commonService.generyTransLogSeq(ctx, transCode);
		return CtxUtil.transPrev(ctx, transCode, json2MapParamCover, requestBuilder, confParser, rst);
	}

	/**
	 * 事务之后处理
	 * @param ctx
	 * @param transCode
	 * @param rst
	 * @param ok
	 */
	private void transAfter(IBusinessContext ctx, String transCode, Map rst, boolean ok) {
		// 生成交易结果
		CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
		// 保存交易日志
		commonService.saveJsonTransLog(ctx, transCode, rst);
	}

}