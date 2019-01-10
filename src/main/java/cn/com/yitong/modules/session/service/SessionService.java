package cn.com.yitong.modules.session.service;

import cn.com.yitong.common.utils.StringUtils;
import cn.com.yitong.core.model.SysDict;
import cn.com.yitong.core.util.DictUtils;
import cn.com.yitong.core.util.SecurityUtils;
import cn.com.yitong.core.util.ThreadContext;
import cn.com.yitong.framework.dao.IbatisDao;
import cn.com.yitong.tools.vo.SimpleResult;
import cn.com.yitong.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhuzengpeng<zzp@yitong.com.cn>
 */
@Service
public class SessionService {

	public static final String REQUEST_STATUS = "serverMaintenanceStatus";
	public static final String REQUEST_STATUS_VALUE_FALSE = "0";

	public static final String REQUEST_MSG_CHECK_NO = "0"; //防重发msgId检查-0 : 不检查, 1：检查
	public static final String REQUEST_MSG_CHECK_STATUS = "requestMsgCheckStatus"; //防重发msgId是否检查配置

	public static final int MSGID_SET_TIME = 30; //验证msgId 有效时间长度 30 秒
	public static final int MSGID_SET_LENGTH = 16; //单个msgId长度 16位
	public static final int MSGID_SET_NUM = 16; //保存msgId的个数

	private static final String CLEAN_DICT_URL = "synDict/cleanSynDict.do";

	//SESSION 状态正常
	public static final String SESSION_STATUS_EVENT_OUTOREXCEPT = "910001";
	//无会话登陆超时
	public static final String SESSION_STATUS_EVENT_NOLOGINOUT = "910002";
	//SESSION被强制结束
	public static final String SESSION_STATUS_EVENT_FORCEOVER = "910003";
	//设备锁定导致会话超时
	public static final String SESSION_STATUS_EVENT_DEVICELOCK = "910004";
	//设备被禁用导致会话超时
	public static final String SESSION_STATUS_EVENT_DISABLE = "910005";
	//用户已在其他设备上登陆
	public static final String SESSION_STATUS_EVENT_LOGIN_OTHER = "910006";
	//SESSION 用户被强制下线（被踢）
	public static final String SESSION_STATUS_EVENT_KICKED = "910007";
	//服务器维护中，请稍后再试
	public static final String SESSION_STATUS_EVENT_SERVMAIN = "910008";
	//需要用户绑定设备
	public static final String SESSION_STATUS_EVENT_BOUND = "910009";
	//会话并发控制时，事件并发连接过多
	public static final String SESSION_STATUS_EVENT_BUSY = "910013";
	//通讯序列不正确
	public static final String SESSION_STATUS_EVENT_MSGID = "910015";
	//当前用户绑定设备 达到最大值
	public static final String SESSION_STATUS_EVENT_BOUND_MAX = "910020";
	//当前设备绑定其他用户
	public static final String SESSION_STATUS_EVENT_BOUND_OTHER_USER = "910021";
	//当前设备已经root 不允许登陆
	public static final String SESSION_STATUS_EVENT_DEVICE_ROOT = "910022";

	@Autowired
    @Qualifier(value = "ibatisDao")
    private IbatisDao ibatisDao;

	/**
	 * 请求前验证当前服务状况
	 * @return
	 */
	public SimpleResult preRequestCheck(String url) {
		SimpleResult result = new SimpleResult(true, "正常");
		if(url.contains(CLEAN_DICT_URL)) {
			return result;
		}
		List<SysDict> dictList = DictUtils.getDictionaries(REQUEST_STATUS);
		if(null != dictList && dictList.size() > 0) {
			SysDict dict = dictList.get(0);
			String value = dict.getValue();
			if(REQUEST_STATUS_VALUE_FALSE.equals(value)) {
				result.setSeccess(false);
				result.setResult(SESSION_STATUS_EVENT_SERVMAIN);
				result.setMsg(dict.getDescription());
				return result;
			}
		}
		return result;
	}

	/**
	 * 验证同一个session 消息重发
	 * @param sessionId sessionId
	 * @param msgId 消息Id
	 * @return
	 */
	public SimpleResult preRequestCheckMsgId(String sessionId, String msgId, HttpServletRequest req) {
		SimpleResult result = new SimpleResult(true, "正常");
		if(SecurityUtils.isLogining()) {
			return result;
		}
		if(REQUEST_MSG_CHECK_NO.equals(DictUtils.getDictValue("SYS_PARAM", REQUEST_MSG_CHECK_STATUS, "0"))) {
			return result;
		}
		String userAgent = req.getHeader("user-agent");
		if(StringUtil.isNotEmpty(userAgent) && !userAgent.toLowerCase().contains("iphone") && !userAgent.toLowerCase().contains("android")) {
			return result;
		}
		//设置检查消息Id
		ThreadContext.setIsCheckMsgId(true);
		if(StringUtil.isEmpty(msgId)) {
			result.setSeccess(false);
			result.setResult(SessionService.SESSION_STATUS_EVENT_MSGID);
			result.setMsg("收到消息Id{" + msgId + "}为空，请检查");
			return result;
		}
		int totalLength = MSGID_SET_NUM * MSGID_SET_LENGTH;
		Map<String, Object> sessionMap = getSessionMap(sessionId);
		if(null != sessionMap) {
			//获取已发的msgid串
			String msgidSet = getValue(sessionMap.get("MSGID_SET"));
			if(StringUtil.isEmpty(msgidSet)) {
				return result;
			}
			List<String> msgIdList = StringUtil.fixedLengthIntercept(msgidSet, MSGID_SET_LENGTH);
			if(msgIdList.contains(msgId)) {
				result.setSeccess(false);
				result.setResult(SessionService.SESSION_STATUS_EVENT_MSGID);
				result.setMsg("收到消重复消息{" + msgId + "}，消息已经存在");
				return result;
			}

			long msgTime = Long.valueOf(msgId.substring(3));
			long nowTime = System.currentTimeMillis();
			if((nowTime-msgTime)/1000 > MSGID_SET_TIME) {
				result.setSeccess(false);
				result.setResult(SessionService.SESSION_STATUS_EVENT_MSGID);
				result.setMsg("收到" + MSGID_SET_TIME + "秒之前的消息，直接抛弃");
				return result;
			}
			if(msgidSet.length() < totalLength) {
				return result;
			}else {
				long oldMsgTime = 0;
				for(String oldMsgId : msgIdList) {
					oldMsgTime = Long.valueOf(oldMsgId.substring(3));
					if((nowTime-oldMsgTime)/1000 > MSGID_SET_TIME) {
						msgidSet = msgidSet.substring(MSGID_SET_LENGTH);
					}else {
						break;
					}
				}
				if(msgidSet.length() < totalLength) {
					SecurityUtils.getSessionRequired().setMsgidSet(msgidSet);
					return result;
				}else {
					result.setSeccess(false);
					result.setResult(SessionService.SESSION_STATUS_EVENT_MSGID);
					result.setMsg("{" + MSGID_SET_TIME + "}秒收到消息过多，请稍候再试");
					return result;
				}
			}
		}
		return result;
	}

	/**
	 * 检查当前SESSION 状态
	 * @param sessionId
	 * @return
	 */
	public SimpleResult checkSessionStatus(String sessionId, boolean isUserCheck) {
		SimpleResult result = new SimpleResult(false, "会话超时");
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("SESSION_ID", sessionId);
		Map<String, Object> sessionMap = ibatisDao.queryForMap("ARES_SESSION.querySessionInfoById", paraMap);
		if(null != sessionMap) {
			String userId = getValue(sessionMap.get("USER_ID"));
			if(StringUtils.isEmpty(userId)) {
				if(isUserCheck) {
					result.setResult(SESSION_STATUS_EVENT_FORCEOVER);
					result.setMsg("需要用户认证或者URL不存在");
				}else {
					result.setResult(SESSION_STATUS_EVENT_NOLOGINOUT);
					result.setMsg("登录超时或者URL不存在");
				}
			}else if(StringUtil.isEmpty(getValue(sessionMap.get("ERROR_ID")))){
				result.setResult(SESSION_STATUS_EVENT_FORCEOVER);
				result.setMsg("需要用户认证或者URL不存在");
			}else {
				result.setResult(getValue(sessionMap.get("ERROR_ID")));
				result.setMsg(getValue(sessionMap.get("ERROR_MSG")));
			}
		}else {
			if(isUserCheck) {
				result.setResult(SESSION_STATUS_EVENT_FORCEOVER);
				result.setMsg("需要用户认证或者URL不存在");
			}else {
				result.setResult(SESSION_STATUS_EVENT_OUTOREXCEPT);
				result.setMsg("登录超时或通讯异常");
			}
		}
		return result;
	}

	/**
	 * 根据sessionId查询session信息
	 * @param sessionId
	 * @return
	 */
	private Map<String, Object> getSessionMap(String sessionId) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("SESSION_ID", sessionId);
		return ibatisDao.queryForMap("ARES_SESSION.querySessionInfoById", paraMap);
	}

	private String getValue(Object obj) {
		return null==obj?"":obj.toString();
	}
}
