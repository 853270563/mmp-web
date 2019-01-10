package cn.com.yitong.ares.login.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.yitong.ares.login.BizLogger;
import cn.com.yitong.ares.login.service.LoginService;
import cn.com.yitong.ares.login.service.impl.LoginExpandDataService;
import cn.com.yitong.core.util.SecurityUtils;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.market.mm.myw.busiPage.service.impl.MywBusiPageService;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

@Controller
public class LoginExpandDataController extends BaseControl {
	
	private Logger logger = YTLog.getLogger(this.getClass());
	@Autowired
	ICrudService service;
	@Autowired
	LoginExpandDataService ledService;
	@Autowired
	MywBusiPageService bpsService;
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
	
    final BizLogger bizLogger = BizLogger.getLogger(this.getClass());
    

	@Autowired
	LoginService loginService;
	
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("ares/login/loginExpandData.do")
	@ResponseBody
	public Map<String,Object> loginExpandData(HttpServletRequest request){
		//	bizLogger.info("用户登录扩展信息申请", "100600");
		String transCode = "ares/login/loginExpandData";
		Map<String,Object> rst = new HashMap<String, Object>();
		//初始化总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!CtxUtil.transPrev(ctx, transCode, json2MapParamCover,
				requestBuilder, confParser, rst)) {
			bizLogger.warn("用户登录扩展信息失败， 失败原因:" + (String)rst.get("MSG"), "100602");
			return rst;
		}
		
		Map<String,Object> params = ctx.getParamMap();
		String user_id = (String) params.get("USER_ID");
		
		
		boolean ok = false;
		try {
			//菜单A查询
			List<Map<String,Object>> menuA = ledService.getMenuA(params);
			//菜单B查询
			List<Map<String,Object>> menuB = ledService.getMenuB(params);
            //登录session数据持久化
            this.addSessionLog(params, request);

			rst.put("menuA", menuA);
			rst.put("menuB", menuB);
            ok=true;
			//	bizLogger.info("用户登录扩展信息成功", "100601");
		} catch (Exception e) {
			// 输出错误的关键信息
			logger.error(ctx.getTransLogBean(transCode), e);
			bizLogger.warn("用户登录扩展信息失败， 失败原因:" + e.getMessage(), "100602");
		}
		CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
		return rst;
	}


    /**
     * 登录session数据持久化
     * @param params
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
	public boolean  addSessionLog(Map<String,Object> params, HttpServletRequest request){
        boolean ok = false;
        String pos = (String)params.get("POS");
		String sessLogId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20);
        params.put("SESS_LOG_ID", sessLogId);
        request.getSession().setAttribute("SESS_LOG_ID", sessLogId);
        try {
			params.remove("POS");
			if (pos != null && pos.contains("-")) {
				String[] posArray = pos.split("-");
				params.put("POS_X", posArray[0].trim());
				params.put("POS_Y", posArray[1].trim());
			}
            String appId = (String)params.get("APP_ID");
            String appVers = (String)params.get("APP_VER_ID"); //客户端传的这个该值对应的是map_app_vers表的app_vers字段
			String newAppVersId = getAppVersId(appId, appVers, objToStr(params.get("DEVICE_ID")));
			if(StringUtil.isNotEmpty(newAppVersId)) {
				params.put("APP_VER_ID", newAppVersId);
			}
            String startDtime = objToStr(params.get("APP_START_DTIME")); //应用启动时间
            String stopDtime = objToStr(params.get("APP_STOP_DTIME")); //应用结束时间
            Date appStartDtime = null;
            Date appStopDtime = null;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if(StringUtil.isNotEmpty(startDtime)){
				appStartDtime = formatter.parse(startDtime);
        	}
            if (StringUtil.isNotEmpty(stopDtime)){
                appStopDtime = formatter.parse(stopDtime);
            }
            String sessionId = SecurityUtils.getSession().getId();
            params.put("SESSION_ID", sessionId);
            params.put("APP_START_DTIME", appStartDtime);
            params.put("APP_STOP_DTIME", appStopDtime);
            String userId = (String)params.get("USER_ID");
        // 数据库操作
            Map<String,Object> paramUser = new HashMap<String, Object>();
            paramUser.put("USER_ID", userId);
            service.update("SESSION_LOG.updateByUserId", paramUser);
            service.insert("SESSION_LOG.insert", params);
            ok=true;
        }catch (Exception e){
            logger.error("SESSION_LOG数据处理失败 "+e);
            ok=false;
        }
		return ok;
    }

	/**
	 *
	 * @param appId
	 * @param appVers
	 * @return
	 */
	private String getAppVersId(String appId, String appVers, String deviceId) {
		String appVersId = appVers;
		if(StringUtil.isNotEmpty(appId) && StringUtil.isNotEmpty(appVers)){
			Map<String,Object> paramAppVers = new HashMap<String, Object>();
			String[] versIds = appVers.split("_");
			if(null == versIds || versIds.length ==0) {
				return appVersId ;
			}
			//查询第一个版本
			paramAppVers.put("APP_ID", appId);
			paramAppVers.put("APP_VERS", versIds[0]);
			Map<String, Object> appVersMap = service.load("MAP_APP_VERS.queryAppVersByAppId", paramAppVers);
			if (appVersMap != null && appVersMap.size() > 0) {
				appVersId = appVersMap.get("APP_VERS_ID").toString().replaceAll("/", "\\\\");
			}
			if(1 == versIds.length) {
				return appVersId;
			}else {
				//查询第二个版本
				paramAppVers.put("APP_VERS", versIds[1]);
				Map<String, Object> appVersMapTemp = service.load("MAP_APP_VERS.queryAppVersByAppId", paramAppVers);
				if(null == appVersMap || appVersMap.size() == 0) {
					if(null == appVersMapTemp || appVersMapTemp.size() == 0) {
						return appVersId;
					}else {
						appVersId = appVersMapTemp.get("APP_VERS_ID").toString().replaceAll("/", "\\\\");
					}
				}else {
					if(null == appVersMapTemp || appVersMapTemp.size() == 0) {
						appVersId = appVersMap.get("APP_VERS_ID").toString().replaceAll("/", "\\\\");
					}else {
						String oneVerId = appVersMap.get("APP_VERS_ID").toString().replaceAll("/", "\\\\");
						String twoVerId = appVersMapTemp.get("APP_VERS_ID").toString().replaceAll("/", "\\\\");

						String oneVerIndex = objToStr(appVersMap.get("APP_VERS_INDEX"));
						String twoVerIndex = objToStr(appVersMapTemp.get("APP_VERS_INDEX"));
						if(Long.valueOf(oneVerIndex) > Long.valueOf(twoVerIndex)) {
							appVersId = oneVerId;
						}else {
							appVersId = twoVerId;
						}
					}
				}
			}
			saveDeviceAppInfo(deviceId, appId, appVersId);
		}
		return appVersId;
	}

	/**
	 * 保存设备 应用 版本信息
	 * @param deviceId 设备ID
	 * @param appId 应用ID
	 * @param appVerId 版本Id
	 * @return
	 */
	private Boolean saveDeviceAppInfo(String deviceId, String appId, String appVerId) {
		if(StringUtil.isEmpty(deviceId) || StringUtil.isEmpty(appId) || StringUtil.isEmpty(appVerId)) {
			return Boolean.FALSE;
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("DEVICE_ID", deviceId);
		paramMap.put("APP_ID", appId);
		List<Map<String, Object>> mapList = service.findList("ARES_DEVICE_APPVER.queryDeviceAppVer", paramMap);
		if(null == mapList || mapList.size() == 0) {
			paramMap.put("ID", UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20));
			paramMap.put("CREATE_TIME", new Date());
			paramMap.put("APP_VER_ID", appVerId);
			service.insert("ARES_DEVICE_APPVER.insertDeviceAppVer", paramMap);
		}else {
			Map<String, Object> map = mapList.get(0);
			paramMap.put("ID", objToStr(map.get("ID")));
			paramMap.put("UPDATE_TIME", new Date());
			paramMap.put("APP_VER_ID", appVerId);
			service.update("ARES_DEVICE_APPVER.updateByIdSelective", paramMap);
		}
		return Boolean.TRUE;
	}

	private String objToStr(Object obj) {
		return null == obj?"":obj.toString();
	}
}
