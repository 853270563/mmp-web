package cn.com.yitong.ares.servCtl.dao.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.yitong.core.session.Session;
import cn.com.yitong.core.session.SessionListener;
import cn.com.yitong.core.session.util.SessionManagerUtils;
import cn.com.yitong.ares.servCtl.dao.ServCtlDao;

/**
 * 并发控制存储之内存实现
 * @author lc3@yitong.com.cn
 */
public class ServCtlMemDaoImpl implements ServCtlDao, SessionListener {

    private static final ConcurrentMap<String, String> sessId2EventIdMap = new ConcurrentHashMap<String, String>();
    private static Logger logger = LoggerFactory.getLogger(ServCtlMemDaoImpl.class);

    @PostConstruct
    public void init() {
        SessionManagerUtils.resigerListener(this);
    }

    @Override
    public void startEvent(String sessionId, String eventId) {
        if(logger.isTraceEnabled()) {
            logger.trace("启动事件：" + eventId + ", 会话标识:" + sessionId);
        }
        sessId2EventIdMap.put(sessionId, eventId);
    }

    @Override
    public void stopEvent(String sessionId, String eventId) {
        if(logger.isTraceEnabled()) {
            logger.trace("结束事件：" + eventId + "， 会话标识：" + sessionId);
        }
        if(null == eventId) {
            sessId2EventIdMap.remove(sessionId);
        } else {
            sessId2EventIdMap.remove(sessionId, eventId);
        }
    }

    @Override
    public int getAccessCount(String eventId) {
        int count = 0;
        if(null == eventId) {
            if(logger.isTraceEnabled()) {
                logger.trace("当前事件记数为:" + count + "，事件标识：" + eventId);
            }
            return count;
        }
        for (String s : sessId2EventIdMap.values()) {
            if(eventId.equals(s)) {
                count++;
            }
        }
        if(logger.isTraceEnabled()) {
            logger.trace("当前事件记数为:" + count);
        }
        return count;
    }

    @Override
    public void onStart(Session session) {

    }

    @Override
    public void onStop(Session session) {
        stopEvent(session.getId(), null);
    }

    @Override
    public void onExpiration(Session session) {
        stopEvent(session.getId(), null);
    }
}
