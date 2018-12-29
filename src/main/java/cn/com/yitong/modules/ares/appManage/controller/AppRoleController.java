package cn.com.yitong.modules.ares.appManage.controller;

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
import cn.com.yitong.modules.ares.appManage.service.CreatePlist;
import cn.com.yitong.util.YTLog;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
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
			String osType = ObjectUtils.getDisplayString(params.get("osType"));
			if ("1".equals(osType)
					|| "2".equals(osType)
					||  "3".equals(osType)
					||  "4".equals(osType)) {
				List<Map<String, Object>> roleAppList = service.findList("ROLE_APP.selectRoleAppes", params);
				if (roleAppList != null && !roleAppList.isEmpty()) {
					List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
					for (Map<String, Object> roleAppMap : roleAppList) {
						roleAppMap.put("osType", osType);
						Map<String, Object> appVersMap = this.getAppVersMap(roleAppMap, request);
						if (!appVersMap.isEmpty()) {
							mapList.add(appVersMap);
						}
					}
					rst.put("LIST", mapList);
				}
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

		// 获取应用操作系统
		Map<String, Object> mapApp = service.load("MAP_APP_VERS.selectAppOS", params);
		if (mapApp == null || mapApp.isEmpty()) {
			return rst;
		}
		String appOS = (String)mapApp.get("APP_OS");
		String osType = (String)params.get("osType");
		// 安卓，IOS，web手机，React手机
		if  (("1".equals(osType) && !"1".equals(appOS) 
				|| ("2".equals(osType) && !"2".equals(appOS)))
				&& !"5".equals(appOS)
				&& !"7".equals(appOS)) {
			return rst;
		}
		// 安卓pad，IOS pad， web Pad, React Pad
		if  (("3".equals(osType) && !"3".equals(appOS) 
				|| ("4".equals(osType) && !"4".equals(appOS)))
				&& !"6".equals(appOS)
				&& !"8".equals(appOS)) {
			return rst;
		}
		
		// 查询应用版本列表
		List<Map<String, Object>> appVersList = service.findList("ROLE_APP.queryAppVersList", params);
		if (appVersList == null || appVersList.isEmpty()) {
			return rst;
		}
		Map<String, Object> mapVersion = appVersList.get(0);
		// 设置最新版本
		rst.put("LATEST_VERS", mapVersion.get("APP_VERS"));
		
		// rst.put("APP_ID", mapApp.get("APP_ID"));
		rst.put("APP_ID", mapApp.get("APP_CODE"));
		rst.put("APP_NAME", mapApp.get("APP_NAME"));
		rst.put("ICON_URL", mapApp.get("APP_ICO"));
		rst.put("APP_PACKAGE", mapApp.get("APP_PACKAGE"));
		rst.put("APP_ACTIVITY", mapApp.get("APP_ACTIVITY"));
		rst.put("UPDATE_TIME", mapVersion.get("APP_PUBLISH_DATE"));
		rst.put("COMMENTS", mapApp.get("APP_DESC"));
		
		String appType = null;
		if ("1".equals(appOS)
				|| "2".equals(appOS)
				|| "3".equals(appOS)
				|| "4".equals(appOS)) {
			appType = "1";
		} else if ("5".equals(appOS)
				|| "6".equals(appOS)){
			appType = "2";
		} else if ("7".equals(appOS)
				|| "8".equals(appOS)){
			appType = "3";
		}
		rst.put("APP_TYPE", appType);
		// 安卓或者Web,React
		if ("1".equals(appOS) 
				|| "3".equals(appOS) 
				|| "5".equals(appOS) 
				|| "6".equals(appOS)
				|| "7".equals(appOS)
				|| "8".equals(appOS)) {
			// 本地上传
			if ("1".equals(mapVersion.get("RES_UPLOAD_MODE"))) {
				rst.put("RES_PATH", mapVersion.get("RES_PATH").toString().replaceAll("\\\\", "/"));
				// 镜像地址
			} else {
				rst.put("RES_URL", mapVersion.get("RES_URL"));
			}
			// IOS
		} else if ("2".equals(appOS) || "4".equals(appOS)) {
			// 已经有plist文件
			if (mapVersion.get("PLIST_FILE_PATH") != null) {
				rst.put("PLIST_FILE_PATH", mapVersion.get("PLIST_FILE_PATH").toString().replaceAll("\\\\", "/"));
				// 没有plist文件
			} else {
				String resFileDownLoadUrl = "";
				String plistFilePath = "";
				String contextPath = request.getContextPath();
				// 资源文件为服务器本地文件
				if (mapVersion.get("RES_PATH") != null) {
					String resFileName = mapVersion.get("RES_PATH").toString();
					resFileDownLoadUrl = AppConstants.PRO_RELEASE_HOST + contextPath + "/apple/download/ipa/" + resFileName;
					plistFilePath = AppConstants.upload_files_path + "/ipa/" + resFileName.substring(0, resFileName.lastIndexOf(".")) + ".plist";
					// 资源文件为镜像文件(暂时不考虑)
				} else if (mapVersion.get("RES_URL")  != null) {
					resFileDownLoadUrl = mapVersion.get("RES_URL").toString();
					plistFilePath = AppConstants.upload_files_path + "/ipa/" + this.getRandomString(30) + ".plist";
				}

				Map<String, String> plistParam = new HashMap<String, String>();
				plistParam.put("resFileDownLoadUrl", resFileDownLoadUrl);
				plistParam.put("plistFilePath", plistFilePath);
				plistParam.put("appName", (String)mapApp.get("APP_NAME"));
				plistParam.put("appPackage", (String)mapApp.get("APP_PACKAGE"));
				plistParam.put("appVersion", (String)mapVersion.get("APP_VERS"));
				File plistFile = CreatePlist.createPlist(plistParam);
				
				plistFilePath = AppConstants.PRO_RELEASE_HOST + contextPath + "/apple/download/ipa/" + plistFile.getName();
				
				rst.put("PLIST_FILE_PATH",  plistFilePath);
				Map<String, Object> tempMap = new HashMap<String, Object>();
				tempMap.put("PLIST_FILE_PATH", plistFilePath);
				tempMap.put("APP_VERS_ID", mapVersion.get("APP_VERS_ID"));
				service.update("MAP_APP_VERS.updatePlistFilePath", tempMap);
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