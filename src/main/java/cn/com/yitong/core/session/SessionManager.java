package cn.com.yitong.core.session;

import cn.com.yitong.core.session.dao.SessionDao;

/**
 * Session 管理接口
 *
 * @author 李超（lc3@yitong.com.cn）
 */
public interface SessionManager {

    String ARES_SESSION_TOKENS_KEY = "JARESESSIONID";

    /**
     * 通过ID获得session，如果没有自动创建
     * @param id ID
     * @return
     */
    Session getOrCreateSession(String id);

    /**
     * 获得会话操作接口
     * @return
     */
    SessionDao getSessionDao();

    /**
     * 通过ID获得session
     * @param id ID
     * @return
     */
    Session getSession(String id);

    /**
     * 验证所有的Session
     */
    void validateSessions();

    /**
     * 提交更改
     * @param session
     */
    void submit(Session session);
}
