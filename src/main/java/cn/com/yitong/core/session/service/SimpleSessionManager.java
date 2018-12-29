package cn.com.yitong.core.session.service;

import java.util.Collection;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.com.yitong.common.utils.SpringContextUtils;
import cn.com.yitong.common.utils.StringUtils;
import cn.com.yitong.core.session.Session;
import cn.com.yitong.core.session.SessionManager;
import cn.com.yitong.core.session.dao.SessionDao;
import cn.com.yitong.core.session.util.SessionManagerUtils;

/**
 * Session 管理类
 *
 * @author 李超（lc3@yitong.com.cn）
 */
@Service
public class SimpleSessionManager implements SessionManager {

    private static Logger logger = LoggerFactory.getLogger(SimpleSessionManager.class);
    private static SessionDao sessionDao;
    private static boolean enableSessionValidation = true;
    private static SessionValidationScheduler sessionValidationScheduler;

    protected static SessionDao getDefaultSessionDao() {
        if(null != sessionDao) {
            return sessionDao;
        }
        synchronized (SimpleSessionManager.class) {
            if(null != sessionDao) {
                return sessionDao;
            }
            sessionDao = SpringContextUtils.getBean(SessionDao.class);
            if(enableSessionValidation) {
                if(null == sessionValidationScheduler) {
                    sessionValidationScheduler = new SessionValidationScheduler(
                            SessionManagerUtils.getDefaultManager());
                }
                sessionValidationScheduler.enableSessionValidation();
            }
        }
        return sessionDao;
    }

    @Override
    public SessionDao getSessionDao() {
        return getDefaultSessionDao();
    }

    @Override
    public Session getSession(String id) {
        Session session = getSessionDao().get(id);
        if(null != session && session.isExpire()) {
            return null;
        }
        if(null != session) {
            session.touch();
        }
        return session;
    }

    @Override
    public Session getOrCreateSession(String id) {
        Session session = getSessionDao().getOrCreate(id);
        if(null != session && session.isExpire()) {
            return null;
        }
        if(null != session) {
            session.touch();
        }
        return session;
    }

    @Override
    public void validateSessions() {
        if(logger.isInfoEnabled()) {
            logger.info("开始验证所有的会话…");
        }
        Collection<Session> activeSessions = getSessionDao().getInvalidSessions();
        int invaliCount = 0;
        for (Session session : activeSessions) {
            String id = session.getId();
            if(logger.isDebugEnabled()) {
                logger.debug("会话[id:" + id + "]已失效或超时");
            }
            session.invalidate();
            invaliCount++;
        }
        if(logger.isInfoEnabled()) {
            logger.info("会话验证完成，[" + invaliCount + "]个会话被清理");
        }
    }

    @Override
    public void submit(Session session) {
        if(null == session) {
            return;
        }
        if(session.isExpire()) {
            return;
        }
        // 对于非设备端请求的会话，不进行持久化到数据库
        if(StringUtils.isEmpty(session.getDeviceCode()) && StringUtils.isEmpty(session.getUserId())) {
            return;
        }
        if(session.isNew()) {
            getSessionDao().create(session);
            session.setIsNew(false);
        } else if(session.isChange()) {
            getSessionDao().update(session);
            session.setIsChange(false);
        }
    }

    public boolean isEnableSessionValidation() {
        return enableSessionValidation;
    }

    public void setEnableSessionValidation(boolean enableSessionValidation) {
        SimpleSessionManager.enableSessionValidation = enableSessionValidation;
    }

    @PreDestroy
    public void destroy() {
        if(null != sessionValidationScheduler) {
            sessionValidationScheduler.disableSessionValidation();
        }
    }
}
