package cn.com.yitong.core.session.dao;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import cn.com.yitong.core.session.Session;

/**
 * SessionDAO的cache方式实现
 * @author 李超（lc3@yitong.com.cn）
 */
public class CacheSessionDAO extends AbstractSessionDao {

    private CacheManager cacheManager;

    private Cache getCache() {
        return cacheManager.getCache("SessionCache");
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    private Object createEle(Session session) {
        return session;
    }

    private Session getVal(Cache.ValueWrapper obj) {
        return null == obj ? null : (Session) obj.get();
    }

    @Override
    public void create(Session session) {
        validate(session);
        getCache().put(session.getId(), createEle(session));
    }

    @Override
    public void delete(Session session) {
        validate(session);
        getCache().evict(session.getId());
    }

    @Override
    public void update(Session session) {
        validate(session);
        getCache().put(session.getId(), createEle(session));
    }

    @Override
    public Session get(String id) {
        if(null == id) {
            return null;
        }
        return getVal(getCache().get(id));
    }

}
