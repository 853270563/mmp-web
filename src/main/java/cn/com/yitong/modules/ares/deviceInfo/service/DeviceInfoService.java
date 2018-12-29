package cn.com.yitong.modules.ares.deviceInfo.service;

import cn.com.yitong.consts.NS;
import cn.com.yitong.core.util.DictUtils;
import cn.com.yitong.framework.core.vo.DeviceCkeck;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.core.session.service.SessionService;
import cn.com.yitong.util.DateUtil;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author winkie 2015/4/3
 */
@Service
public class DeviceInfoService {
	
	private Logger logger = YTLog.getLogger(this.getClass());
	/**
	 * 系统配置之用户最大绑定设备数配置
	 */
	private static final Long MAX_USER_BOUND_DEVICE_NUM = Long.valueOf(DictUtils.getDictValue("SYS_PARAM", NS.DICT_SYS_PARAMS_MAX_USER_BOUND_DEVICE_NUM,"0"));
	private static final String DEVICE_CHECK_YES = "1";  // 1:检查  0：不检查
	private static final String DEVICE_ROOT_CHECK_YES = "1"; // 1:检查  0：不检查

	@Autowired
	ICrudService service;

	/**
	 * 验证当前设备是否需要擦除 True 需要， False 不需要
	 * @param param 设备UUID 若参数为空，或者查询不到相应的设备，默认无需擦除
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map isNeedEraseAndLock(Map param) {
		if(!isNeedDeviceCheck()) {
			return returnMap(param, Boolean.FALSE, Boolean.FALSE);
		}
		String deviceUuid = null==param.get("DEVICE_UUID")?"":param.get("DEVICE_UUID").toString();
		if(StringUtil.isEmpty(deviceUuid)) {
			if(logger.isInfoEnabled()) {
				logger.info("设备擦除：设备UUID为空");
			}
			return returnMap(param, Boolean.FALSE, Boolean.FALSE);
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("deviceUuid", deviceUuid);
		Map<String, Object> deviceInfo = service.load("DEVICE_INFO.queryByUuid", paramMap);
		if(null == deviceInfo) {
			if(logger.isInfoEnabled()) {
				logger.info("设备擦除：查询设备为空");
			}
			return returnMap(param, Boolean.TRUE, Boolean.FALSE);
		}
		String eraseFalg = null==deviceInfo.get("ERASE_FLAG")?"":deviceInfo.get("ERASE_FLAG").toString();
		String lockFalg = null==deviceInfo.get("LOCK_FLAG")?"":deviceInfo.get("LOCK_FLAG").toString();
		Boolean easeSign = Boolean.FALSE;
		Boolean lockSign = Boolean.FALSE;
		if(StringUtil.isNotEmpty(eraseFalg) && NS.DEVICE_ERASE_STATUS_YES.equals(eraseFalg)) {
			if(logger.isInfoEnabled()) {
				logger.info("设备擦除：设置擦除标识");
			}
			easeSign = Boolean.TRUE;
		}
		if(StringUtil.isNotEmpty(lockFalg) && NS.DEVICE_LOCK_STATUS_YES.equals(lockFalg)) {
			if(logger.isInfoEnabled()) {
				logger.info("设备擦除：设置锁定标识");
			}
			lockSign = Boolean.TRUE;
		}
		return returnMap(param, easeSign, lockSign);
	}
	
	/**
	 * 设备擦除数据结果反馈
	 * @param deviceUuid 设备UUID
	 * @param result 0： 擦除失败  1：擦除成功
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Boolean eraseDeviceResultReturn(String deviceUuid, String result) {
		if(StringUtil.isEmpty(deviceUuid) || StringUtil.isEmpty(result)) {
			if(logger.isInfoEnabled()) {
				logger.info("设备UUID或者擦除结果为空");
			}
			return Boolean.FALSE;
		}
		if(NS.DEVICE_ERASE_RESULT_SUCCESS.equals(result)) {
			if(logger.isInfoEnabled()) {
				logger.info("擦除数据成功，去除擦除标识");
			}
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("deviceUuid", deviceUuid);
			Map<String, Object> deviceInfo = service.load("DEVICE_INFO.queryByUuid", paramMap);
			if(null == deviceInfo) {
				return Boolean.TRUE;
			}
			deviceInfo.put("ERASE_FLAG", NS.DEVICE_ERASE_STATUS_NO);
			service.update("DEVICE_INFO.updateEraseById", deviceInfo);
		}else {
			if(logger.isInfoEnabled()) {
				logger.info("擦除数据失败，不做操作");
			}
		}
		return Boolean.TRUE;
	}
	
	/**
	 * 设备解绑
	 * @param deviceUuid 设备UUID
	 * @return
	 */
	public Boolean undoBoundDevice(String deviceUuid) {
		if(StringUtil.isEmpty(deviceUuid)) {
			if(logger.isInfoEnabled()) {
				logger.info("设备解绑：设备UUID为空");
			}
			return Boolean.FALSE;
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("deviceUuid", deviceUuid);
		service.update("DEVICE_INFO.undoBoundDevice", paramMap);
		return Boolean.TRUE;
	}
	
	/**
	 * 设备绑定
	 * @param params
	 */
	@SuppressWarnings("unchecked")
	public Boolean boundDevice(Map params, Map rst) {
		//校验参数是否为空，为空 返回失败
		String deviceUuid = (String)params.get("DEVICE_UUID");
		String crStaff = (String)params.get("CR_STAFF");
		if(StringUtil.isEmpty(deviceUuid)) {
			if(logger.isInfoEnabled()) {
				logger.info("设备绑定：设备UUID为空");
			}
			rst.put("MSG", "绑定参数为空");
			return Boolean.FALSE;
		}
		
		//校验当前设备和用户是否已经绑定
		Map paramMap = new HashMap();
		paramMap.put("userId", crStaff);
		paramMap.put("deviceUuid", deviceUuid);
		Map cntMap = service.load("DEVICE_INFO.countByUuidAndUser", paramMap);
		if(null != cntMap && cntMap.size() > 0) {
			if(logger.isInfoEnabled()) {
				logger.info("设备绑定：当前设备和用户已绑定，无需绑定");
			}
			return Boolean.TRUE;
		}
		
		//校验当前用户是否绑定设备最大值
		Map<String, Object> userMap = new HashMap<String, Object>();
		userMap.put("userId", crStaff);
		Map<String, Object> cntUserBoundDevice = service.load("DEVICE_INFO.cntUserBoundDevice", userMap);
		Object cntStr = cntUserBoundDevice.get("NUM");
		Long cnt = Long.valueOf(cntStr.toString());
		if(cnt >= MAX_USER_BOUND_DEVICE_NUM) {
			rst.put("MSG", "当前用户绑定设备已达到最大值");
			return Boolean.FALSE;
		}
		
		//注册设备
		Map<String, Object> deviceMap = service.load("DEVICE_INFO.queryByUuid", paramMap);
		params.put("UPDATE_TIME", new Date());
		if(null == deviceMap || deviceMap.size() == 0) {
			if(logger.isInfoEnabled()) {
				logger.info("设备注册：当前设备没有入库，创建设备信息");
			}
			params.put("LOCK_FLAG", NS.DEVICE_LOCK_STATUS_NO);
			params.put("ERASE_FLAG", NS.DEVICE_ERASE_STATUS_NO);
			params.put("LOSE_FLAG", NS.DEVICE_LOSE_STATUS_NO);
			params.put("DEVICE_STATUS", "2");
			params.put("REMARK", "设备自助注册");
			service.insert("DEVICE_INFO.registerDevice", params);
		}else {
			if(logger.isInfoEnabled()) {
				logger.info("设备注册：当前设备已入库，更新设备信息");
			}
			params.put("REMARK", "更新设备信息");
			service.update("DEVICE_INFO.updateByIdSelective", params);
		}
		
		if(logger.isInfoEnabled()) {
			logger.info("设备绑定：设备用户绑定");
		}
		Map boundMap = new HashMap();
		boundMap.put("DEVICE_UUID", deviceUuid);
		boundMap.put("USER_ID", crStaff);
		boundMap.put("REGISTER_TIME", new Date());
		service.insert("DEVICE_INFO.boundDevice", boundMap);
		return Boolean.TRUE;
	}
	
	@SuppressWarnings("unchecked")
	private Map returnMap(Map param, Boolean eraseFlag, Boolean lcokFlag) {
		if(eraseFlag) {
			param.put("ERASE_SIGN", NS.DEVICE_ERASE_STATUS_YES);
		}else {
			param.put("ERASE_SIGN", NS.DEVICE_ERASE_STATUS_NO);
		}
		if(lcokFlag) {
			param.put("LOCK_SIGN", NS.DEVICE_LOCK_STATUS_YES);
		}else {
			param.put("LOCK_SIGN", NS.DEVICE_LOCK_STATUS_NO);
		}
		return param;
	}
	
	/**
	 * 用于登陆验证设备是否允许登陆
	 * @param deviceUuid 设备UUID
	 * @param userId 用户ID
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public DeviceCkeck loginDeviceCheck(String deviceUuid, String userId) {
		DeviceCkeck checkResult = new DeviceCkeck();
		if(!isNeedDeviceCheck()) {
			return checkResult;
		}
		if(StringUtil.isEmpty(userId)) {
			checkResult.setResult(NS.DEVICE_CHECK_RESULT_FAIL);
			checkResult.setIsSuccess(false);
			checkResult.setMessage("用户userId为空，请检查");
			return checkResult;
		}
		if(StringUtil.isEmpty(deviceUuid)) {
			checkResult.setEraseSign(NS.DEVICE_ERASE_STATUS_YES);
			return registerCheck(userId, checkResult);
		}
		//判断设备是否存在
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("deviceUuid", deviceUuid);
		Map<String, Object> deviceInfo = service.load("DEVICE_INFO.queryByUuid", paramMap);
		if(null == deviceInfo || 0 == deviceInfo.size()) {
			checkResult.setEraseSign(NS.DEVICE_ERASE_STATUS_YES);
			return registerCheck(userId, checkResult);
		}
		//判断是否需要擦除
		String eraseFlag = (String)deviceInfo.get("ERASE_FLAG");
		if(StringUtil.isNotEmpty(eraseFlag) && NS.DEVICE_ERASE_STATUS_YES.equals(eraseFlag)) {
			checkResult.setEraseSign(NS.DEVICE_ERASE_STATUS_YES);
		}
		//判断是否需要锁定
		String lockFlag = (String)deviceInfo.get("LOCK_FLAG");
		if(StringUtil.isNotEmpty(lockFlag) && NS.DEVICE_LOCK_STATUS_YES.equals(lockFlag)) {
			checkResult.setLockSign(NS.DEVICE_LOCK_STATUS_YES);
			checkResult.setResult(SessionService.SESSION_STATUS_EVENT_DEVICELOCK);
			checkResult.setMessage("当前设备已锁定，请联系管理员处理");
			checkResult.setIsSuccess(false);
			return checkResult;
		}
		if(isNeedCheckDeviceRoot()) {
			//需要验证设备root权限 ，root设备不允许登陆
			String isRoot = (String)deviceInfo.get("IS_ROOT");
			if(StringUtil.isNotEmpty(isRoot) && NS.DEVICE_IS_ROOT_YES.equals(isRoot)) {
				checkResult.setResult(SessionService.SESSION_STATUS_EVENT_DEVICE_ROOT);
				checkResult.setIsSuccess(false);
				checkResult.setMessage("ROOT过的设备不允许登陆");
				return checkResult;
			}
		}
		//校验绑定关系是否存在
		List<Map<String, Object>> rsDeviceUserMaps = service.findList("DEVICE_INFO.queryRelationByDeviceUuid", paramMap);
		if(null == rsDeviceUserMaps || 0 == rsDeviceUserMaps.size()) {
			return registerCheck(userId, checkResult);
		}else if(1 == rsDeviceUserMaps.size()) {
			Map<String, Object> rsMap = rsDeviceUserMaps.get(0);
			String boundUserId = (String)rsMap.get("USER_ID");
			if(StringUtil.isEmpty(boundUserId)) {
				return registerCheck(userId, checkResult);
			}
			if(boundUserId.equals(userId)) {
				checkResult.setMessage("设备登陆验证成功");
				return checkResult;
			}else {
				checkResult.setIsSuccess(false);
				checkResult.setResult(SessionService.SESSION_STATUS_EVENT_BOUND_OTHER_USER);
				checkResult.setMessage("当前设备已经绑定其他用户，请联系管理员处理");
				return checkResult;
			}
		}else {
			String boundUserId = null;
			for(Map<String, Object> map : rsDeviceUserMaps) {
				boundUserId = (String)map.get("USER_ID");
				if(StringUtil.isEmpty(boundUserId)) {
					continue;
				}
				if(boundUserId.equals(userId)) {
					break;
				}
			}
			if(StringUtil.isEmpty(boundUserId)) {
				return registerCheck(userId, checkResult);
			}else if(boundUserId.equals(userId)) {
				checkResult.setMessage("设备登陆验证成功");
				return checkResult;
			}else {
				checkResult.setIsSuccess(false);
				checkResult.setResult(SessionService.SESSION_STATUS_EVENT_BOUND_OTHER_USER);
				checkResult.setMessage("当前设备已经绑定其他用户，请联系管理员处理");
				return checkResult;
			}
		}
	}
	
	/**
	 * 无设备 或者无绑定关系时 注册检查
	 * @param userId 用户ID
	 * @param checkResult 返回结果
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public DeviceCkeck registerCheck(String userId, DeviceCkeck checkResult) {
		checkResult.setIsSuccess(false);
		Map<String, Object> userMap = new HashMap<String, Object>();
		userMap.put("userId", userId);
		Map<String, Object> cntUserBoundDevice = service.load("DEVICE_INFO.cntUserBoundDevice", userMap);
		Object cntStr = cntUserBoundDevice.get("NUM");
		Long cnt = Long.valueOf(cntStr.toString());
		if(cnt < MAX_USER_BOUND_DEVICE_NUM) {
			checkResult.setResult(SessionService.SESSION_STATUS_EVENT_BOUND);
			checkResult.setMessage("允许当前设备和用户绑定");
		}else {
			checkResult.setResult(SessionService.SESSION_STATUS_EVENT_BOUND_MAX);
			checkResult.setMessage("当前用户绑定设备达到最大值，请联系管理员处理");
		}
		return checkResult;
	}

	/**
	 * 设备设备权限登入
	 * @param deviceUUID
	 * @param map
	 * @param isAdd 是否首次登入
	 */
	public void insertDeviceAuth(String deviceUUID,Map map,final boolean isAdd) {
		Map<String,Object> params = map;
		params.put("DEVICE_UUID",deviceUUID);
		params.put("AUTH_TYPE","0");
		params.put("UPDATE_TIME", DateUtil.todayStr("yyyy-MM-dd HH:mm:ss"));
		if (isAdd){
			params.put("DEVICE_INFO",params.get("CLI_DEVICE_INFO"));
			service.insert("DEVICE_INFO.insertDeviceAuth",params);
		}else {
			service.update("DEVICE_INFO.updateDeviceAuth",params);
		}

	}

	/**
	 * 查询设备权限登入
	 * @param deviceUUID
	 * @param appId
	 */
	public String selectDeviceAuth(String deviceUUID,String appId) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("DEVICE_UUID",deviceUUID);
		params.put("APP_ID",appId);
		Map<String,Object> map = service.load("DEVICE_INFO.selectDeviceAuth",params);
		if (map == null || map.isEmpty()){
			return "-1";//无数据
		}
		return (String)map.get("AUTH_TYPE");
	}

	/**
	 * 检查是否需要进行设备检查
	 * @return
	 */
	private Boolean isNeedDeviceCheck() {
		return DEVICE_CHECK_YES.equals(DictUtils.getDictValue("SYS_PARAM", NS.DICT_SYS_PARAMS_DEVICE_CHECK, "0"));
	}

	/**
	 * 检查 是否需要进行 设备root检查 默认不需要检查
	 * @return
	 */
	private Boolean isNeedCheckDeviceRoot() {
		return DEVICE_ROOT_CHECK_YES.equals(DictUtils.getDictValue("SYS_PARAM", NS.DICT_SYS_PARAMS_DEVICE_ROOT_CHECK, "0"));
	}
}
