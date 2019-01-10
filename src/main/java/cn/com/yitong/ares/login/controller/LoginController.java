package cn.com.yitong.ares.login.controller;

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

import cn.com.yitong.ares.deviceInfo.service.DeviceInfoService;
import cn.com.yitong.ares.login.BizLogger;
import cn.com.yitong.ares.login.service.LoginService;
import cn.com.yitong.ares.login.service.LogoutService;
import cn.com.yitong.common.utils.ConfigUtils;
import cn.com.yitong.common.utils.JsonUtils;
import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.consts.NS;
import cn.com.yitong.consts.SessConsts;
import cn.com.yitong.core.model.SysDict;
import cn.com.yitong.core.session.Session;
import cn.com.yitong.core.session.timeout.SessionTimeOutMonitorManager;
import cn.com.yitong.core.util.DictUtils;
import cn.com.yitong.core.util.SecurityUtils;
import cn.com.yitong.core.web.filter.ReusableHttpServletRequest;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.core.vo.DeviceCkeck;
import cn.com.yitong.framework.dao.IbatisDao;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.service.ILoginSessionService;
import cn.com.yitong.framework.service.impl.SessionMngService;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.framework.util.security.MD5Encrypt;
import cn.com.yitong.modules.session.service.SessionService;
import cn.com.yitong.tools.vo.SimpleResult;
import cn.com.yitong.util.ConfigEnum;
import cn.com.yitong.util.DateUtil;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

/**
 * 客户端登录相关
 * 
 * @author yaoym
 * 
 */
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
	@Qualifier("loginSessionService")
	ILoginSessionService loginSessionService;

	@Autowired
	LoginService loginService;
	
	@Autowired
	LogoutService logoutService;
	
	@Autowired
	@Qualifier("ibatisDao")
	private IbatisDao dao;

	@Autowired
	private DeviceInfoService deviceInfoService;
	
    @Autowired
    private SessionMngService sessionMngService;

    final BizLogger bizLogger = BizLogger.getLogger(this.getClass());
    
    private static final String ONLYDEVICE = "1";  //单一设备
	private static final String ONLYSESSION = "2"; //单一会话
    private static final String DEVICE_CHECK_YES = "1";  // 1:检查  0：不检查
    private static final String LOGIN_BY_PWD = "0";  // 密码登录
    private static final String LOGIN_BY_FINGERPRINT = "1";  // 指纹登录
	private static final String LOGIN_BY_GUESTER = "2"; // 手势登录

	@RequestMapping("ares/login/ClientNoLogin.do")
	@ResponseBody
	public Map<String, Object> nologin(HttpServletRequest request) {
		String transCode = "ares/login/ClientNoLogin";
		Map<String, Object> rst = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!CtxUtil.transPrev(ctx, transCode, json2MapParamCover,
				requestBuilder, confParser, rst)) {
			return rst;
		}
		long unixTime = System.currentTimeMillis();
		rst.put("UNIX_TIME", unixTime);
		SecurityUtils.getSession().setAttribute("UNIX_TIME", unixTime);
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
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("ares/login/ClientLogin.do")
	@ResponseBody
	public Map<String, Object> login(HttpServletRequest request) {
		bizLogger.info("用户登录申请", "100100");
		String transCode = "ares/login/ClientLogin";
		Map<String, Object> rst = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!CtxUtil.transPrev(ctx, transCode, json2MapParamCover,
				requestBuilder, confParser, rst)) {
			bizLogger.warn("用户登录失败，失败原因:" + (String)rst.get("MSG"), "100102");
			return rst;
		}
		// 校验验证码
		 if (!checkImageCode(ctx)) {// 验证码不正确
			 return CtxUtil.transError(ctx,
		 transCode, rst, AppConstants.STATUS_RANDOM_ERROR,
		 AppConstants.MSG_RANDOM_ERROR); }
		boolean status = false;// 默认的是登录失败
		Map<String,Object> params = ctx.getParamMap();// 获取总线中参数Map
		String userId = params.get("USER_ID").toString().trim();
		String saveUserId = params.get("USER_ID").toString().trim();
		Map<String, Object> userInfo=null;
		try {
			userInfo = loginService.loadUserById(saveUserId);// 获取用户信息
            // 如果用户不存在或被注销了，提示用户不存在
			if (userInfo == null) {
				// 用户名不存在
				bizLogger.warn("用户登录失败，失败原因:用户不存在！", "100102");
				return CtxUtil.transError(ctx, transCode, rst,
						AppConstants.STATUS_USER_ERROR, "用户名或者密码错误！");
			}
			// 校验密码输入错误次数
			int errLgnCnt = StringUtil.getInt(userInfo, "ERR_LGN_CNT", 0);
			int errTimes = Integer.parseInt(DictUtils.getDictValue("SYS_PARAM", "ERR_LGN_CNT", "5"));
			if (errLgnCnt >= errTimes) {// 
				bizLogger.warn("用户登录失败，失败原因:错误次数超限，请联系系统管理员！", "100102");
				return CtxUtil.transError(ctx, transCode, rst,
						AppConstants.STATUS_USER_LOCKED_ERROR,
						"错误次数超限，请联系系统管理员！");
			}
			
			//指纹登录
			String loginType = ctx.getParam("LOGIN_TYPE");
			Map<String, Object> tmp = new HashMap<String, Object>();
			if (LOGIN_BY_FINGERPRINT.equals(loginType)) {
				logger.info("登录方式：指纹登录");

			} else if (LOGIN_BY_GUESTER.equals(loginType)) {
				logger.info("登录方式：手势登录");
				String curPwd = (String) userInfo.get("GUESTER_PWD");// 手势密码
				String userPwd = ctx.getParam("GUESTER_PWD");
				if (!StringUtil.isEmpty(userPwd)) {
					tmp.put("USER_ID", userId);
					if (!userPwd.equalsIgnoreCase(curPwd)) {
						errLgnCnt++;
						int hasLgnCnt = errTimes - errLgnCnt;
						tmp.put(AppConstants.STATUS, AppConstants.STATUS_PWD_ERROR);// 密码错误
						tmp.put("ERR_LGN_CNT", errLgnCnt);
						loginService.updateErrorLoginCnt(tmp);
						bizLogger.warn("用户登录失败，失败原因:手势错误, 登录失败!", "100102");
						return CtxUtil.transError(ctx, transCode, rst, AppConstants.STATUS_PWD_ERROR, "登录失败!手势错误" + errLgnCnt + "次，剩余" + hasLgnCnt + "次");
					}
				}

			}else{
				String curPwd = (String) userInfo.get("USER_PSW");// 验证密码
				String userPwd = ctx.getParam("USER_PSW");
				tmp.put("USER_ID", userId);
				if (!userPwd.equalsIgnoreCase(curPwd)) {
					errLgnCnt++;
					int hasLgnCnt = errTimes - errLgnCnt;
					tmp.put(AppConstants.STATUS, AppConstants.STATUS_PWD_ERROR);// 密码错误
					tmp.put("ERR_LGN_CNT", errLgnCnt);
					loginService.updateErrorLoginCnt(tmp);
					bizLogger.warn("用户登录失败，失败原因:密码错误, 登录失败!", "100102");
					if(hasLgnCnt == 0){
						return CtxUtil.transError(ctx, transCode, rst, AppConstants.STATUS_PWD_ERROR, "登录失败!密码错误" + errLgnCnt + "次，账号已锁定，请联系管理员解锁！");
					}
					return CtxUtil.transError(ctx, transCode, rst, AppConstants.STATUS_PWD_ERROR, "登录失败!密码错误" + errLgnCnt + "次，剩余" + hasLgnCnt + "次");
				} else if (userPwd.equalsIgnoreCase(MD5Encrypt.MD5(DictUtils.getDictValue("SYS_PARAM", "RESET_PASS_WORD", "888888")))) {
					return CtxUtil.transError(ctx, transCode, rst, AppConstants.STATUS_USER_INIT_PWD, "此密码为初始密码，请修改密码");
				}
			}
			
            // 如果用户已经登陆，不能重新登陆
//            if (SessionListener.isOnline4UserId(userId)) {
//                return CtxUtil.transError(ctx, transCode, rst,
//                                          AppConstants.STATUS_PWD_ERROR,
//                                          "当前用户已在线，请正常退出后再登录！");
//            }
            // 客户端类型的用户才能登陆客户端
            if (ConfigEnum.DICT_USER_TYP_WEB.strVal().equals(StringUtil.getString(userInfo, "USER_TYPE", ""))) {
				bizLogger.warn("用户登录失败，失败原因:您是后管平台用户，无权限登陆客户端！", "100102");
                return CtxUtil.transError(ctx, transCode, rst,
                                          AppConstants.STATUS_PWD_ERROR,
                                          "您是后管平台用户，无权限登陆客户端！");
            }
            // 增加设备注册验证--擦除 锁定 设置注册 用户绑定最大设备数等
            String deviceUuid = SecurityUtils.getSessionRequired().getDeviceCode();
            if(StringUtil.isEmpty(deviceUuid)) {
            	deviceUuid = ctx.getParam("CLI_DEVICE_NO");
            }
            
            if(DEVICE_CHECK_YES.equals(ConfigUtils.getValue("DEVICE_CHECK"))){
            	DeviceCkeck checkResult = deviceInfoService.loginDeviceCheck(deviceUuid, userId);
            	rst.put("ERASE_SIGN", checkResult.getEraseSign());//checkResult.getEraseSign()--争议点，新设备首次登陆是否擦除,暂定不擦除
            	rst.put("LOCK_SIGN", checkResult.getLockSign());
            	rst.put("RESULT", checkResult.getResult());
            	if(!checkResult.getIsSuccess()) {
            		bizLogger.warn("用户登录失败，失败原因:" + checkResult.getMessage(), "100102");
            		SessionTimeOutMonitorManager.doHandler(new SimpleResult(false, checkResult.getMessage(), checkResult.getResult()),
            				SecurityUtils.getSessionRequired().getId());
            		return CtxUtil.transError(ctx, transCode, rst, "005", checkResult.getMessage());
            	}
            }
            

            String forceLogin=ctx.getParam("FORCE_LOGIN_FLAG");//是否强制登录标识
			String loginAccountLimtType = "0";
			List<SysDict> dicts = DictUtils.getDictionaries(NS.LOGINACCOUNTLIMTTYPE);
			if(null != dicts && dicts.size() > 0) {
				if(StringUtil.isNotEmpty(dicts.get(0).getValue())) {
					loginAccountLimtType = dicts.get(0).getValue();
				}
			}

            if(StringUtil.isNotEmpty(forceLogin) && "1".equals(forceLogin)){//1表示强制登录，注销当前在线用户会话
				// 设置单一设备时 userId session 被踢标识
				if(ONLYDEVICE.equals(loginAccountLimtType)) {
					logger.info("用户： " + saveUserId + "已被踢掉");
					sessionMngService.updateSessionKickedUser(saveUserId);
				}else {
					//不是单一设备，注销会话时不标识被踢
					sessionMngService.updateSessionByUserId(saveUserId);
				}
            }else{
                //并发控制
                int sessionCnt=0;
                logger.debug("登录控制类型【0：不控制；1：单一设备；2：单一会话】: "+  loginAccountLimtType);
                if(ONLYDEVICE.equals(loginAccountLimtType)){ //单一设备
                    sessionCnt = sessionMngService.getSessionCntByUserAndDeviceId(saveUserId, deviceUuid);
                    logger.debug("根据用户和设备号查询当前所有会话数:" + sessionCnt);
                    if(sessionCnt == 0){ //注销历史会话
                        sessionMngService.updateSessionByDeviceId(saveUserId, deviceUuid);
                    }
                }else if(ONLYSESSION.equals(loginAccountLimtType)){ //单一会话
                    sessionCnt = sessionMngService.getSessionCntByUser(saveUserId);
                    logger.debug("根据用户查询当前所有会话数:"+sessionCnt);
                }else {//无控制
                    sessionCnt=0;
                }

                if(sessionCnt == 0){
                    final int loginLimtCnt = ConfigUtils.getValue(NS.LOGINLIMTCNT, -1); //获取总体登陆限制数
                    logger.debug("总体登录限制数:"+loginLimtCnt);
                    if(loginLimtCnt > -1){
                        if(loginLimtCnt > 0){//正数
                            logger.debug("当前总会话数:"+sessionMngService.getOnlineSessionCnt());
                            if(sessionMngService.getOnlineSessionCnt() < loginLimtCnt){
                                rst.put("CONCURRENCY_RESULT", "成功");
                            }else{
								rst.put("RESULT", "009");
                                return CtxUtil.transError(ctx, transCode, rst,
										AppConstants.STATUS_OK, "已在其他设备登陆");
                            }
                        } else if(0 == loginLimtCnt){
                            return CtxUtil.transError(ctx, transCode, rst,
                                    AppConstants.STATUS_FAIL, "登陆限制数为0");
                        }else{
                            rst.put("CONCURRENCY_RESULT", "成功");
                        }
                    }
                } else {//会话数已达到上限
					String msg = "";
					if(ONLYDEVICE.equals(loginAccountLimtType)) {
						msg = "在其它设备上已登录，是否强制登录";
						rst.put("RESULT", SessionService.SESSION_STATUS_EVENT_LOGIN_OTHER);
					}else{
						msg = "会话数已达到上限，是否强制登录";
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
                    return CtxUtil.transError(ctx, transCode, rst,
							AppConstants.STATUS_OK, msg);
                }
            }

            tmp.put(AppConstants.STATUS, AppConstants.STATUS_OK);// 密码正确
			status = true;// 登录成功
            // 所属分行机构ID
            userInfo.put("EXT_ORG_ID", loginService.queryExtOrgId4User(userInfo));
			// 更新会话认证状态
			SecurityUtils.getSessionRequired().setUserId(saveUserId);
			SecurityUtils.getSessionRequired().setAuthStatus("1");
			SecurityUtils.getSessionRequired().setAttribute("orgId", loginService.queryExtOrgId4User(userInfo));
			try {
				loginService.updateErrorLoginCnt(tmp);	// 更新密码错误次数
				loginService.updateLastLgnTime(userInfo);//更新最近登录时间和首次登陆标识
				//登陆成功后把同一设备号其它有效SESSION置为无效
				loginService.setSessionInvalidByDeviceId(deviceUuid);
			} catch (Exception e) {
				logger.warn("更新密码错误次数和最近登录时间失败。", e);
			}
			
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
			
			// 单点登陆
            /*SessionListener.logoutOnline4UserId(userId,request.getSession());*/
            
			// 保存会话
			saveSession(ctx, userInfo);
			// 
			bizLogger.info("用户登录成功,登录方式:[" + (LOGIN_BY_FINGERPRINT.equals(loginType) ? "指纹" : "密码") + "]", "100101");
			String loginToken = loginService.setLoginTokenToUser(deviceUuid, userId);
			rst.put("LOGINTOKEN", loginToken);
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
		CtxUtil.transAfter(ctx, transCode, rst, status, responseParser,
				confParser);
		return rst;
	}

	/**
	 * 检查验证码
	 * 
	 * @param ctx
	 * @return
	 */
	private boolean checkImageCode(IBusinessContext ctx) {
		String IMG_CODE = ctx.getParam("IMG_CODE");
		if(StringUtil.isEmpty(IMG_CODE)){
			return true;
		}
		Session session = SecurityUtils.getSessionRequired();
		Map sqlMap = new HashMap();
		sqlMap.put("DEVICE_ID", session.getDeviceCode());
		List<Map> rstList = dao.findList("ARES_SESSION.loadByDeviceId", sqlMap);
		
		if(rstList==null || rstList.isEmpty()){
			return false;
		}
		Map rst = rstList.get(0);
			String DATA = (String) rst.get("DATA");
			Map<Object, Object> jsonMap = JsonUtils.jsonToMap(DATA);
			logger.info("SESSION_ID:" + jsonMap.get("SESSION_ID"));
			if(IMG_CODE.equals(jsonMap.get("validateCode"))){
				return true;
			}
			
		

		return false;
		
		
	
	}

	/**
	 * 保存登录日志
	 *
     * @param request
     * @param ctx
     * @param status
     */
	private void saveLoginLog(HttpServletRequest request, IBusinessContext ctx, boolean status) {
		Map sessLog = ctx.getParamMap();
		String userId = ctx.getParam("USER_ID");
		sessLog.put("SESSION_ID", ctx.getHttpSession().getId());
		sessLog.put("LGN_ID", ctx.getParam("USER_ID"));
		sessLog.put("CLIENT_TYP", ctx.getParam("CLI_TYP"));
		if (status) {
			sessLog.put("LGN_STAT", "1");// 0未设置；1成功；2失败；
		} else {
			sessLog.put("LGN_STAT", "2");
		}
		sessLog.put("SESS_STAT", "1");// 0 待缴活；1 有效； 3 过期；
		sessLog.put("CREATE_DATE", DateUtil.todayStr());
		sessLog.put("CREATE_TIME", DateUtil.curTimeStr());
        sessLog.put("SRV_IP", request.getRemoteAddr());

		String sessSeq = (String) loginSessionService.saveLgnSessLog(sessLog);
		ctx.saveSessionText(SessConsts.SESS_SEQ, sessSeq);
	}

	/**
	 * 用户信息存放到Session中
	 * 
	 * @param ctx
	 * @param user
	 */
	private void saveSession(IBusinessContext ctx, Map user) {
		HttpSession session = ctx.getHttpSession();// 获取缓存中session对象
		ctx.saveSessionText(SessConsts.ISLOGIN, AppConstants.TRUE);
		String user_id = ctx.getParam("USER_ID");
		session.setAttribute(SessConsts.LOGIN_ID, user_id);
		session.setAttribute(SessConsts.ORGAN_ID, user.get("ORG_ID"));
		session.setAttribute(SessConsts.USER_INFO, user);
		bizLogger.info("用户登录成功,登录地点" + ctx.getParam("LOGIN_ADDRESS"), "100101");
	}

	/**
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("ares/login/ClientLogout.do")
	@ResponseBody
	public Map logout(HttpServletRequest request) {
		String transCode = "ares/login/ClientLogout";
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!CtxUtil.transPrev(ctx, transCode, json2MapParamCover,
				requestBuilder, confParser, rst)) {
			return rst;
		}
		// 获取当前用户session
		Session sessionRequired = SecurityUtils.getSession();
		try {
			CtxUtil.transAfter(ctx, transCode, rst, true, responseParser,
					confParser);
			bizLogger.info("用户退出登录", "100201");
		} catch (Exception e) {
			logger.error(ctx.getTransLogBean(transCode), e);
			CtxUtil.transError(ctx, transCode, rst);
		} finally {
			logger.info("sessionId== " + sessionRequired.getId() + " invalidate ...");
			ReusableHttpServletRequest r = (ReusableHttpServletRequest) request;
			r.getOriginalSession().invalidate();
			sessionRequired.invalidate();
		}
		return rst;
	}

	/**
	 * 更新会话状态
	 * 
	 * @param session
	 */
	private void updateSessStat(HttpSession session) {
		Map map = new HashMap();
		map.put(SessConsts.SESSION_ID, session.getId());
		map.put("SESS_STAT_INVAIDATE", "2");// 2表示退出时，所有该session会话都置为失效状态
		loginSessionService.updateSess_SeqStat(map);
	}

	/**
	 * 退出更新日志表
	 * 
	 * @param session
	 */
	private boolean updateLogoutLog(HttpSession session) {
		Map sessLog = new HashMap();
		String lgn_id = (String) session.getAttribute(SessConsts.LOGIN_ID);
		String session_id = (String) session.getAttribute(SessConsts.SESSION_ID);
		if (lgn_id == null) {// session中不存在当前用户信息
			logger.info("=== current session has not find this user info ===");
			return false;
		}
		
	
		
		sessLog.put(SessConsts.LOGIN_ID, lgn_id);
		sessLog.put(SessConsts.SESSION_ID, session_id);
		sessLog.put("LGN_STAT", "1");// 1表示正常登录、2表示异常登录
		sessLog.put("SESS_STAT", "1");// 1表示session会话有效、2表示会话失效
		sessLog.put("LOSE_DATE", DateUtil.todayStr());// 退出时间、日期形式
		sessLog.put("LOSE_TIME", DateUtil.curTimeStr());// 退出时间精确到秒
		sessLog.put("EXIT_STAT", "1");// 退出状态，1、表示正常退出，2表示异常退出

		return loginSessionService.updateLgnStat_ExitStat(sessLog);// 更新日志表
	}
	
	@RequestMapping("ares/login/serviceToHtml5.do")
	@ResponseBody
	public Map<String, Object> serviceToHtml5(HttpServletRequest request) {

		return (Map<String, Object>) SecurityUtils.getSessionRequired().getAttribute(SessConsts.USER_INFO);
	}

	@RequestMapping("ares/getToken.do")
	@ResponseBody
	public Map<String, Object> getToken(HttpServletRequest request) {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("token", SecurityUtils.getSessionRequired().getId());
		return hashMap;
	}

	/**
	 * H5登录：<br>
	 * 1、验证验证码<br>
	 * 2、加载用户信息，验证用户号<br>
	 * 3、验证用户状态是否锁定<br>
	 * 4、验证密码是否正确<br>
	 * 5、更新错误密码登录次数<br>
	 * 6、登录通过，保存会话<br>
	 * 7、记录登录日志<br>
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("ares/login/H5Login.do")
	@ResponseBody
	public Map<String, Object> h5Login(HttpServletRequest request) {
		//bizLogger.info("用户登录申请", "100100");
		String transCode = "ares/login/H5Login";
		Map<String, Object> rst = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!CtxUtil.transPrev(ctx, transCode, json2MapParamCover, requestBuilder, confParser, rst)) {
			bizLogger.warn("用户登录失败，失败原因:" + (String) rst.get("MSG"), "100102");
			return rst;
		}
		// 校验验证码
		if (!checkImageCode(ctx)) {// 验证码不正确
			return CtxUtil.transError(ctx, transCode, rst, AppConstants.STATUS_RANDOM_ERROR, AppConstants.MSG_RANDOM_ERROR);
		}

		boolean status = false;// 默认的是登录失败
		Map<String, Object> params = ctx.getParamMap();// 获取总线中参数Map
		String userId = params.get("USER_ID").toString().trim();
		String saveUserId = params.get("USER_ID").toString().trim();
		Map<String, Object> userInfo = null;
		try {
			userInfo = loginService.loadUserById(saveUserId);// 获取用户信息
			// 如果用户不存在或被注销了，提示用户不存在
			if (userInfo == null) {
				// 用户名不存在
				bizLogger.warn("用户登录失败，失败原因:用户不存在！", "100102");
				return CtxUtil.transError(ctx, transCode, rst, AppConstants.STATUS_USER_ERROR, "用户不存在！");
			}
			// 校验密码输入错误次数
			int errLgnCnt = StringUtil.getInt(userInfo, "ERR_LGN_CNT", 0);
			if (errLgnCnt >= 6) {// 超过6次将会被锁定, 需要人工解锁
				bizLogger.warn("用户登录失败，失败原因:错误次数超限，请联系系统管理员！", "100102");
				return CtxUtil.transError(ctx, transCode, rst, AppConstants.STATUS_USER_LOCKED_ERROR, "错误次数超限，请联系系统管理员！");
			}

			String loginType = ctx.getParam("LOGIN_TYPE");
			Map<String, Object> tmp = new HashMap<String, Object>();
			String curPwd = (String) userInfo.get("USER_PSW");// 验证密码
			String userPwd = ctx.getParam("USER_PSW");
			tmp.put("USER_ID", userId);
			if (!MD5Encrypt.MD5(userPwd).equalsIgnoreCase(curPwd)) {
				errLgnCnt++;
				int hasLgnCnt = 6 - errLgnCnt;
				tmp.put(AppConstants.STATUS, AppConstants.STATUS_PWD_ERROR);// 密码错误
				tmp.put("ERR_LGN_CNT", errLgnCnt);
				loginService.updateErrorLoginCnt(tmp);
				bizLogger.warn("用户登录失败，失败原因:密码错误, 登录失败!", "100102");
				return CtxUtil.transError(ctx, transCode, rst, AppConstants.STATUS_PWD_ERROR, "登录失败!密码错误" + errLgnCnt + "次，剩余" + hasLgnCnt + "次");
			}
			tmp.put(AppConstants.STATUS, AppConstants.STATUS_OK);// 密码正确
			status = true;// 登录成功
			// 所属分行机构ID
			userInfo.put("EXT_ORG_ID", loginService.queryExtOrgId4User(userInfo));
			// 更新会话认证状态
			SecurityUtils.getSessionRequired().setUserId(saveUserId);
			SecurityUtils.getSessionRequired().setAuthStatus("1");
			SecurityUtils.getSessionRequired().setAttribute("orgId", loginService.queryExtOrgId4User(userInfo));
			try {
				loginService.updateErrorLoginCnt(tmp); // 更新密码错误次数
				loginService.updateLastLgnTime(userInfo);//更新最近登录时间
			} catch (Exception e) {
				logger.warn("更新密码错误次数和最近登录时间失败。", e);
			}
			rst.putAll(userInfo);
			// lc3:获取角色列表，用逗号分隔
			List<String> roleIds = loginService.queryRoleIdsByUserId((String) userInfo.get("ID"));
			rst.put("ROLE_IDS", null != roleIds ? StringUtils.collectionToCommaDelimitedString(roleIds) : "");
			saveSession(ctx, userInfo);
			// 
			bizLogger.info("用户登录成功,登录方式:[密码]", "100101");
		} catch (Exception e) {
			// 输出错误的关键信息
			logger.error(ctx.getTransLogBean(transCode), e);
			bizLogger.warn("用户登录失败，失败原因:" + e.getMessage(), "100102");
		} finally {
			if (logger.isDebugEnabled()) {
				logger.debug("start save session log...");
			}
		}
		CtxUtil.transAfter(ctx, transCode, rst, status, responseParser, confParser);
		return rst;
	}

}
