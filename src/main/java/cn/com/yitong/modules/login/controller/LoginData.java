package cn.com.yitong.modules.login.controller;

import java.util.ArrayList;
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
import cn.com.yitong.consts.SessConsts;
import cn.com.yitong.core.util.SecurityUtils;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.vo.DeviceCkeck;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.service.ILoginSessionService;
import cn.com.yitong.ares.deviceInfo.service.DeviceInfoService;
import cn.com.yitong.ares.login.service.LoginService;
import cn.com.yitong.framework.service.impl.SessionMngService;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.util.ConfigEnum;
import cn.com.yitong.util.DateUtil;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

@Controller
public class LoginData extends BaseControl {

	private Logger logger = YTLog.getLogger(this.getClass());
	@Autowired
	ICrudService service;
	@Autowired
	LoginService loginService;
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
	@Qualifier("loginSessionService")
	ILoginSessionService loginSessionService;
	
	@Autowired
	private DeviceInfoService deviceInfoService;

    @Autowired
    private SessionMngService sessionMngService;

	final String BASE_PATH = "login/";

    private static final String ONLYDEVICE = "1";  //单一设备
    private static final String ONLYSESSION = "2";  //单一设备

	/**
	 * 用户信息存放到Session中
	 * 
	 * @param ctx
	 * @param user
	 */
	private void saveSession(IBusinessContext ctx, Map user) {
		HttpSession session = ctx.getHttpSession();// 获取缓存中session对象
		ctx.saveSessionText(SessConsts.ISLOGIN, AppConstants.TRUE);
		String user_id = (String) ctx.getParam("USER_ID");
		session.setAttribute(SessConsts.LOGIN_ID, user_id);
		session.setAttribute(SessConsts.ORGAN_ID, user.get("ORGAN_ID"));
		logger.info("save user ： " + user_id + "  info into sessionId : "
				+ session.getId() + " successful");
	}
	
	/**
	 * 列表查询
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("login/LoginData.do")
	@ResponseBody
	public Map loginData(HttpServletRequest request) {
		
		String _ticket="";
		String transCode = "login/LoginData";
	
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = CtxUtil.createMapContext(request);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}
		if (CtxUtil.debugTrans(transCode)) {
			boolean ok = client.execute(ctx, transCode);
			transAfter(ctx, transCode, rst, ok);
			return rst;
		}
		
		// 校验验证码
		/*
		 * if (!checkImageCode(ctx)) {// 验证码不正确 return CtxUtil.transError(ctx,
		 * transCode, rst, AppConstants.STATUS_RANDOM_ERROR,
		 * AppConstants.MSG_RANDOM_ERROR); }
		 */


		String userId = ctx.getParam("USER_ID");
		boolean status = false;// 默认的是登录失败
		Map params = ctx.getParamMap();// 获取总线中参数Map
		try {
			Map<String, Object> userInfo = loginService.loadUserById(userId);// 获取用户信息
            // 如果用户不存在或被注销了，提示用户不存在
			if (userInfo == null || "0".equals(String.valueOf(userInfo
                    .get("USER_STAUS")))) {
				// 用户名不存在
				return CtxUtil.transError(ctx, transCode, rst,
						AppConstants.STATUS_USER_ERROR, "用户不存在！");
			}
			// 校验密码输入错误次数
			int errLgnCnt = StringUtil.getInt(userInfo, "ERR_LGN_CNT", 0);
			if (errLgnCnt >= 6) {// 超过6次将会被锁定, 需要人工解锁
				return CtxUtil.transError(ctx, transCode, rst,
						AppConstants.STATUS_USER_LOCKED_ERROR,
						"密码错误超过5次已被锁定, 请联系系统管理员解锁!");
			}
			String curPwd = (String) userInfo.get("USER_PSW");// 验证密码
			String userPwd = (String) ctx.getParam("USER_PSW");
			Map<String, Object> tmp = new HashMap<String, Object>();
			tmp.put("USER_ID", userId);
			if (!userPwd.equalsIgnoreCase(curPwd)) {
				tmp.put(AppConstants.STATUS, AppConstants.STATUS_PWD_ERROR);// 密码错误
				loginService.updateErrorLoginCnt(tmp);
				return CtxUtil.transError(ctx, transCode, rst,
						AppConstants.STATUS_PWD_ERROR, "密码错误, 登录失败!");
			}
            // 如果用户已经登陆，不能重新登陆
//            if (SessionListener.isOnline4UserId(userId)) {
//                return CtxUtil.transError(ctx, transCode, rst,
//                                          AppConstants.STATUS_PWD_ERROR,
//                                          "当前用户已在线，请正常退出后再登录！");
//            }
			
            // 客户端类型的用户才能登陆客户端
            if (ConfigEnum.DICT_USER_TYP_WEB.strVal().equals(StringUtil.getString(userInfo, "USER_TYP", ""))) {
                return CtxUtil.transError(ctx, transCode, rst,
                                          AppConstants.STATUS_PWD_ERROR,
                                          "您是后管平台用户，无权限登陆客户端！");
            }
            
            // 增加设备注册验证--擦除 锁定 设置注册 用户绑定最大设备数等
            String deviceUuid = SecurityUtils.getSessionRequired().getDeviceCode();
            if(StringUtil.isEmpty(deviceUuid)) {
            	deviceUuid = (String) ctx.getParam("CLI_DEVICE_NO");
            }
            DeviceCkeck checkResult = deviceInfoService.loginDeviceCheck(deviceUuid, userId);
            rst.put("ERASE_SIGN", checkResult.getEraseSign());
            rst.put("LOCK_SIGN", checkResult.getLockSign());
            rst.put("RESULT", checkResult.getResult());
            if(!checkResult.getIsSuccess()) {
            	return CtxUtil.transError(ctx, transCode, rst,
                        AppConstants.STATUS_FAIL, checkResult.getMessage());
            }


            String forceLogin=(String) ctx.getParam("FORCE_LOGIN_FLAG");
            if(StringUtil.isNotEmpty(forceLogin) && "1".equals(forceLogin)){//1表示强制登录，注销当前在线用户会话
                sessionMngService.updateSessionByUserId(userId);
            }else{
                //并发控制
                int sessionCnt=0;
                String loginAccountLimtType = ConfigUtils.getValue(NS.LOGINACCOUNTLIMTTYPE, "0");
                logger.debug("登录控制类型【0：不控制；1：单一设备；2：单一会话】: "+loginAccountLimtType);
                if(ONLYDEVICE.equals(loginAccountLimtType)){ //单一设备
                    sessionCnt = sessionMngService.getSessionCntByUserAndDeviceId(userId,deviceUuid);
                    logger.debug("根据用户和设备号查询当前所有会话数:"+sessionCnt);
                    if(sessionCnt==0){ //注销历史会话
                        sessionMngService.updateSessionByDeviceId(userId,deviceUuid);
                    }
                }else if(ONLYSESSION.equals(loginAccountLimtType)){ //单一会话
                    sessionCnt = sessionMngService.getSessionCntByUser(userId);
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
                                ctx.setParam("FORCE_LOGIN_STATUS","0");//0表示当前会话已经达到上限，如果要强制登录客户端需传送状态1
                                return CtxUtil.transError(ctx, transCode, rst,
                                        AppConstants.STATUS_FAIL, "会话数已达到上限,是否强制登录");
                            }
                        } else if(0 == loginLimtCnt){
                            return CtxUtil.transError(ctx, transCode, rst,
                                    AppConstants.STATUS_FAIL, "登陆限制数为0");
                        }else{
                            rst.put("CONCURRENCY_RESULT", "成功");
                        }
                    }
                } else {//会话数已达到上限
                    ctx.setParam("FORCE_LOGIN_STATUS","0");//0表示当前会话已经达到上限，如果要强制登录客户端需传送状态1
                    return CtxUtil.transError(ctx, transCode, rst,
                            AppConstants.STATUS_FAIL, "会话数已达到上限,是否强制登录");
                }
            }



			tmp.put(AppConstants.STATUS, AppConstants.STATUS_OK);// 密码正确
			status = true;// 登录成功
			userInfo.put(AppConstants.LAST_LGN_DATE, DateUtil.todayStr());
			userInfo.put(AppConstants.LAST_LGN_TIME, DateUtil.curTimeStr());
            // 所属分行机构ID
            userInfo.put("EXT_ORG_ID", loginService.queryExtOrgId4User(userInfo));

			// 更新会话认证状态
			SecurityUtils.getSessionRequired().setUserId(userId);
			SecurityUtils.getSessionRequired().setAuthStatus("1");

			try {
				loginService.updateErrorLoginCnt(tmp);	// 更新密码错误次数
				loginService.updateLastLgnTime(userInfo);//更新最近登录时间
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
			List<String> roleIds = loginService.queryRoleIdsByUserId(userId);
			rst.put("ROLE_IDS", null != roleIds ? StringUtils.collectionToCommaDelimitedString(roleIds) : "");
			//获取当前时间
			String CURRENT_TIME=null;
			
			//利率查询
			List<Map> rate = new ArrayList<Map>();
			//汇率查询
			List<Map> exRate = new ArrayList<Map>();
			//菜单B查询
			List<Map> menuB = new ArrayList<Map>();
			//菜单A查询
			List<Map> menuA = new ArrayList<Map>();
			//资费查询
			List<Map> charge = new ArrayList<Map>();
			//广告及展架通用接口
			List<Map> resFile = new ArrayList<Map>();
			if(status){
				//获取数据库当前时间
				Map curTime=service.load("RATE_INTE.queryCurTime", params);
				CURRENT_TIME=String.valueOf(curTime.get("CURRENT_TIME"));
				//登录成功即获取menu菜单
				menuA=this.getMenuA(params);
				menuB=this.getMenuB(params);
				String Udate=String.valueOf(params.get("OPERATE_DATE"));
				//如果客戶端传回空值，说明客户端本地并无此数据，故全部加载（极端情况下，若用户手动删除客户端本地文件，也会传回空值）
				if(Udate.equals("null") || Udate.equals("")){
					rate=this.getRate(params);
					exRate=this.getExRate(params);
					charge=this.getCharge(params);
					Map resFiles=new HashMap();
					resFiles.put("ORGAN_LIMIT",userInfo.get("ORG_ID"));
					resFile=this.getResFiles(resFiles);
				}else{
					//首先判断该类信息是否修改（增删改），以此确定是否需要查询最新信息（修改的则查询）
					//非空值，则表示客户端本地有缓存，故查询数据库相应表是否更改
					//利率、汇率、资费
					Map<String,Object> rateObj= new HashMap<String,Object>();
					List list = new ArrayList<String>();
					list.add("rate_inte");
					list.add("exchange_rate");
					list.add("charge");
					rateObj.put("OPERATE_IDS",list);
					rateObj.put("OPERATE_DATE", Udate);
					List isUpdate=service.findList("MGG_TRIGGER_RELATION.query", rateObj);
					//isUdate非空则说明数据有更改
					if(null!=isUpdate && !isUpdate.isEmpty()){
						rate=this.getRate(params);
						exRate=this.getExRate(params);
						charge=this.getCharge(params);
						Map resFiles=new HashMap();
						resFiles.put("ORGAN_LIMIT",userInfo.get("ORG_ID"));
						resFile=this.getResFiles(resFiles);
					}
				}
				rst.put("menuA", menuA);
				rst.put("menuB", menuB);
				rst.put("rate", rate);
				rst.put("exRate", exRate);
				rst.put("charge", charge);
				rst.put("resFile", resFile);
			}
			
		
			
			
			// 单点登陆
            /*SessionListener.logoutOnline4UserId(userId,request.getSession());*/
            
			// 保存会话
			saveSession(ctx, userInfo);
 			rst.put("CURRENT_TIME", CURRENT_TIME);
		}catch (Exception e) {
				// 输出错误的关键信息
				logger.error(ctx.getTransLogBean(transCode), e);
		}
		finally {
			logger.debug("start save session log...");
			// 保存登录日志
			_ticket=saveLoginLog(request, ctx, status);
		}
		rst.put("_ticket", _ticket);
		transAfter(ctx, transCode, rst, status);
		return rst;
	}
	
	/**
	 * 保存登录日志
	 *
     * @param request
     * @param ctx
     * @param status
     */
	private String saveLoginLog(HttpServletRequest request, IBusinessContext ctx, boolean status) {
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
		// 单会话控制
//		singleSession(userId,sessSeq,request.getSession());
		return sessSeq;

	}

	private List getResFiles(Map params){
		List datas = service.findList("MZJ_RES_DATA.pageQuery", params);
		List resDatas= new ArrayList();
		if(datas!=null  && datas.size()>0)
		{
			for(int i =0; i<datas.size();i++)
			{
				Map temp = (HashMap)datas.get(i);
				if(null!=temp.get("PROPA_IMG") && !"".equals(temp.get("PROPA_IMG")) &&
				   null!=temp.get("FILE_ADDR")  && !"".equals(temp.get("FILE_ADDR") )){
					temp.put("PROPA_IMG", "/download/userResource/resources.do?type=resources&fileName="+temp.get("PROPA_IMG"));
					temp.put("FILE_ADDR", "/download/userResource/resources.do?type=resources&fileName="+temp.get("FILE_ADDR"));
				}else{
					temp.put("PROPA_IMG", temp.get("PROPA_IMG"));
					temp.put("FILE_ADDR", temp.get("FILE_ADDR"));
				}
//				temp.put("PROPA_IMG", AppConstants.IMG_WEB_URL+temp.get("PROPA_IMG"));
//				temp.put("FILE_ADDR", AppConstants.IMG_WEB_URL+temp.get("FILE_ADDR"));
				if(("null").equals(String.valueOf(temp.get("PRD_TYPE")))){
					temp.put("PRD_TYPE", "");
				}
				resDatas.add(temp);		
			}
		}
		return resDatas;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List getMenuA(Map params){
		List<Map> menuA=new ArrayList<Map>();
		List datas = service.findList("P_MOBI_ROL_MENU.queryMenuAtypeList", params);
		
		for(int i=0; i<datas.size();i++)
		{
			Map menuListTypeA=(HashMap)datas.get(i);
			if(null!=menuListTypeA.get("FILE_ADDR") && !"".equals(menuListTypeA.get("FILE_ADDR"))){
				if(menuListTypeA.get("FILE_ADDR").toString().trim().length()>0){
					menuListTypeA.put("MENU_IMG", "/download/userResource/resources.do?type=resources&fileName="+menuListTypeA.get("FILE_ADDR"));
				}else{
					menuListTypeA.put("MENU_IMG", menuListTypeA.get("FILE_ADDR"));
				}
			}else{
				menuListTypeA.put("MENU_IMG", menuListTypeA.get("FILE_ADDR"));
			}
//			menuListTypeA.put("MENU_IMG",  AppConstants.IMG_WEB_URL+menuListTypeA.get("FILE_ADDR"));
			menuA.add(menuListTypeA);
		}
		return menuA;
	}
	public static void main(String[] args) {
		String b=null;
		System.out.println(String.valueOf(b));
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List getMenuB(Map params){
		List<Map> menuB=new ArrayList<Map>();
		//菜单查询
		//先获取一级菜单
		List<Map>  menuList1 = new ArrayList();
		params.put("MENU_LEVEL", "1");
		menuList1=service.findList("P_MOBI_ROL_MENU.queryMenuUserBtypeList", params);
		for(int i=0; menuList1.size()>i ;i++)
		{
			Map content=new HashMap();
			//添加一级菜单
			content.put("MENU_ID", menuList1.get(i).get("MENU_ID"));
			content.put("MENU_NAME", menuList1.get(i).get("MENU_NAME"));
			content.put("MENU_URL", menuList1.get(i).get("MENU_URL"));
			if(null!=content.get("FILE_ADDR") && !"".equals(content.get("FILE_ADDR"))){
				if(content.get("FILE_ADDR").toString().trim().length()>0){
					content.put("MENU_IMG", "/download/userResource/resources.do?type=resources&fileName="+content.get("FILE_ADDR"));
				}else{
					content.put("MENU_IMG", content.get("FILE_ADDR"));
				}
			}else{
				content.put("MENU_IMG", content.get("FILE_ADDR"));
			}
//			content.put("MENU_IMG", AppConstants.IMG_WEB_URL+content.get("FILE_ADDR"));
			content.put("MENU_TYP", menuList1.get(i).get("MENU_TYP"));
			content.put("MENU_PAR_ID", menuList1.get(i).get("MENU_PAR_ID"));
			//查询二级菜单
			params.put("MENU_LEVEL", "2");
			params.put("MENU_PAR_ID", menuList1.get(i).get("MENU_ID"));		
			List mentList2= service.findList("P_MOBI_ROL_MENU.queryMenuUserBtypeList", params);
			List menuList2Temp= new ArrayList();
			for(int j =0;j<mentList2.size();j++)
			{
				Map  tempList2=(HashMap)mentList2.get(j);
				if(null!=tempList2.get("FILE_ADDR") && !"".equals(tempList2.get("FILE_ADDR"))){
					if(tempList2.get("FILE_ADDR").toString().trim().length()>0){
						tempList2.put("MENU_IMG", "/download/userResource/resources.do?type=resources&fileName="+tempList2.get("FILE_ADDR"));
					}else{
						tempList2.put("MENU_IMG", tempList2.get("FILE_ADDR"));
					}
				}else{
					tempList2.put("MENU_IMG", tempList2.get("FILE_ADDR"));
				}
//				tempList2.put("MENU_IMG",  AppConstants.IMG_WEB_URL+tempList2.get("FILE_ADDR"));
				menuList2Temp.add(tempList2);
			}
			//添加二级菜单
			content.put("MENU_LIST2", menuList2Temp);		
			menuB.add(content);
		}
		return menuB;
	}
	//获取利率表数据
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List getRate(Map params){
		List<Map> rate=service.findList("RATE_INTE.queryRate", params);
		return rate;
	}
	//获取汇率表数据
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List getExRate(Map params){
		List<Map> exchanger= service.findList("EXCHANGE_RATE.query", params);
		return exchanger;
	}
	//获取资费表数据
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List getCharge(Map params){
		List<Map> charge= new ArrayList<Map>();
		List<Map> proTyp= service.findList("CHARGE.queryProductType", params);
		for(Map typ : proTyp){
			Map chargeMap= new HashMap();
			chargeMap.put("PRODUCT_TYPE_ID", typ.get("PRODUCT_TYPE_ID"));
			chargeMap.put("PRODUCT_TYPE_NAME", typ.get("PRODUCT_TYPE_NAME"));
			chargeMap.put("PRODUCT_TYPE_DESC", typ.get("PRODUCT_TYPE_DESC"));
			List<Map> productInfo = service.findList("CHARGE.queryProductInfo", chargeMap);
			List<Map> PRODUCTLIST= new ArrayList<Map>();
			for(Map pro:productInfo){
				Map proDet = new HashMap();
				proDet.put("PRODUCT_ID", pro.get("PRODUCT_ID"));
				proDet.put("PRODUCT_NAME", pro.get("PRODUCT_NAME"));
				proDet.put("PRODUCT_DESC", pro.get("PRODUCT_DESC"));
				List<Map> chargeInfo = service.findList("CHARGE.queryChargeInfo", proDet);
				List<Map> CHARGELIST= new ArrayList<Map>();
				for(Map charges : chargeInfo){
					Map chargeDet=new HashMap();
					chargeDet.put("CHARGE_NAME",charges.get("CHARGE_NAME"));
					chargeDet.put("CHARGE_COST", charges.get("CHARGE_COST"));
					CHARGELIST.add(chargeDet);
				}
				proDet.put("CHARGELIST", CHARGELIST);
				PRODUCTLIST.add(proDet);
			}
			chargeMap.put("PRODUCTLIST", PRODUCTLIST);
			charge.add(chargeMap);
		}
		return charge;
	}
//	product.put("PRODUCT_TYPE_NAME", typ.get("PRODUCT_TYPE_NAME"));
//	product.put("PRODUCT_TYPE_DESC", typ.get("PRODUCT_TYPE_DESC"));
	/**
	 * 事务前置处理
	 * 
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
	 * 
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
