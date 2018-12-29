package cn.com.yitong.modules.ares.login.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.yitong.common.utils.ConfigUtils;
import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.consts.NS;
import cn.com.yitong.core.session.Session;
import cn.com.yitong.core.session.model.MhhSession;
import cn.com.yitong.core.session.service.SessionMngService;
import cn.com.yitong.core.session.service.SessionService;
import cn.com.yitong.core.session.timeout.SessionTimeOutMonitorManager;
import cn.com.yitong.core.session.util.SessionConverts;
import cn.com.yitong.core.util.DictUtils;
import cn.com.yitong.core.util.SecurityUtils;
import cn.com.yitong.core.util.ThreadContext;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.core.vo.DeviceCkeck;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.modules.ares.deviceInfo.service.DeviceInfoService;
import cn.com.yitong.modules.ares.dictSyn.model.SysDict;
import cn.com.yitong.modules.ares.login.BizLogger;
import cn.com.yitong.modules.ares.login.service.LoginService;
import cn.com.yitong.modules.ares.login.service.LogoutService;
import cn.com.yitong.tools.vo.SimpleResult;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

@Controller
public class LoginController extends BaseControl {

	private Logger logger = YTLog.getLogger(this.getClass());

	@Autowired
	@Qualifier("requestBuilder4db")
	IRequstBuilder requestBuilder;// 请求报文生成器
	@Autowired
	@Qualifier("responseParser4db")
	IResponseParser responseParser;// 响应报文解析器
	@Autowired
	@Qualifier("EBankConfParser4db")
	IEBankConfParser confParser;// 报文装载器

	@Autowired
	LoginService loginService;
	
	@Autowired
	LogoutService logoutService;

	@Autowired
	private DeviceInfoService deviceInfoService;
	
    @Autowired
    private SessionMngService sessionMngService;

    final BizLogger bizLogger = BizLogger.getLogger(this.getClass());
    
    private static final String ONLYDEVICE = "1";  //单一设备
    private static final String ONLYSESSION = "2";  //单一设备

	@RequestMapping("ares/login/ClientNoLogin.do")
	@ResponseBody
	public Map<String, Object> nologin(HttpServletRequest request) {
		String transCode = "ares/login/ClientNoLogin";
		Map<String, Object> rst = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!CtxUtil.transPrev(ctx, transCode, json2MapParamCover, requestBuilder, confParser, rst)) {
			return rst;
		}
		long unixTime = System.currentTimeMillis();
		rst.put("UNIX_TIME", unixTime);

		String respReuse = NS.SESSION_TOKEN_IS_REUSE_NO;
		Map<String,Object> params = ctx.getParamMap();// 获取总线中参数Map
		String reuse = (String)params.get("reuse");
		if(NS.SESSION_TOKEN_IS_REUSE_YES.equals(reuse)) {
			String preToken = (String)params.get("token");
			if(StringUtil.isNotEmpty(preToken)) {
				MhhSession mhhSession = loginService.getSessionById(preToken);
				if(null != mhhSession) {
					Session preSession = SessionConverts.aresSession2Session(mhhSession);
					if(null != preSession && !preSession.isExpire()) {
						try {
							preSession.getServerId();
							Session currSession = SecurityUtils.getSessionRequired();
							logger.info("会话复用：当前会话ID：" + currSession.getId() + "," + "复用会话ID：" + preSession.getId());
							preSession.setMsgId(currSession.getMsgId());
							preSession.setDeviceCode(currSession.getDeviceCode());
							ThreadContext.bindSession(preSession);
							respReuse = NS.SESSION_TOKEN_IS_REUSE_YES;
						} catch (Exception e) {
							logger.warn("会话复用：复用的会话超时不可用，ID：" + preToken);
						}
					}
				}
			}
		}
		SecurityUtils.getSessionRequired().setAttribute("UNIX_TIME", unixTime);
		rst.put("reused", respReuse);
		rst.put("STATUS", AppConstants.STATUS_OK);
		rst.put("MSG", "无用户会话登录成功！");
		return rst;
	}

	/**
	 * 登录检查：<br>
	 * 1、验证验证码<br>
	 * 2、加载用户信息，验证用户号<br>
	 * 3、验证用户状态是否锁定<br>
	 * 4、验证密码是否正确<br>
	 * 5、更新错误密码登录次数<br>
	 * 6、登录通过，保存会话<br>
	 * 7、记录登录日志<br>
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("ares/login/ClientLogin.do")
	@ResponseBody
	public Map<String, Object> login(HttpServletRequest request) {
		bizLogger.info("用户登录申请", "100100");
		String transCode = "ares/login/ClientLogin";
		Map<String, Object> rst = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!CtxUtil.transPrev(ctx, transCode, json2MapParamCover, requestBuilder, confParser, rst)) {
			bizLogger.warn("用户登录失败，失败原因:" + (String)rst.get("MSG"), "100102");
			return rst;
		}
		// 校验验证码
		if (!checkImageCode(ctx)) {
			return CtxUtil.transError(ctx, transCode, rst, AppConstants.STATUS_FAIL, AppConstants.MSG_RANDOM_ERROR);
		}
		boolean status = false;// 默认的是登录失败
		Map<String,Object> params = ctx.getParamMap();// 获取总线中参数Map
		String userId = params.get("USER_ID").toString();
		Map<String, Object> userInfo = null;
		try {
			userInfo = loginService.loadUserById(userId);// 获取用户信息
            // 如果用户不存在或被注销了，提示用户不存在
			if (userInfo == null || "0".equals(String.valueOf(userInfo.get("USER_STAUS")))) {
				// 用户名不存在
				bizLogger.warn("用户登录失败，失败原因:用户不存在！", "100102");
				return CtxUtil.transError(ctx, transCode, rst,AppConstants.STATUS_FAIL, "用户不存在！");
			}
			// 校验密码输入错误次数
			int errLgnCnt = StringUtil.getInt(userInfo, "ERR_LGN_CNT", 0);
			if (errLgnCnt >= 6) {// 超过6次将会被锁定, 需要人工解锁
				bizLogger.warn("用户登录失败，失败原因:密码错误超过5次已被锁定, 请联系系统管理员解锁!", "100111");
				rst.put("result", SessionService.LOGIN_STATUS_EVENT_USER_LOCK);
				return CtxUtil.transError(ctx, transCode, rst, AppConstants.STATUS_FAIL, "密码错误超过5次已被锁定, 请联系系统管理员解锁!");
			}
			String curPwd = (String) userInfo.get("USER_PSW");// 验证密码
			String userPwd = (String) ctx.getParam("USER_PSW");
			Map<String, Object> tmp = new HashMap<String, Object>();
			tmp.put("USER_ID", userId);
			if (!userPwd.equalsIgnoreCase(curPwd)) {
				tmp.put(AppConstants.STATUS, AppConstants.STATUS_FAIL);// 密码错误
				loginService.updateErrorLoginCnt(tmp);
				bizLogger.warn("用户登录失败，密码错误，您还可以输入" + (5 - errLgnCnt) + "次", "100112");

				if (errLgnCnt == 5) {
					rst.put("result", SessionService.LOGIN_STATUS_EVENT_USER_LOCK);
					return CtxUtil.transError(ctx, transCode, rst, AppConstants.STATUS_FAIL, "密码错误超过5次已被锁定, 请联系系统管理员解锁!");
				} else {
					rst.put("result", SessionService.LOGIN_STATUS_EVENT_RROR_PWD);
					return CtxUtil.transError(ctx, transCode, rst, AppConstants.STATUS_FAIL, "用户登录失败，密码错误，您还可以输入" + (5 - errLgnCnt) + "次");
				}
			}
			
            // 客户端类型的用户才能登陆客户端
            if (NS.DICT_USER_TYP_WEB.equals(StringUtil.getString(userInfo, "USER_TYP", ""))) {
				bizLogger.warn("用户登录失败，失败原因:您是后管平台用户，无权限登录客户端！", "100113");
				rst.put("result", SessionService.LOGIN_STATUS_EVENT_ERROR_TYPE);
                return CtxUtil.transError(ctx, transCode, rst, AppConstants.STATUS_FAIL, "您是后管平台用户，无权限登录客户端！");
            }
            
            // 增加设备注册验证--擦除 锁定 设置注册 用户绑定最大设备数等
            String deviceUuid = SecurityUtils.getSessionRequired().getDeviceCode();
            if(StringUtil.isEmpty(deviceUuid)) {
            	deviceUuid = (String) ctx.getParam("CLI_DEVICE_NO");
            }
            //人工授权
//            if (needMAuth(deviceUuid,ctx.getParamMap())){
//				return CtxUtil.transError(ctx, transCode, rst,AppConstants.STATUS_FAIL,"请联系系统管理员授权该设备![Powered By YiTong]");
//			}
            DeviceCkeck checkResult = deviceInfoService.loginDeviceCheck(deviceUuid, userId);
            rst.put("ERASE_SIGN", checkResult.getEraseSign());//checkResult.getEraseSign()--争议点，新设备首次登陆是否擦除,暂定不擦除
            rst.put("LOCK_SIGN", checkResult.getLockSign());
            rst.put("RESULT", checkResult.getResult());
            if(!checkResult.getIsSuccess()) {
				bizLogger.warn("用户登录失败，失败原因:" + checkResult.getMessage(), "100102");
				SessionTimeOutMonitorManager.doHandler(
						new SimpleResult(false, checkResult.getMessage(), checkResult.getResult()),
						SecurityUtils.getSessionRequired().getId());
            	return CtxUtil.transError(ctx, transCode, rst, "005", checkResult.getMessage());
            }

            String forceLogin = (String) ctx.getParam("FORCE_LOGIN_FLAG");
			String loginAccountLimtType = "0";
			List<SysDict> dicts = DictUtils.getDictionaries(NS.LOGINACCOUNTLIMTTYPE);
			if(null != dicts && dicts.size() > 0) {
				if(StringUtil.isNotEmpty(dicts.get(0).getValue())) {
					loginAccountLimtType = dicts.get(0).getValue();
				}
			}
			//1表示强制登录，注销当前在线用户会话
            if(StringUtil.isNotEmpty(forceLogin) && "1".equals(forceLogin)){
				// 设置单一设备时 userId session 被踢标识
				if(ONLYDEVICE.equals(loginAccountLimtType)) {
					sessionMngService.updateSessionKickedUser(userId);
				}else {//不是单一设备，注销会话时不标识被踢
					sessionMngService.updateSessionByUserId(userId);
				}
            }else{
                //并发控制[无控制]
                int sessionCnt = 0;
                logger.debug("登录控制类型【0：不控制；1：单一设备；2：单一会话】: "+  loginAccountLimtType);
                if(ONLYDEVICE.equals(loginAccountLimtType)){ //单一设备
                    sessionCnt = sessionMngService.getSessionCntByUserAndDeviceId(userId, deviceUuid);
                    logger.debug("根据用户和设备号查询当前所有会话数:" + sessionCnt);
                    if(sessionCnt == 0){ //注销历史会话
                        sessionMngService.updateSessionByDeviceId(userId, deviceUuid);
                    }
                }else if(ONLYSESSION.equals(loginAccountLimtType)){ //单一会话
                    sessionCnt = sessionMngService.getSessionCntByUser(userId);
                    logger.debug("根据用户查询当前所有会话数:"+sessionCnt);
                }
                if(sessionCnt == 0){//并发无控制
                    final int loginLimtCnt = ConfigUtils.getValue(NS.LOGINLIMTCNT, -1); //获取总体登陆限制数
                    logger.debug("总体登录限制数:"+loginLimtCnt);
                    if(loginLimtCnt > -1){
                        if(loginLimtCnt > 0){//正数
                            logger.debug("当前总会话数:"+sessionMngService.getOnlineSessionCnt());
                            if(sessionMngService.getOnlineSessionCnt() < loginLimtCnt){
                                rst.put("CONCURRENCY_RESULT", "成功");
                            }else{
                                ctx.setParam("FORCE_LOGIN_STATUS","0");//0表示当前会话已经达到上限，如果要强制登录客户端需传送状态1
                                return CtxUtil.transError(ctx, transCode, rst, AppConstants.STATUS_FAIL, "会话数已达到上限,是否强制登录");
                            }
                        } else if(0 == loginLimtCnt){
                            return CtxUtil.transError(ctx, transCode, rst, AppConstants.STATUS_FAIL, "登录限制数为0");
                        }else{
                            rst.put("CONCURRENCY_RESULT", "成功");
                        }
                    }
                } else {//会话数已达到上限
					String msg = "会话数已达到上限，是否强制登录";
					if(ONLYDEVICE.equals(loginAccountLimtType)) {
						msg = "在其它设备上已登录，是否强制登录";
						rst.put("RESULT", SessionService.SESSION_STATUS_EVENT_LOGIN_OTHER);
					}
					String forceLoginStatus = "0";
					List<SysDict> forceLogDicts = DictUtils.getDictionaries(NS.FORCEKOGINTYPE);
					if(null != forceLogDicts && forceLogDicts.size() > 0) {
						String value = forceLogDicts.get(0).getValue();
						if(StringUtil.isNotEmpty(value) && "1".equals(value)) {
							forceLoginStatus = "1";
						}
					}
					//0表示当前会话已经达到上限，如果要强制登录客户端需传送状态1
                    ctx.setParam("FORCE_LOGIN_STATUS", forceLoginStatus);
					rst.put("FORCE_LOGIN_STATUS", forceLoginStatus);
                    return CtxUtil.transError(ctx, transCode, rst, AppConstants.STATUS_FAIL, msg);
                }
            }

            tmp.put(AppConstants.STATUS, AppConstants.STATUS_OK);// 密码正确
			status = true;// 登录成功

            // 所属分行机构ID
            userInfo.put("EXT_ORG_ID", loginService.queryExtOrgId4User(userInfo));

			// 更新会话认证状态
			SecurityUtils.getSessionRequired().setUserId(userId);
			SecurityUtils.getSessionRequired().setAuthStatus("1");

			// lc3:应安卓客户端要求，对为空的字段传空串
			for(Map.Entry<String, Object> entry : userInfo.entrySet()) {
				if(null == entry.getValue()) {
					entry.setValue("");
				}
			}
			rst.putAll(userInfo);

			// lc3:获取角色列表，用逗号分隔
			List<String> roleIds = loginService.queryRoleIdsByUserId((String)userInfo.get("ID"));
			rst.put("ROLE_IDS", null != roleIds ? StringUtils.collectionToCommaDelimitedString(roleIds) : "");

			try {
				loginService.updateErrorLoginCnt(tmp);	// 更新密码错误次数
				loginService.updateLastLgnTime(userInfo);//更新最近登录时间
				//登陆成功后把同一设备号其它有效SESSION置为无效
				loginService.setSessionInvalidByDeviceId(deviceUuid);
			} catch (Exception e) {
				logger.warn("更新密码错误次数和最近登录时间失败。", e);
			}

			// 保存会话
			saveSession(ctx, userInfo);
			bizLogger.info("用户登录成功,登录方式:[密码]", "100101");
			rst.put("LOGINTOKEN", loginService.setLoginTokenToUser(deviceUuid, userId));
			rst.put("APPKEY", params.get("APP_CODE"));
		}catch (Exception e) {
			// 输出错误的关键信息
			logger.error(ctx.getTransLogBean(transCode), e);
			bizLogger.warn("用户登录失败，失败原因:" + e.getMessage(), "100102");
		}finally {
			if(logger.isDebugEnabled()){
				logger.debug("start save session log...");
			}
		}
		CtxUtil.transAfter(ctx, transCode, rst, status, responseParser, confParser);
		return rst;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("ares/login/ClientLogout.do")
	@ResponseBody
	public Map logout(HttpServletRequest request) {
		String transCode = "ares/login/ClientLogout";
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!CtxUtil.transPrev(ctx, transCode, json2MapParamCover,requestBuilder, confParser, rst)) {
			return rst;
		}
		// 获取当前用户session
		HttpSession session = request.getSession();// 获取当前用户session
		try {
			CtxUtil.transAfter(ctx, transCode, rst, true, responseParser, confParser);
			Map sessionMap = new HashMap();
			sessionMap.put("SESS_LOG_ID", session.getAttribute("SESS_LOG_ID"));
			logoutService.updateLogoutDTime(sessionMap);
			bizLogger.info("用户退出登录", "100201");
		} catch (Exception e) {
			logger.error(ctx.getTransLogBean(transCode), e);
			CtxUtil.transError(ctx, transCode, rst);
		} finally {
			logger.info("sessionId== " + session.getId() + " invalidate ...");			
			Session sessionRequired = SecurityUtils.getSessionRequired();
			sessionRequired.setUserId(null);
			sessionRequired.setAuthStatus(null);
		}
		return rst;
	}

	/**
	 * 检查验证码
	 */
	private boolean checkImageCode(IBusinessContext ctx) {
		Object sessionImgCode = SecurityUtils.getSessionRequired().getAttribute(NS.IMG_CODE);
		String session_img_code = "";
		if(null != sessionImgCode) {
			session_img_code = sessionImgCode.toString();
		}
		String imgCode = ctx.getParam("IMG_CODE");
		if(StringUtil.isEmpty(imgCode)) {
			return true;
		}
		if (StringUtil.isNotEmpty(imgCode) && imgCode.equals(session_img_code)) {
			return true;
		}
		return false;
	}

	/**
	 * 用户信息存放到Session中
	 */
	private void saveSession(IBusinessContext ctx, Map user) {
		HttpSession session = ctx.getHttpSession();// 获取缓存中session对象
		ctx.saveSessionText(NS.ISLOGIN, AppConstants.TRUE);
		String user_id = (String) ctx.getParam("USER_ID");
		session.setAttribute(NS.LOGIN_ID, user_id);
		session.setAttribute(NS.ORGAN_ID, user.get("CODE"));
		session.setAttribute(NS.USER_NO, user.get("USER_NO"));
		logger.info("save user ： " + user_id + "  info into sessionId : " + session.getId() + " successful");
	}

	/**
	 * 人工审核是否有效【非业务功能】
	 */
	private boolean needMAuth(String deviceUuid,Map params) {
		if(Boolean.valueOf(ConfigUtils.getValue("M_AUTH_ENABLED",false))){
			String authType = deviceInfoService.selectDeviceAuth(deviceUuid,(String) params.get("APP_ID"));
			try {
				//设备设备权限登入
				deviceInfoService.insertDeviceAuth(deviceUuid,params,"-1".equals(authType));
			}catch (Exception e){
				logger.warn("设备权限登入[插入/更新]异常：",e);
			}
			if (!"1".equals(authType)){//设备未授权或者首次登入
				return true;
			}
		}
		return false;
	}
}
