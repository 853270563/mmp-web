package cn.com.yitong.framework.service.impl;

import cn.com.yitong.framework.dao.IbatisDao;
import cn.com.yitong.modules.session.service.SessionService;
import cn.com.yitong.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class SessionMngService {

	@Autowired
	@Qualifier("ibatisDao")
	private IbatisDao dao;

	/**
	 * 检查单点登录
	 * @param userId
	 * @param request
	 * @param forceLoginFlag
	 * @return
	 */
	public boolean checkSigleLogin(HttpServletRequest request,String userId,String forceLoginFlag) {
		List<Map<String, Object>> rstList = onLineWithUserId(userId);
		if(rstList != null && rstList.size()>0){//说明当前用户已经在登录
			for(Map map : rstList){
				if(map.get("DEVICE_ID") != null && map.get("DEVICE_ID").equals(request.getParameter("DEVICE_ID"))){//检查当前设备ID是否在登录
					this.singleForceLogin(userId, request);//直接强制登陆（互踢）
					return true;
				}
			}
			if(null != forceLoginFlag){
				this.singleForceLogin(userId, request);//强制登陆（互踢）
			}else {
				return false;
			}
		}
		this.singleNormalLogin(userId, request);//正常登陆（互踢）
		return true;
	}
	
    /**
	 * 正常登陆（单点控制）
	 * 
	 * @param userId
	 * @param request
	 * @return
	 */
	public boolean singleNormalLogin(String userId, HttpServletRequest request) {
		boolean saveRst = saveCurrentUser(userId, request);
		if(saveRst){
			return true;
		}
		return false;
	}
	
	/**
	 * 强制登陆（单点控制）
	 * 
	 * 1、先检查表中是否已经有记录。
	 * 2、如果表中有记录则将其AUTH_STATUS第一位设置为0，然后将自己的信息插入表中。
	 * 3、如果表中没有记录，则直接将自己的信息插入表中。
	 * @param userId
	 * @param request
	 * @return
	 */
	public boolean singleForceLogin(String userId, HttpServletRequest request){
		boolean updateRst = updateOtherUser(userId, request);
		boolean saveRst = saveCurrentUser(userId, request);
		if(updateRst && saveRst){
			return true;
		}
		return false;
	}
	
	/**
	 * 检查用户在线的状态
	 * 
	 * 0：被踢，1：正常在线，2:正常退出
	 * @param request
	 * @return
	 */
	public int onLineStatusCheck(HttpServletRequest request){
		String sessionId = request.getSession().getId();
		Map<String,Object> paramsMap = new HashMap<String,Object>();
		paramsMap.put("SESSION_ID", sessionId);
		paramsMap.put("DEVICE_ID", request.getParameter("deviceId"));
		Map<String,Object> map = dao.load("ARES_SESSION.load", paramsMap);
		if(map != null && map.size()>0){//表中有记录，则说明此用户0：被踢，1：正常在线
			String onLineStatus = map.get("AUTH_STATUS").toString().substring(0, 1);//AUTH_STATUS字段第一位代表在线的状态
			if("0".equals(onLineStatus) ){
				return 0;//0：被踢
			}else if("1".equals(onLineStatus)){
				return 1;//1：正常在线
			}else {
				return 2;//2:正常退出
			}
		}
		return 2;//2:正常退出
	}
	
	private boolean updateOtherUser(String userId, HttpServletRequest request){
		boolean rst = false;
		String sessionId = request.getSession().getId();
		List<Map<String, Object>> rstList = onLineWithUserId(userId);
		if(rstList != null && rstList.size()>0){
			for(Map map : rstList){
				if(sessionId != map.get("SESSION_ID")){
					String authStatus = map.get("AUTH_STATUS").toString();
					authStatus = "0"+authStatus.substring(1);
					map.put("AUTH_STATUS", authStatus);
					rst = dao.update("ARES_SESSION.update", map);
				}
			}
			
		}
		return rst;
	}
	
	private List onLineWithUserId(String userId){
		Map<String,Object> paramsMap = new HashMap<String,Object>();
		paramsMap.put("USER_ID", userId);
		paramsMap.put("AUTH_STATUS", "1");
		List rstList = dao.findList("ARES_SESSION.findList", paramsMap);
		return rstList;
	}
	
	private boolean saveCurrentUser(String userId, HttpServletRequest request){
		String serverIp = request.getLocalAddr();
		String sessionId = request.getSession().getId();
		Map<String,Object> paramsMap = new HashMap<String,Object>();
		paramsMap.put("SESSION_ID", sessionId);
		paramsMap.put("USER_ID", userId);
		paramsMap.put("CREATE_TIME", new  Date());
		paramsMap.put("DEVICE_ID", request.getParameter("deviceId"));
		paramsMap.put("SERVER_IP", serverIp);
		paramsMap.put("AUTH_STATUS", "1");
		Object rstObject = dao.insert("ARES_SESSION.insert", paramsMap);
		if(rstObject == null){
			return true;
		}
		return false;
	}
	
	/**
	 * 删除用户的会话记录
	 * @param sessionId
	 * @return
	 */
	public boolean deleteCurrentSession(String sessionId){
		Map<String,Object> paramsMap = new HashMap<String,Object>();
		paramsMap = new HashMap<String,Object>();
		paramsMap.put("SESSION_ID", sessionId);
		boolean rstObject = dao.delete("ARES_SESSION.delete", paramsMap);
		return rstObject;
	}

    /**
     * 根据用户查询当前所有会话数
     * @param userId
     * @return
     */
    public int getSessionCntByUser(String userId){
        Map<String,Object> paramsMap = new HashMap<String,Object>();
        paramsMap.put("USER_ID", userId);
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        paramsMap.put("SYSTIME", dateString);
        List<Map<String,Object>> sessionList = dao.findList("ARES_SESSION.queryOnlineSessionCntByUser", paramsMap);
        return null==sessionList?0:sessionList.size();
    }

    /**
     * 根据用户和设备号查询当前所有会话数
     * @param userId
     * @param deviceId
     * @return
     */
    public int getSessionCntByUserAndDeviceId(String userId ,String deviceId){
        Map<String,Object> paramsMap = new HashMap<String,Object>();
        paramsMap.put("USER_ID", userId);
        paramsMap.put("DEVICE_ID", deviceId);
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        paramsMap.put("SYSTIME", dateString);
        Map<String,Object> sessionMap = dao.load("ARES_SESSION.querySessionCnt", paramsMap);
        Integer sessionCnt=Integer.parseInt(sessionMap.get("CNT").toString());
        return null==sessionCnt?0:sessionCnt;
    }


    /**
     * 获取当前总会话数
     * @return
     */
    public int getOnlineSessionCnt(){
        Map<String,Object> paramsMap = new HashMap<String,Object>();

        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        paramsMap.put("SYSTIME", dateString);
        Map<String,Object> sessionMap = dao.load("ARES_SESSION.queryOnlineSessionCnt", paramsMap);
        Integer onlineSessionCnt=Integer.parseInt(sessionMap.get("CNT").toString());
        return null==onlineSessionCnt?0:onlineSessionCnt;
    }

    public boolean updateSessionByDeviceId(String userId,String deviceId){
        boolean rst = false;
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("USER_ID",userId);
        params.put("DEVICE_ID", deviceId);
        rst = dao.update("ARES_SESSION.updateByDeviceId", params);
        return rst;
    }

    public boolean updateSessionByUserId(String userId){
        boolean rst = false;
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("USER_ID",userId);
        rst = dao.update("ARES_SESSION.updateByUserId", params);
        return rst;
    }

	/**
	 * 查询出当前用户的session
	 * @param userId
	 * @return
	 */
	public boolean updateSessionKickedUser(String userId) {
		Map<String,Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("USER_ID", userId);
		paramsMap.put("ERROR_ID", SessionService.SESSION_STATUS_EVENT_KICKED);
		paramsMap.put("ERROR_MSG", "当前登陆已被踢");
		return dao.update("ARES_SESSION.updateByUserId", paramsMap);
	}
}
