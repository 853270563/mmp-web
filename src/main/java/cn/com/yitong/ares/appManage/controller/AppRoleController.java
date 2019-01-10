package cn.com.yitong.ares.appManage.controller;

import cn.com.yitong.ares.appManage.service.CreatePlist;
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
import cn.com.yitong.ares.login.BizLogger;
import cn.com.yitong.util.YTLog;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

@Controller
public class AppRoleController extends BaseControl {

	private Logger logger = YTLog.getLogger(this.getClass());
	@Autowired
	ICrudService service;
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
	
	final BizLogger bizLogger = BizLogger.getLogger(this.getClass());
	
	/**
	 * 获取原生资源更新信息
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("ares/appManage/roleApp.do")
	@ResponseBody
	public Map<String, Object> roleApp(HttpServletRequest request) {
		String transCode = BASE_PATH + "roleApp";
		Map<String, Object> rst = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}
		// 数据库操作区
		Map<String, Object> params = ctx.getParamMap();
		boolean ok = false;
		try {
			List<Map<String, Object>> roleAppList = service.findList("ROLE_APP.selectRoleAppes", params);
			if (roleAppList != null && !roleAppList.isEmpty()) {
				List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
				for (Map<String, Object> roleAppMap : roleAppList) {
					Map<String, Object> appVersMap = this.getAppVersMap(roleAppMap, request);
					if (!appVersMap.isEmpty() && params.get("osType").toString().equals(appVersMap.get("appOS").toString())) {
						mapList.add(appVersMap);
					}
				}
				rst.put("LIST", mapList);
			}
			ok = true;
		} catch (Exception e) {
			// 输出错误的关键信息
			ok=false;
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}
	
	/**
	 * 查询应用版本详情
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> getAppVersMap(Map<String, Object> params, HttpServletRequest request) throws Exception {
		Map<String, Object> rst = new HashMap<String, Object>();
		// 查询当前版本序列
		Map<String, Object> appVersIndexMap = service.load("MAP_APP_VERS.loadEarliestAppVers", params);
		// 如果应用版本不存在的话
		if (appVersIndexMap == null || appVersIndexMap.isEmpty()) {
			return rst;
		}
		// 查询所有大于当前版本序列的应用版本
		params.putAll(appVersIndexMap);
		List<Map<String, Object>> appVersList = service.findList("ROLE_APP.queryAppVersList", params);
		if (appVersList == null || appVersList.isEmpty()) {
			return rst;
		}
		for (Map<String, Object> map : appVersList) {
			// 原生资源
			if ("1".equals(map.get("RES_TYPE").toString())) {
				// 设置最新版本
				if (rst.get("LATEST_VERS") == null) {
					rst.put("LATEST_VERS", map.get("APP_VERS"));
					// 获取应用操作系统
					Map<String, Object> mapApp = service.load("MAP_APP_VERS.selectAppOS", params);
					
					rst.put("APP_ID", mapApp.get("APP_ID"));
					rst.put("APP_NAME", mapApp.get("APP_NAME"));
					rst.put("ICON_URL", mapApp.get("APP_ICO"));
					rst.put("APP_PACKAGE", mapApp.get("APP_PACKAGE"));
					rst.put("UPDATE_TIME", map.get("APP_PUBLISH_DATE"));
					rst.put("COMMENTS", mapApp.get("APP_DESC"));
					
					String appOS = mapApp.get("APP_OS").toString();
					rst.put("appOS", appOS);
					// 安卓
					if ("1".equals(appOS) || "3".equals(appOS)) {
						// 本地上传
						if ("1".equals(map.get("RES_UPLOAD_MODE"))) {
							rst.put("RES_PATH", map.get("RES_PATH").toString().replaceAll("\\\\", "/"));
							// 镜像地址
						} else {
							rst.put("RES_URL", map.get("RES_URL"));
						}
						// IOS
					} else if ("2".equals(appOS) || "4".equals(appOS)) {
						// 已经有plist文件
						if (map.get("PLIST_FILE_PATH") != null) {
							rst.put("PLIST_FILE_PATH", map.get("PLIST_FILE_PATH").toString().replaceAll("\\\\", "/"));
							// 没有plist文件
						} else {
							String resFileDownLoadUrl = "";
							String plistFilePath = "";
							// 资源文件为服务器本地文件
							if (map.get("RES_PATH") != null) {
								String resFileName = map.get("RES_PATH").toString();
								String contextPath = request.getContextPath();
								resFileDownLoadUrl = AppConstants.PRO_RELEASE_HOST + contextPath + "/ares/appManage/fileDownLoad.do?RES_PATH=" + resFileName;
								plistFilePath = AppConstants.upload_files_path +  "/ipa/" + resFileName.substring(0, resFileName.lastIndexOf(".")) + ".plist";
							} else if (map.get("RES_URL")  != null) {
								resFileDownLoadUrl = map.get("RES_URL").toString();
								plistFilePath = AppConstants.upload_files_path + "/ipa/" + this.getRandomString(30) + ".plist";
							}
							File plistFile;
							plistFile = CreatePlist.createPlist(resFileDownLoadUrl, plistFilePath);
							rst.put("PLIST_FILE_PATH",  plistFile.getName());
							Map<String, Object> tempMap = new HashMap<String, Object>();
							tempMap.put("PLIST_FILE_PATH", plistFile.getName());
							tempMap.put("APP_VERS_ID", map.get("APP_VERS_ID"));
							service.update("MAP_APP_VERS.updatePlistFilePath", tempMap);
						}
					}
					break;
				}
			}
		}
		return rst;
	}

	/**
	 * 生成随机字符串
	 * 
	 * @param length 生成字符串的长度  
	 * @return
	 */
	private String getRandomString(int length) { 
	    String base = "abcdefghijklmnopqrstuvwxyz0123456789";     
	    Random random = new Random();     
	    StringBuffer sb = new StringBuffer();     
	    for (int i = 0; i < length; i++) {     
	        int number = random.nextInt(base.length());     
	        sb.append(base.charAt(number));     
	    }
	    return sb.toString();     
	 }
	
	/**
	 * 事务前置处理
	 * @param ctx
	 * @param transCode
	 * @param rst
	 * @return
	 */
	private boolean transPrev(IBusinessContext ctx, String transCode, Map<String, Object> rst) {
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
	private void transAfter(IBusinessContext ctx, String transCode, Map<String, Object> rst,
			boolean ok) {
		CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
	}
}