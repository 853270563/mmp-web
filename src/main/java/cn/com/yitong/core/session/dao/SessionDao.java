package cn.com.yitong.core.session.dao;

import java.util.Collection;

import cn.com.yitong.core.session.Session;

/**
 * AresSession持久化接口
 *
 * @author 李超（lc3@yitong.com.cn）
 */
public interface SessionDao {

    /**
     * 新增
     * @param session 会话
     */
    void create(Session session);

    /**
     * 删除
     * @param session 会话
     */
    void delete(Session session);

    /**
     * 修改
     * @param session 会话
     */
    void update(Session session);

    /**
     * 通过ID获取会话
     * @param id ID
     * @return 会话
     */
    Session get(String id);

    /**
     * 通过ID获取会话，没有则创建一个新的
     * @param id ID
     * @return 会话
     */
    Session getOrCreate(String id);

    /**
     * 得到活动的所有Session
     * @return
     */
    Collection<Session> getActiveSessions();

    /**
     * 得到无效的所有会话
     * @return
     */
    Collection<Session> getInvalidSessions();

    /**
     * 得到所有的会话
     * @return
     */
    Collection<Session> getAllSession();
}
