package cn.com.yitong.modules.ares.login.service;

import cn.com.yitong.core.session.Session;
import cn.com.yitong.core.session.SessionListener;
import cn.com.yitong.core.session.util.SessionManagerUtils;
import cn.com.yitong.framework.dao.IbatisDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 退出登录或session超时时，更新session日志的应用结束时间
 * 
 * @author sunwei (sunw@yitong.com.cn)
 *
 */
@Service
public class LogoutService implements SessionListener {
	@Autowired
	@Qualifier("ibatisDao")
	private IbatisDao dao;
	
	@PostConstruct
	public void init() {
		SessionManagerUtils.resigerListener(this);
	}

	/**
	 * 退出登录时，更新session日志的应用结束时间
	 * @return
	 */
	public boolean updateLogoutDTime(Map sessionMap) {
		
		Object sessLogID = sessionMap.get("SESS_LOG_ID");
		if (sessLogID != null) {
			Map paramMap = new HashMap();
			paramMap.put("SESS_LOG_ID", sessLogID);
			paramMap.put("APP_STOP_DTIME", new Date());
			dao.update("SESSION_LOG.updateById", paramMap);
		}
		
		return true;
	}
	
	/**
	 * session超时时，更新session日志的应用结束时间
	 * @param session
	 * @return
	 */
	@Override
	public void onExpiration(Session session) {
		Object sessID = session.getId();
		Map paramsMap = new HashMap();
		paramsMap.put("SESSION_ID", sessID);
		List<Map> sessionLogList = dao.findList("SESSION_LOG.queryBySessionId", paramsMap);
		if (sessionLogList == null || sessionLogList.isEmpty()) {
			return;
		}
		Object sessLogID = sessionLogList.get(0).get("SESS_LOG_ID");
		int interval = session.getTimeout();
		if (interval < 0 || sessLogID == null) {
			return;
		}
		long now = System.currentTimeMillis();
		Date date = new Date(now - interval * 1000);
		Map paramMap = new HashMap();
		paramMap.put("SESS_LOG_ID", sessLogID);
		paramMap.put("APP_STOP_DTIME", date);
		dao.update("SESSION_LOG.updateById", paramMap);
	}

	@Override
	public void onStart(Session arg0) {
		// do nothing
	}

	@Override
	public void onStop(Session arg0) {
		// do nothing
	}
}
