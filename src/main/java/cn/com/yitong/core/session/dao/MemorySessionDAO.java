package cn.com.yitong.core.session.dao;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.yitong.core.session.Session;

/**
 * session持久化处理类，存储介质为内存，不支持集群和分布式
 *
 * @author 李超（lc3@yitong.com.cn）
 */
public class MemorySessionDAO extends AbstractSessionDao {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 存储session
     */
    private static final Map<Object, Session> sessions =
            new ConcurrentHashMap<Object, Session>();

    @Override
    public void create(Session session) {
        validate(session);
        sessions.put(session.getId(), session);
    }

    @Override
    public void delete(Session session) {
        validate(session);
        sessions.remove(session.getId());
    }

    @Override
    public void update(Session session) {
        validate(session);
        sessions.put(session.getId(), session);
    }

    @Override
    public Session get(String id) {
        if(null == id) {
            return null;
        }
        return sessions.get(id);
    }

    @Override
    public Collection<Session> getAllSession() {
        return Collections.unmodifiableCollection(sessions.values());
    }
}
