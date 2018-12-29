package cn.com.yitong.core.session.dao;

import cn.com.yitong.core.session.Session;
import cn.com.yitong.core.util.SessionRedisUtils;

/**
 * @author zhanglong@yitong.com.cn
 * @date 15/10/21
 */
public class RedisCacheSessionDao extends AbstractSessionDao {

    @Override
    public void create(Session session) {
        validate(session);
        SessionRedisUtils.saveSession(session);
    }

    @Override
    public void delete(Session session) {
        validate(session);
        SessionRedisUtils.deleteSession(session.getId());
    }

    @Override
    public void update(Session session) {
        validate(session);
        SessionRedisUtils.saveSession(session);
    }

    @Override
    public Session get(String id) {
        return SessionRedisUtils.getSession(id);
    }
}
