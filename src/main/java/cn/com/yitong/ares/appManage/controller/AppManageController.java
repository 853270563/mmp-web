package cn.com.yitong.ares.appManage.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import cn.com.yitong.ares.appManage.service.CreatePlist;
import cn.com.yitong.ares.appManage.service.CreateUpgradePackService;
import cn.com.yitong.ares.login.BizLogger;
import cn.com.yitong.common.utils.StringUtils;
import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.market.jjk.service.CustomFileMngService;
import cn.com.yitong.util.CustomFileType;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

@Controller
public class AppManageController extends BaseControl {

	private Logger logger = YTLog.getLogger(this.getClass());
	@Autowired
	ICrudService service;
	@Resource
	private CustomFileMngService customFileMngService;
	
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
	
	final String BASE_PATH = "ares/appManage/";
	private static String rootPath = AppConstants.upload_files_path + File.separator + "zip" + File.separator;
	final BizLogger bizLogger = BizLogger.getLogger(this.getClass());
	
	/**
	 * 获取原生资源更新信息
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("ares/appManage/getApkUpdateInfo.do")
	@ResponseBody
	public Map getApkUpdateInfo(HttpServletRequest request) {
		//bizLogger.info("应用版本查询申请", "151101");
		String transCode = BASE_PATH + "getApkUpdateInfo";
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			bizLogger.warn("应用版本查询失败，失败原因：" + (String)rst.get("MSG"), "151103");
			return rst;
		}
		// 数据库操作区
		Map params = ctx.getParamMap();
		boolean ok = false;
		if ("1090".equals(params.get("APP_ID"))||"1080".equals(params.get("APP_ID"))) {
			try {
				// 查询当前版本序列
				Map appVersIndexMap = service.load("MAP_APP_VERS.loadAppVersIndex", params);
				// 如果应用版本不存在的话
				if (appVersIndexMap == null || appVersIndexMap.isEmpty()) {
					appVersIndexMap = service.load("MAP_APP_VERS.loadEarliestAppVers", params);
				}
				if (appVersIndexMap != null && !appVersIndexMap.isEmpty()) {
					// 查询所有大于当前版本序列的应用版本
					params.putAll(appVersIndexMap);
					List<Map> appVersList = service.findList("MAP_APP_VERS.queryAppVersList", params);
					// 查询结果不为空的情况下
					if (appVersList != null && !appVersList.isEmpty()) {
						Map ipaMap = appVersList.get(0);
						rst.put("STATUS", "1");
						rst.put("RES_FORCE_UPDATE", ipaMap.get("APP_IS_UPDATE"));
						rst.put("LATEST_VERS", ipaMap.get("APP_VERS"));
						rst.put("RES_VERS_MEMO", ipaMap.get("APP_VERS_DESC"));
						rst.put("RES_URL", "");
						rst.put("RES_PATH", "http://36.32.193.150:8080/mmp-web/iosUpdate.html");
						rst.put("PLIST_FILE_PATH", "");
						rst.put("RES_SIZE", "");
					}else{
						rst.put("STATUS", "0");
						rst.put("MSG", "查询可更新版本列表为空");
					}
				}else{
					rst.put("STATUS", "2");
					rst.put("MSG", "没有查询到当前版本号");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			try {
				// 查询当前版本序列
				Map appVersIndexMap = service.load("MAP_APP_VERS.loadAppVersIndex", params);
				// 如果应用版本不存在的话
				if (appVersIndexMap == null || appVersIndexMap.isEmpty()) {
					appVersIndexMap = service.load("MAP_APP_VERS.loadEarliestAppVers", params);
				}
				if (appVersIndexMap != null && !appVersIndexMap.isEmpty()) {
					// 查询所有大于当前版本序列的应用版本
					params.putAll(appVersIndexMap);
					List<Map> appVersList = service.findList("MAP_APP_VERS.queryAppVersList", params);
					// 查询结果不为空的情况下
					if (appVersList != null && !appVersList.isEmpty()) {
						//过滤出原生资源版本
						List<Map> nativeAppVersList = new ArrayList<Map>();
						String resType = "";
						for (Map map : appVersList) {
							resType = getValue(map.get("RES_TYPE"));
							if ("1".equals(resType)) {
								nativeAppVersList.add(map);
							}
						}
						if(nativeAppVersList.size() == 0) {
							appVersList = service.findList("MAP_APP_VERS.queryAppEqualVersList", params);
							nativeAppVersDeal(appVersList, rst, params);
						}else {
							nativeAppVersDeal(nativeAppVersList, rst, params);
						}
					}else {
						appVersList = service.findList("MAP_APP_VERS.queryAppEqualVersList", params);
						nativeAppVersDeal(appVersList, rst, params);
					}
				//	bizLogger.info("应用版本查询成功", "151102");
					ok = true;
				} else {
		            rst.put("STATUS", "0");
					rst.put("MSG", "未查询到当前应用信息!");
					if(logger.isInfoEnabled()) {
						logger.info("未查询到当前应用信息！");
					}
				//	bizLogger.warn("应用版本查询失败，失败原因：未查询到当前应用信息！", "151103");
					ok = false;
				}
			} catch (Exception e) {
				// 输出错误的关键信息
				ok = false;
				logger.error(ctx.getTransLogBean(transCode), e);
				bizLogger.warn("应用版本查询失败，失败原因:" + e.getMessage(), "151103");
			}
			transAfter(ctx, transCode, rst, ok);
		}
		return rst;
	}

	/**
	 * 原生资源版本处理
	 * @param nativeAppVersList 待处理列表
	 * @param rst 返回参数
	 * @param params 请求参数
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void nativeAppVersDeal(List<Map> nativeAppVersList, Map rst, Map params) throws Exception{
		String loginName = getValue(params.get("USER_LOGIN_NAME"));
		//匿名用户 查询推广的版本
		if (StringUtil.isEmpty(loginName)) {
			List<Map> canUserVersList = new ArrayList<Map>();
			String extendStatus = "";
			for (Map map : nativeAppVersList) {
				extendStatus = getValue(map.get("APP_EXTEND_STATUS"));
				if ("3".equals(extendStatus.trim())) {
					canUserVersList.add(map);
				}
			}
			nativeAppVersList = canUserVersList;
		} else {
			//根据用户机构过滤权限
			Map officeMap = service.load("MAP_APP_VERS.selectUserOfficeId", params);
			params.putAll(officeMap);
			// 查询用户所在机构的父机构和子机构
			List<Map> userOrgnMapList = service.findList("MAP_APP_VERS.selectUserOrgnInfo", params);
			List<String> userOrgnList = new ArrayList<String>();
			if (userOrgnMapList != null && !userOrgnMapList.isEmpty()) {
				for (Map tempMap : userOrgnMapList) {
					String id = tempMap.get("ID").toString();
					if (!userOrgnList.contains(id)) {
						userOrgnList.add(tempMap.get("ID").toString());
					}
					String parentIds = tempMap.get("PARENT_IDS").toString();
					String[] parentId = parentIds.split(",");
					for (String tempStr : parentId) {
						if (!tempStr.trim().isEmpty() && !userOrgnList.contains(tempStr.trim())) {
							userOrgnList.add(tempStr.trim());
						}
					}
				}
			}

			// 查询应用版本使用机构列表
			List<String> appVersIdList = new ArrayList<String>();
			for (Map tempMap : nativeAppVersList) {
				appVersIdList.add(tempMap.get("APP_VERS_ID").toString());
			}
			params.put("appVersIdList", appVersIdList);
			List<Map> orgnList = service.findList("MAP_APP_VERS.selectAppVersOrgn", params);
			// key：应用版本编号，value：使用机构列表
			Map<String, List<String>> appVersOrgnMap = new HashMap<String, List<String>>();
			if (orgnList != null && !orgnList.isEmpty()) {
				for (Map<String, String> tempMap : orgnList) {
					String appVersId = tempMap.get("APP_VERS_ID");
					String appOrgn = tempMap.get("APP_ORGN");
					List<String> appOrgnList = appVersOrgnMap.get(appVersId);
					if (appOrgnList == null) {
						appOrgnList = new ArrayList<String>();
						appVersOrgnMap.put(appVersId, appOrgnList);
					}
					appOrgnList.add(appOrgn);
				}
			}

			// 找出当前用户不可用的应用版本
			List<String> delAppVersIdList = new ArrayList<String>();
			Set<Entry<String, List<String>>> entrySet = appVersOrgnMap.entrySet();
			for (Entry<String, List<String>> entry : entrySet) {
				boolean tag = false;
				for (String orgnId : userOrgnList) {
					if (entry.getValue().contains(orgnId)) {
						tag = true;
						break;
					}
				}
				if (!tag) {
					delAppVersIdList.add(entry.getKey());
				}
			}
			// 将该用户不可用的版本从版本列表中删除
			if (!delAppVersIdList.isEmpty()) {
				int length = nativeAppVersList.size();
				for (int i = length - 1; i >= 0; i--) {
					Map map = nativeAppVersList.get(i);
					if (delAppVersIdList.contains(map.get("APP_VERS_ID").toString())) {
						nativeAppVersList.remove(i);
					}
				}
			}
		}

		//处理过滤后的版本集合
		if (nativeAppVersList.isEmpty()) {
			rst.put("STATUS", "1");
			rst.put("MSG", "查询可更新版本列表为空");
		} else {
			rst.put("RES_FORCE_UPDATE", "0");
			for (Map map : nativeAppVersList) {
				if (rst.get("LATEST_VERS") == null) {
					rst.put("LATEST_VERS", map.get("APP_VERS"));
					rst.put("RES_VERS_MEMO", map.get("APP_VERS_DESC"));

					// 获取应用操作系统
					Map mapApp = service.load("MAP_APP_VERS.selectAppOS", params);
					String appOS = mapApp.get("APP_OS").toString();
					// 安卓
					if ("1".equals(appOS) || "3".equals(appOS)) {
						// 本地上传
						if ("1".equals(map.get("RES_UPLOAD_MODE"))) {
							rst.put("RES_URL", "");
							rst.put("RES_PATH", map.get("RES_PATH"));
							rst.put("PLIST_FILE_PATH", "");
							rst.put("RES_SIZE", map.get("RES_SIZE"));
							// 镜像地址
						} else {
							rst.put("RES_URL", map.get("RES_URL"));
							rst.put("RES_SIZE", this.getNetFileSize((String) map.get("RES_URL")));
							rst.put("RES_PATH", "");
							rst.put("PLIST_FILE_PATH", "");
						}
						// IOS
					} else if ("2".equals(appOS) || "4".equals(appOS)) {
						rst.put("RES_URL", "");
						rst.put("RES_PATH", "");
						// 已经有plist文件
						if (map.get("PLIST_FILE_PATH") != null) {
							rst.put("PLIST_FILE_PATH", map.get("PLIST_FILE_PATH"));
							rst.put("RES_SIZE", map.get("RES_SIZE"));
							// 没有plist文件
						} else {
							String resFileDownLoadUrl = "";
							String plistFilePath = "";
							Object resSize = null;
							// 资源文件为服务器本地文件
							if (map.get("RES_PATH") != null) {
								String resFileName = map.get("RES_PATH").toString();
								resSize = map.get("RES_SIZE");
								resFileDownLoadUrl = AppConstants.PRO_RELEASE_HOST + "/ares/appManage/fileDownLoad.do?RES_PATH=" + resFileName;
								plistFilePath = AppConstants.upload_files_path + "/ipa/" + resFileName.substring(0, resFileName.lastIndexOf(".")) + ".plist";
								// 资源文件为镜像文件
							} else if (map.get("RES_URL") != null) {
								resFileDownLoadUrl = map.get("RES_URL").toString();
								SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
								plistFilePath = AppConstants.upload_files_path + "/ipa/" + simpleDateFormat.format(new Date()) + ".plist";
								resSize = this.getNetFileSize(resFileDownLoadUrl);
							}
							File plistFile = CreatePlist.createPlist(resFileDownLoadUrl, plistFilePath);
							rst.put("PLIST_FILE_PATH", plistFile.getName());
							rst.put("RES_SIZE", resSize);
							Map tempMap = new HashMap();
							tempMap.put("PLIST_FILE_PATH", plistFile.getName());
							tempMap.put("RES_SIZE", resSize);
							tempMap.put("APP_VERS_ID", map.get("APP_VERS_ID"));
							service.update("MAP_APP_VERS.updatePlistFilePath", tempMap);
						}
					}
				}
				// 是否需要强制更新
				if (null != map.get("APP_IS_UPDATE") && "1".equals(map.get("APP_IS_UPDATE").toString())) {
					rst.put("RES_FORCE_UPDATE", "1");
					break;
				}
			}
		}
	}

	/**
	 * 获取HTML资源包更新信息
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes"})
	@RequestMapping("ares/appManage/getHtmlUpdateInfo.do")
	@ResponseBody
	public Map getHtmlUpdateInfo(HttpServletRequest request) {
		bizLogger.info("资源版本查询申请", "151201");
		String transCode = BASE_PATH + "getHtmlUpdateInfo";
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			bizLogger.warn("资源版本查询失败，失败原因：" + (String)rst.get("MSG"), "151203");
			return rst;
		}
		// 数据库操作区
		Map params = ctx.getParamMap();
		boolean ok = false;
		try {
			// 查询当前版本序列
			Map appVersIndexMap = service.load("MAP_APP_VERS.loadAppVersIndex", params);
			// 如果应用版本不存在的话
			if (appVersIndexMap == null || appVersIndexMap.isEmpty()) {
				appVersIndexMap = service.load("MAP_APP_VERS.loadEarliestAppVers", params);
			}
			if (appVersIndexMap != null && !appVersIndexMap.isEmpty()) {
				// 查询所有大于当前版本序列的应用版本
				params.putAll(appVersIndexMap);
				List<Map> appVersList = service.findList("MAP_APP_VERS.queryAppVersList", params);
				// 查询结果不为空的情况下
				if (appVersList != null && !appVersList.isEmpty()) {
					// 获取应用类型
					params.put("LATEST_VERS", appVersList.get(0).get("APP_VERS"));
					Map appMap = service.load("MAP_APP_VERS.loadResPath", params);
					// 混合应用
					if ("2".equals(appMap.get("APP_TYPE").toString()))  {
						
						// 资源类型为原生资源的，从列表中删除
		    			int length = appVersList.size();
		    			for (int i = length - 1; i >= 0; i --) {
		    				Map map = appVersList.get(i);
		    				if ("1".equals(map.get("RES_TYPE").toString())) {
		    					appVersList.remove(i);
		    				}
		    			}
		    			// 列表为空，返回
			    		if (appVersList.isEmpty()) {
			    			return this.getRst(ctx, transCode);
			    		}
	    				
			    		// 更新权限控制
						/*boolean accesControl = this.accesControl(ctx, transCode, params, appVersList);
						if (!accesControl) {
							return this.getRst(ctx, transCode);
						}*/
			    		
						this.genResultMap(rst, appVersList);
						// 应用密钥
						rst.put("SECRET_KEY", appMap.get("SECRET_KEY"));
						// 原生应用
					} else {
						return this.getRst(ctx, transCode);
					}
				} else {
					return this.getRst(ctx, transCode);
				}
				ok = true;
				bizLogger.info("资源版本查询成功", "151202");
			} else {
	            rst.put("STATUS", "0");
				ok = false;
				bizLogger.warn("资源版本查询失败，失败原因：应用编号不存在！", "151203");
			}
		} catch (Exception e) {
			// 输出错误的关键信息
			ok=false;
			logger.error(ctx.getTransLogBean(transCode), e);
			bizLogger.warn("资源版本查询失败，失败原因：" + e.getMessage(), "151203");
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}
	
	/**
	 * 更新权限控制
	 * @param ctx
	 * @param transCode
	 * @param params
	 * @param appVersList
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean accesControl(IBusinessContext ctx, String transCode, Map params, List<Map> appVersList){
		// 匿名用户
		/*if(null!=(String)params.get("USER_LOGIN_NAME")&&params.get("USER_LOGIN_NAME").toString().length()==4){
			params.put("USER_LOGIN_NAME", params.get("USER_LOGIN_NAME").toString().trim());
		}*/
		if (StringUtils.isEmpty((String)params.get("USER_LOGIN_NAME"))) {
			while(!appVersList.isEmpty()){
				Map map = appVersList.get(0);
				// 推广状态不是"3"(已推广)
				if (!"3".equals(map.get("APP_EXTEND_STATUS"))) {
					appVersList.remove(0);
				} else {
					break;
				}
			}
			// 列表为空，返回
    		if (appVersList.isEmpty()) {
    			return false;
    		}
			// 非匿名用户，权限控制
		} else {
			Map officeMap =  service.load("MAP_APP_VERS.selectUserOfficeId", params);
			params.putAll(officeMap);
			// 查询用户所在机构的父机构和子机构
			List<Map> userOrgnMapList = service.findList("MAP_APP_VERS.selectUserOrgnInfo", params);
			List<String> userOrgnList = new ArrayList<String>();
			if (userOrgnMapList != null && !userOrgnMapList.isEmpty()) {
				for (Map tempMap : userOrgnMapList ){
					String id = tempMap.get("ID").toString();
					if (!userOrgnList.contains(id)) {
						userOrgnList.add(tempMap.get("ID").toString());
					}
					String parentIds = tempMap.get("PARENT_IDS").toString();
					String[] parentId = parentIds.split(",");
					for (String tempStr : parentId) {
						if (!tempStr.trim().isEmpty() && !userOrgnList.contains(tempStr.trim())) {
							userOrgnList.add(tempStr.trim());
						}
					}
				}
			}
			// 查询应用版本使用机构列表
			List<String> appVersIdList = new ArrayList<String>();
			for (Map tempMap : appVersList) {
				appVersIdList.add(tempMap.get("APP_VERS_ID").toString());
			}
			params.put("appVersIdList", appVersIdList);
			List<Map> orgnList = service.findList("MAP_APP_VERS.selectAppVersOrgn", params);
    		// key：应用版本编号，value：使用机构列表
    		Map<String, List<String>> appVersOrgnMap = new HashMap<String, List<String>>();
    		if (orgnList != null && !orgnList.isEmpty()) {
        		for (Map<String, String> tempMap : orgnList) {
        			String appVersId = tempMap.get("APP_VERS_ID");
        			String appOrgn = tempMap.get("APP_ORGN");
        			List<String> appOrgnList = appVersOrgnMap.get(appVersId);
        			if (appOrgnList == null) {
        				appOrgnList = new ArrayList<String>();
        				appVersOrgnMap.put(appVersId, appOrgnList);
        			}
        			appOrgnList.add(appOrgn);
        		}
    		}
    		// 找出当前用户不可用的应用版本
    		List<String> delAppVersIdList = new ArrayList<String>();
    		Set<Entry<String, List<String>>> entrySet = appVersOrgnMap.entrySet();
    		for (Entry<String, List<String>> entry : entrySet) {
	    		boolean flag = false;
    			for (String orgnId : userOrgnList) {
    				if (entry.getValue().contains(orgnId)) {
    					flag = true;
    					break;
    				}
    			}
    			if (!flag) {
    				delAppVersIdList.add(entry.getKey());
    			}
    		}
    		// 将该用户不可用的版本从版本列表中删除
    		if (!delAppVersIdList.isEmpty()) {
    			int length = appVersList.size();
    			for (int i = length - 1; i >= 0; i --) {
    				Map map = appVersList.get(i);
					if("3".equals(map.get("APP_EXTEND_STATUS"))) {
						continue;
					}
    				if (delAppVersIdList.contains(map.get("APP_VERS_ID").toString())) {
    					appVersList.remove(i);
    				}
    			}
    		}
    		if (appVersList.isEmpty()) {
    			return false;
    		}
		}
		return true;
	}
	
	/**
	 * 
	 * @param rst
	 * @param appVersList
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void genResultMap(Map rst, List<Map> appVersList) throws Exception{
		// 设置最新版本和强制更新标识
		rst.put("LATEST_VERS", appVersList.get(0).get("APP_VERS"));
		rst.put("WEB_RES_FORCE_UPDATE", "0");
		for (Map map : appVersList) {
			// 需要强制更新
			if (null != map.get("APP_IS_UPDATE") && "1".equals(map.get("APP_IS_UPDATE").toString())) {
				rst.put("WEB_RES_FORCE_UPDATE", "1");
				break;
			}
		}
		int webPackSize = 0;
		if ("1".equals(appVersList.get(0).get("RES_UPLOAD_MODE"))) {
			if (appVersList.get(0).get("RES_SIZE") != null && appVersList.get(0).get("RES_SIZE") instanceof Long) {
				webPackSize = ((Long)appVersList.get(0).get("RES_SIZE")).intValue();
			} else if (appVersList.get(0).get("RES_SIZE") != null) {
				webPackSize = ((BigDecimal)appVersList.get(0).get("RES_SIZE")).intValue();
			}
		} else {
			webPackSize = this.getNetFileSize(appVersList.get(0).get("RES_URL").toString());
		}
		int allResUpPackSize = 0;
		List<Map> list = new ArrayList<Map>();
		for (Map map : appVersList) {
			// 某个版本没有升级包的情况下
			if (("1".equals(map.get("RES_UPLOAD_MODE")) && StringUtils.isEmpty((String)map.get("RES_UPDATE_PACK_PATH")))
					|| ("2".equals(map.get("RES_UPLOAD_MODE")) && StringUtils.isEmpty((String)map.get("RES_UPDATE_PACK_URL")))) {
				allResUpPackSize = webPackSize + 1;
				break;
			}
			Map tempMap = new HashMap();
			int RES_UPDATE_PACK_SIZE;
			if ("1".equals(map.get("RES_UPLOAD_MODE"))) {
				RES_UPDATE_PACK_SIZE = Integer.parseInt(map.get("RES_UPDATE_PACK_SIZE").toString());
				allResUpPackSize += RES_UPDATE_PACK_SIZE;
				tempMap.put("RES_UPDATE_PACK_URL", "");
				tempMap.put("RES_UPDATE_PACK_PATH", map.get("RES_UPDATE_PACK_PATH").toString());
			} else {
				RES_UPDATE_PACK_SIZE = this.getNetFileSize((String)map.get("RES_UPDATE_PACK_URL"));
				allResUpPackSize += RES_UPDATE_PACK_SIZE;
				tempMap.put("RES_UPDATE_PACK_URL", map.get("RES_UPDATE_PACK_URL"));
				tempMap.put("RES_UPDATE_PACK_PATH", "");
			}
			tempMap.put("RES_UPDATE_PACK_INDEX", map.get("APP_VERS_INDEX"));
			tempMap.put("RES_UPDATE_PACK_SIZE", RES_UPDATE_PACK_SIZE);
			list.add(tempMap);
		}
		// 升级包总大小大于最新的web资源包大小的八成 或者大于当千版本的个数大于1
		if (allResUpPackSize >= webPackSize * 0.8 || appVersList.size() > 1) {
			if ("1".equals(appVersList.get(0).get("RES_UPLOAD_MODE"))) {
				rst.put("WEB_RES_PATH", appVersList.get(0).get("RES_PATH"));
				rst.put("WEB_RES_SIZE", appVersList.get(0).get("RES_SIZE"));
				rst.put("WEB_RES_URL", "");
			} else {
				rst.put("WEB_RES_URL", appVersList.get(0).get("RES_URL").toString());
				rst.put("WEB_RES_SIZE", this.getNetFileSize(appVersList.get(0).get("RES_URL").toString()));
				rst.put("WEB_RES_PATH", "");
			}

			list = new ArrayList<Map>();
			Map tempMap = new HashMap();
			tempMap.put("RES_UPDATE_PACK_PATH", "");
			tempMap.put("RES_UPDATE_PACK_SIZE", "");
			tempMap.put("RES_UPDATE_PACK_URL", "");
			tempMap.put("RES_UPDATE_PACK_INDEX", "");
			list.add(tempMap);
			rst.put("LIST", list);
		} else {
			rst.put("LIST", list);
			rst.put("WEB_RES_PATH", "");
			rst.put("WEB_RES_SIZE", "");
			rst.put("WEB_RES_URL", "");
		}
	}
	
	/**
	 * 返回结果
	 * @param ctx
	 * @param transCode
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map getRst(IBusinessContext ctx, String transCode ){
		Map rst = new HashMap();
		boolean ok = true;
		rst.put("WEB_RES_URL", "");
		rst.put("WEB_RES_PATH", "");
		rst.put("WEB_RES_SIZE", "");
		rst.put("SECRET_KEY", "");
		rst.put("LATEST_VERS", "");
		rst.put("WEB_RES_FORCE_UPDATE", "");
		ArrayList<Map> list = new ArrayList<Map>();
		Map tempMap = new HashMap();
		tempMap.put("RES_UPDATE_PACK_URL", "");
		tempMap.put("RES_UPDATE_PACK_PATH", "");
		tempMap.put("RES_UPDATE_PACK_SIZE", "");
		tempMap.put("RES_UPDATE_PACK_INDEX", "");
		list.add(tempMap);
		rst.put("LIST", list);
		transAfter(ctx, transCode, rst, ok);
		bizLogger.info("资源版本查询成功", "151202");
		return rst;
	}

	/**
	 * 下载资源包
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("ares/appManage/fileDownLoad.do")
	@ResponseBody
	public Map fileDownLoad(HttpServletRequest request, HttpServletResponse response) {
		//bizLogger.info("应用资源包下载开始", "151301");
		String transCode = BASE_PATH + "fileDownLoad";
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}
        if (CtxUtil.debugTrans("loadInfoGT")) {
            boolean ok = client.execute(ctx, transCode);
            transAfter(ctx, transCode, rst, ok);
            return rst;
        }
		// 数据库操作区
		Map params = ctx.getParamMap();
		boolean ok = false;
		try {
			String filePath = AppConstants.upload_files_path;
			String[] resPathes = params.get("RES_PATH").toString().split(",");
			List<File> resFileList = new ArrayList<File>();
			for (String fileName : resPathes) {
				if (fileName.isEmpty()){
					continue;
				}
				String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
				if ("plist".equals(fileType.toLowerCase())) {
					fileType = "ipa";
				}
				File file = new File((filePath + "/" + fileType + "/" + fileName).replaceAll("//", "/"));
				String logdata = "文件下载：" + file.getAbsolutePath();
				bizLogger.info(logdata, "150002");
				resFileList.add(file);
			}
			Map paramMap = new HashMap();
			List<String> list = Arrays.asList(resPathes);
			Collections.sort(list);
			if (list.size() == 1) {
				paramMap.put("RES_PATH", resPathes[0]);
			} else {
				paramMap.put("RES_PATH", list.get(list.size() - 1));
			}
			Map appVersMap = service.load("MAP_APP_VERS.queryAppVersInfo", paramMap);
			if (appVersMap != null) {
				String logdata = appVersMap.get("APP_ID") + "," + appVersMap.get("APP_VERS_ID") ;
				bizLogger.info(logdata, "150002");
			}

			File outFile = null;
			// 只有zip文件
			boolean zipFileFlag = true;
			if (resFileList.size() == 1) {
				File file = resFileList.get(0);
				String fileName = file.getName();
				if ("apk".equals(fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase())
						|| "ipa".equals(fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase())
						|| "plist".equals(fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase())) {
					outFile = file;
					zipFileFlag = false;
				}
			}
			// 只有zip文件的话，不用做成zip压缩包(web资源包除外)
			ZipFile zipFile = null;
			if (zipFileFlag) {
				zipFile = this.createZipFile(filePath, list);
				String passwd = (String)appVersMap.get("SECRET_KEY");

				ZipParameters parameters = new ZipParameters();
				parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
				parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

				parameters.setEncryptFiles(true);
				parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
				parameters.setPassword(passwd);

				ArrayList filesToAdd = new ArrayList();
				try {
					for (File file : resFileList) {
						filesToAdd.add(file);
					}
					zipFile.addFiles(filesToAdd, parameters);
				} catch(Exception e){
					e.printStackTrace();
					throw e;
				} finally {
				}
				if(null != zipFile) {
					outFile = zipFile.getFile();
				}
			}

			FileInputStream inputStream = null;
			try {
				inputStream = new FileInputStream(outFile);
				response.setHeader("Content-Disposition", "attachment; filename=\"" + outFile.getName() + "\"");
				response.setHeader("Content-Type", "application/octet-stream");
				response.setContentLength((int) outFile.length());
				FileCopyUtils.copy(inputStream, response.getOutputStream());
			} catch (FileNotFoundException e) {
				throw new FileNotFoundException("请求的文件不存在");
			} catch (IOException e) {
				throw new IOException("输出文件出错，请联系管理员", e);
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}
				response.getOutputStream().close();
			}
			ok = true;
			//	bizLogger.info("应用资源包下载成功", "151302");
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);// 返回404状态码
			// 输出错误的关键信息
			ok=false;
			bizLogger.warn("应用资源包下载失败，失败原因：" + e.getMessage(), "151303");
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return null;
	}
	
	/**
	 * 单文件下载
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings({"rawtypes" })
	@RequestMapping("ares/appManage/oneFileDownLoad.do")
	@ResponseBody
	public Map oneFileDownLoad(HttpServletRequest request, HttpServletResponse response) {
		bizLogger.info("应用资源包下载开始", "151301");
		String trans_code = "fileDownLoad";
        String transCode = BASE_PATH + trans_code;
        Map rst = new HashMap();
        // 初始化数据总线
        IBusinessContext ctx = new BusinessContext(request,
                IBusinessContext.PARAM_TYPE_MAP);
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
		try {
			String filePath = AppConstants.upload_files_path;
			String fileName = params.get("RES_PATH").toString();
			String fileType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
			if ("plist".equals(fileType)) {
				fileType = "ipa";
			}
			File file = new File(filePath + File.separator + fileType + File.separator + fileName);
			String range = request.getHeader("Range");
			int startLength = 0;
			if(StringUtil.isNotEmpty(range)) {
				String[] ranges = range.split("-");
				if(null != ranges && ranges.length > 0) {
					startLength = Integer.valueOf(ranges[0]);
				}
			}

			response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
			response.setHeader("Content-Type", "application/octet-stream");
			response.setContentLength((int) file.length() - startLength);
			FileInputStream inputStream = null;
			try {
				inputStream = new FileInputStream(file);
				inputStream.skip(startLength);
				FileCopyUtils.copy(inputStream, response.getOutputStream());
			} catch (FileNotFoundException e) {
				throw new FileNotFoundException("请求的文件不存在");
			} catch (IOException e) {
				throw new IOException("输出文件出错，请联系管理员", e);
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}
				response.getOutputStream().close();
			}
			bizLogger.info("应用资源包下载成功", "151302");
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);// 返回404状态码
			// 输出错误的关键信息
			bizLogger.warn("应用资源包下载失败，失败原因：" + e.getMessage(), "151303");
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		return null;
	}
	
	/**
	 * 创建新文件
	 */
	private File createFile(String filePath, List<String> list){
		String begin = list.get(0).replaceAll("_upgradepack", "").replaceAll(".zip", "");
		String end = list.get(list.size() - 1).replaceAll("_upgradepack", "").replaceAll(".zip", "");
		String fileName = null;
		if (list.get(0).contains("_upgradepack.zip")) {
			fileName = begin + "_" + end +"_upgradepack.zip";
		} else {
			fileName = begin + "_" + end +".zip";
		}
		 File file = new File(filePath + File.separator + "zip" + File.separator + fileName);
		 return file;
	}

	/**
	 * 创建zip文件
	 */
	private ZipFile createZipFile(String filePath, List<String> list) throws Exception{
		String begin = list.get(0).replaceAll("_upgradepack", "").replaceAll(".zip", "");
		String end = list.get(list.size() - 1).replaceAll("_upgradepack", "").replaceAll(".zip", "");
		String fileName = null;
		if (list.get(0).contains("_upgradepack.zip")) {
			fileName = begin + "_" + end +"_upgradepack.zip";
		} else {
			fileName = begin + "_" + end +".zip";
		}
		ZipFile file = new ZipFile(filePath + File.separator + "zip" + File.separator + fileName);
		return file;
	}
	
	private int getNetFileSize(String URLName) throws Exception{
		// 创建URL
		URL url = new URL(URLName); 
		// 试图连接并取得返回状态码urlconn.connect();
		URLConnection urlconn = url.openConnection(); 
		HttpURLConnection httpconn = (HttpURLConnection) urlconn;
		// 服务器返回的状态,不等于HTTP_OK说明连接不成功
		if (httpconn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			bizLogger.error("连接失败！资源更新出错！", "150002");
			logger.error("连接失败！资源更新出错！");
			throw new Exception(URLName + "连接失败！资源更新出错！");
		} else {
			// 取数据长度
			return urlconn.getContentLength();
		}
	}
	
	/**
	 * 事务前置处理
	 * @param ctx
	 * @param transCode
	 * @param rst
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private boolean transPrev(IBusinessContext ctx, String transCode, Map rst) {
		return CtxUtil.transPrev(ctx, transCode, json2MapParamCover,
				requestBuilder, confParser, rst);
	}

	/**
	 * 事务之后处理
	 * @param ctx
	 * @param transCode
	 * @param rst
	 * @param ok
	 */
	@SuppressWarnings("rawtypes")
	private void transAfter(IBusinessContext ctx, String transCode, Map rst,
			boolean ok) {
		CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
	}
	
	/**
	 * 添加应用版本
	 * @param
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("ares/appManage/saveAppVers.do")
	@ResponseBody
    public Map saveAppVers(String appId,MultipartHttpServletRequest request) {
        bizLogger.info("添加应用版本", "151101");
        String transCode = BASE_PATH + "saveAppVers";
        Map rst = new HashMap();
        // 初始化数据总线
        IBusinessContext ctx = new BusinessContext(request,
                IBusinessContext.PARAM_TYPE_MAP);
        // 检查报文定义
/*		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}*/
        // 数据库操作区
        Assert.notNull(appId, "应用编号不能为空");
        Map params = ctx.getParamMap();
        params.put("APP_ID", appId);
        boolean ok = false;
        String appVers="";
        try {
            List<Map<String, Object>> resFiles = null;
            List<MultipartFile> files = request.getFiles("file");
            if(null != files && files.size()>0){
                resFiles= this.uploadFiles(files);
            }else
            {
                logger.info("版本创建失败，没有上传文件！");
                ok=false;
                rst.put("STATUS", "0");
                rst.put("MSG", "上传版本成功，当前版本相同！");
            }
            /**************创建资源升级包BEGIN***************/
			String resType = "2"; //"资源类型  原生资源：1，   web资源：2，
            // 上传本地web资源包
            if (resFiles != null && !resFiles.isEmpty()) {
                String fileName = resFiles.get(0).get("fileName").toString();
                String newFilePath = rootPath + fileName;
                File newFile = new File(newFilePath);

                List<Map> appVersList=service.findList("MAP_APP_VERS.selectAppVersList", params);
                if (appVersList != null && !appVersList.isEmpty()) {
                    Map<String, Object> appversMap = appVersList.get(0);
                    if ("2".equals(appversMap.get("RES_TYPE")) && "1".equals(appversMap.get("RES_UPLOAD_MODE"))) {
                        String originalFilePath = rootPath + appversMap.get("RES_PATH");
                        File originalFile = new File(originalFilePath);
                        try {
                            Map<String, Object> map = CreateUpgradePackService.createUpgradePack(originalFile, newFile);
                            if (map != null) {
                                // 两个资源包完全一样
                                if ("1".equals(map.get("status"))) {
                                    logger.info("版本创建失败，当前上传的资源包和前一个版本的没有变化！");
                                    ok=false;
                                    rst.put("STATUS", "1");
                                    rst.put("MSG", "上传版本成功，当前版本相同！");
                                    return rst;
                                } else if (!"2".equals(map.get("status"))) {
                                    File upgradePackFile = (File)map.get("file");
                                    int rootPathLength = rootPath.length();
                                    String resUpdatePackPath = upgradePackFile.getPath().substring(rootPathLength);
                                    params.put("RES_UPDATE_PACK_PATH", resUpdatePackPath);
                                    params.put("RES_UPDATE_PACK_SIZE", String.valueOf(upgradePackFile.length()));
                                }
                            }
                        } catch (Exception e) {
							e.printStackTrace();
                            logger.error("自动生成升级包异常！", e);
                        }
                    }
                }
            }
            /**********************END**********************/
            Map<String, Object> filesMap = resFiles.get(0);
            params.put("RES_SIZE", filesMap.get("fileSize"));
            params.put("RES_PATH", filesMap.get("fileName"));
            //获取最大的版本号加1
            Map appVersMap =service.load("MAP_APP_VERS.selectMaxAppVers", params);
            appVers= this.addAppVersSeq(String.valueOf(appVersMap.get("APP_VERS")));
            params.put("APP_VERS", appVers);
            params.put("RES_TYPE", resType);
            params.put("APP_IS_UPDATE", "1");//设置强制更新
            //获取最大的版本编号id 加1
            Map appVersIdMap =service.load("MAP_APP_VERS.selectMaxAppVersId", params);
            String appVersId =(String)appVersIdMap.get("APP_VERS_ID");
			if (appVersId != null && appVersId.matches("\\d+")) {
                int intAppVersId=Integer.parseInt((String)appVersIdMap.get("APP_VERS_ID"))+1;
                appVersId=String.valueOf(intAppVersId);
            }
            else{
				appVersId = UUID.randomUUID().toString().replaceAll("-", "");
            }
            params.put("APP_VERS_ID", appVersId);
            //mapAppVersOrgn.setAppVersId(mapAppVers.getAppVersId());
            params.put("APP_AUDIT_STATUS", "2"); //审核通过
			params.put("RES_UPLOAD_MODE", "1"); //本地上传
            Map idxMap =service.load("MAP_APP_VERS.getAppVersIndex", params);
            params.put("APP_VERS_INDEX", idxMap.get("APP_VERS_INDEX"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            params.put("APP_PUBLISH_DATE", dateFormat.format(new Date()));
            params.put("APP_CREATE_DATE", dateFormat.format(new Date()));
            params.put("APP_CREATE_USER", "admin");
            ok=service.insert("MAP_APP_VERS.insertAppVersion", params);
/*              for(String officeId:officeIds){
            	mapAppVersOrgn.setAppOrgn(officeId);
            	mapAppVersDao.insertAppVersOrgn(mapAppVersOrgn);
            }*/
        } catch (Exception e) {
            // 输出错误的关键信息
            ok=false;
            logger.error(ctx.getTransLogBean(transCode), e);
            bizLogger.warn("添加应用版本失败，失败原因:" + e.getMessage(), "151103");
            rst.put("STATUS", "0");
            rst.put("MSG", "上传版本失败");
            return rst;
        }
        //transAfter(ctx, transCode, rst, ok);
        rst.put("STATUS", "1");
        rst.put("APP_VERS", appVers);
        rst.put("MSG", "上传版本成功");
        return rst;
    }
	
	/**
	 * 上传zip包文件
	 * @param files
	 * @return
	 * @throws java.io.IOException
	 */
	
	private List<Map<String, Object>> uploadFiles(List<MultipartFile> files) throws IOException {
		List<Map<String, Object>> fileMsgList = new ArrayList<Map<String, Object>>();
		Map<String, Object> fileMsgMap = null;
		for (MultipartFile file : files) {
			if (file.isEmpty())
				continue;
			String type = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
			CustomFileType fileType = getFileType(type);
			String originalFilename = file.getOriginalFilename();
			if (CustomFileType.ZIP.equals(fileType) || CustomFileType.APK.equals(fileType) ) {
				String fileName = customFileMngService.saveFile(file.getInputStream(), fileType, originalFilename);
				fileMsgMap = new HashMap<String, Object>();
				fileMsgMap.put("fileName", fileName);
				fileMsgMap.put("fileSize", file.getSize());
				fileMsgList.add(fileMsgMap);
				
			}
		}
		return fileMsgList;
	}
	
	public static CustomFileType getFileType(String type){
		CustomFileType fileType = null;
		if(CustomFileType.IMG.getFileTypeList().contains(type)){
			fileType = CustomFileType.valueOf("img".toUpperCase());
		}else if(CustomFileType.ZIP.getFileTypeList().contains(type)){
			fileType = CustomFileType.valueOf("zip".toUpperCase());
		}else if(CustomFileType.APK.getFileTypeList().contains(type)){
			fileType = CustomFileType.valueOf("apk".toUpperCase());
		}else if(CustomFileType.PDF.getFileTypeList().contains(type)){
			fileType = CustomFileType.valueOf("zip".toUpperCase());
		}else if(CustomFileType.IPA.getFileTypeList().contains(type)){
			fileType = CustomFileType.valueOf("ipa".toUpperCase());
		}
		return fileType;
	}

    public String addAppVersSeq(String tempAppVers){

        String appVers="";
        if(tempAppVers==null && tempAppVers!="")
        {
            appVers="1.0";
            return appVers;
        }
        String appVersArr[] = tempAppVers.split("\\.");

        if(appVersArr[appVersArr.length-1].equals("9")  || appVersArr[appVersArr.length-1].equals("99") || appVersArr[appVersArr.length-1].equals("999"))
        {
            int s=Integer.parseInt(appVersArr[0])+1;
            appVers = s+".0";
        }
        else
        {
            int s=Integer.parseInt(appVersArr[appVersArr.length-1])+1;
            appVers=Integer.parseInt(appVersArr[0])+"."+s;
        }
        return appVers;
    }

	private String getValue(Object obj) {
		return null==obj?"":obj.toString();
	}
}