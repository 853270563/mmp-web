package cn.com.yitong.core.session;

/**
 * Session 监听器接口
 *
 * @author 李超（lc3@yitong.com.cn）
 */
public interface SessionListener {

    /**
     * session创建事件接口
     * @param session
     */
    void onStart(Session session);

    /**
     * session结束事件接口
     * @param session
     */
    void onStop(Session session);

    /**
     * sesseion终结事件
     * @param session
     */
    void onExpiration(Session session);
}
