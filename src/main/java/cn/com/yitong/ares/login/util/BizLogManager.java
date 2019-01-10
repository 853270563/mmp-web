package cn.com.yitong.ares.login.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import cn.com.yitong.common.utils.ConfigUtils;
import cn.com.yitong.common.utils.JsonUtils;
import cn.com.yitong.common.utils.SpringContextUtils;
import cn.com.yitong.core.session.Session;
import cn.com.yitong.core.util.ConfigName;
import cn.com.yitong.core.util.SecurityUtils;
import cn.com.yitong.framework.service.ICrudService;

/**
 * 日志管理类，提供缓存日志，日志上传等功能的实现
 *
 * @author hry@yitong.com.cn
 * @date 2015年1月10日
 */
public class BizLogManager {

	private Logger logger = LoggerFactory.getLogger(BizLogManager.class);
	/*缓存未提交的日子数量*/
	private int currentCacheLogSize = 0;
	/*缓存最大数量*/
	private final int MAX_CACHE_LOG_SIZE = 1;
	/*日志文件是否保存至文件*/
	private final boolean CACHE_LOG_FILE = false;
	//用户保存临时日志信息
	private final List<Map<String, String>> logList = new ArrayList<Map<String, String>>();

	private final String SAVE_BIZLOG_TYPE = "saveBizLogType";
	//直接保存还是URL调用服务保存
	private final int SAVE_LOG_TYPE = 1; //直接保存到数据库

	@Autowired
	private ICrudService service;

	/**
	 * @param loglevel 日志基本（一般、重要、重大）
	 * @param callingClass 调用日志的Class
	 * @param apptag   应用标签
	 * @author hry@yitong.com.cn
	 * @date 2015年1月13日
	 */
	public void save(String loglevel, String logData, String callingClass, String apptag, String eventId) {
		Session session = SecurityUtils.getSession();
		if(validate(logData, session)){
			//事件保存到session
			saveEventToSession(eventId, session);
			//创建日志
			BizLogInfo logInfo = BizLogUtil.createLogInfo(loglevel, logData, callingClass, apptag, session);

			if(StringUtils.isEmpty(logInfo.getMsg_id()) || StringUtils.isEmpty(logInfo.getDevice_id())){
				//logger.error("保存日志失败：消息ID或者设备ID值为空");
//				return;
			}
			//保存日志
			saveBizLogInfo(logInfo);
			logger.info("保存到日志到数据库成功：{}", logData);
		}
	}
	private void saveBizLogInfo(BizLogInfo logInfo) {
		if(CACHE_LOG_FILE){
			saveToFile(logInfo);
		}else{
			saveToList(logInfo);
		}
	}

	private void saveEventToSession(String eventId, Session session) {
		if(!StringUtils.isEmpty(eventId)){
			session.setEventId(eventId);
		}
	}

	private boolean validate(String logData, Session session) {
		if(session == null){
			logger.warn("Session为null，无法创建日志！");
			return false;
		}
		if (StringUtils.isEmpty(logData)) {
			logger.warn("日志数据为空，创建日志失败！");
			return false;
		}
		return true;
	}

	/**
	 * 保存日志信息到List
	 * @author hry@yitong.com.cn
	 * @date 2015年1月13日
	 */
	private void saveToList(BizLogInfo logInfo) {
		int saveLogType = ConfigUtils.getValue(SAVE_BIZLOG_TYPE, 0);
		if(SAVE_LOG_TYPE == saveLogType) {
			service.insert("LOG.insert", BizLogUtil.logConverToMap(logInfo));
		}else {
			synchronized (logList) {
				logList.add(log2Map(logInfo));
				if (MAX_CACHE_LOG_SIZE <= logList.size()) {
					String data = listToJsonStr(logList.toArray());
					//通过Http post请求提交到Log服务器
					logger.debug("logData:" + data);
					String url = ConfigUtils.getValue(ConfigName.BIZ_LOG_BATCH_SERVICE_URL);
					logger.debug("logServerUrl:" + url);
					boolean isSuccessed = BizLogUtil.postToServer(data, url);
					logger.debug("isSuccessed:" + isSuccessed);
					//清空日志缓存
					if (isSuccessed) {
						logList.clear();
					}
				}
			}
		}
	}

	/**
	 * 保存日志信息到缓存文件
	 * @author hry@yitong.com.cn
	 * @date 2015年1月13日
	 */
	private void saveToFile(BizLogInfo logInfo) {
		try {
			BizLogFileUtil.writeLog(JsonUtils.objectToJson(logInfo));
			currentCacheLogSize++;
		} catch (IOException e) {
			logger.error("日志保存至临时文件失败，日志信息直接上传至日志服务器",e);
			BizLogUtil.postToServer(listToJsonStr(logInfo), ConfigUtils.getValue(ConfigName.BIZ_LOG_BATCH_SERVICE_URL));
		}
		//缓存达到上限，批量上传缓存日志
		if(MAX_CACHE_LOG_SIZE <= currentCacheLogSize){
			//读取日志文件内容
			List<String> logStrList = null;
			try {
				logStrList = BizLogFileUtil.readLog();
			} catch (IOException e) {
				logger.error("读取日志文件异常，上传日志服务器失败",e);
				return ;
			}
			String data = listToJsonStr(logStrList);
			//通过Http post请求提交到Log服务器
			boolean isSuccessed = BizLogUtil.postToServer(data, ConfigUtils.getValue(ConfigName.BIZ_LOG_BATCH_SERVICE_URL));
			//清空日志缓存
			if(isSuccessed){
				if(BizLogFileUtil.clearLog()) currentCacheLogSize = 0;
			}
		}
	}

	private Map<String, String> log2Map(BizLogInfo logInfo) {
		return logInfo.toMap();
	}

	private String listToJsonStr(Object... logList) {
		return JsonUtils.objectToJson(logList);
	}

	public static BizLogManager getInstance() throws BeansException {
		return SpringContextUtils.getBean(BizLogManager.class);
	}
}
