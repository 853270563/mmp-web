package cn.com.yitong.core.session.service;

import cn.com.yitong.framework.dao.IbatisDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SessionMngService {

	@Autowired
	@Qualifier("ibatisDao")
	private IbatisDao dao;

    /**
     * 根据用户查询当前所有会话数
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
	 */
	public boolean updateSessionKickedUser(String userId) {
		Map<String,Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("USER_ID", userId);
		paramsMap.put("ERROR_ID", SessionService.SESSION_STATUS_EVENT_KICKED);
		paramsMap.put("ERROR_MSG", "当前登陆已被踢");
		return dao.update("ARES_SESSION.updateByUserId", paramsMap);
	}
}
