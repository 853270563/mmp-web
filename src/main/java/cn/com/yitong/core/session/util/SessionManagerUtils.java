package cn.com.yitong.core.session.util;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.yitong.common.utils.SpringContextUtils;
import cn.com.yitong.core.session.Session;
import cn.com.yitong.core.session.SessionListener;
import cn.com.yitong.core.session.SessionManager;

/**
 * Session管理工具类
 *
 * @author 李超（lc3@yitong.com.cn）
 */
public class SessionManagerUtils {

    private static Logger logger = LoggerFactory.getLogger(SessionManagerUtils.class);

    private final static List<SessionListener> SESSION_LISTENERS =
            new LinkedList<SessionListener>();

    /**
     * 注册session监听器
     * @param listener
     */
    public static void resigerListener(SessionListener listener) {
        if(null == listener) {
            return;
        }
        SESSION_LISTENERS.add(listener);
    }

    /**
     * session创建时代理处理函数
     * @param session
     */
    public static void onSessionStarted(Session session) {
        if(null == session) {
            return;
        }
        if(logger.isDebugEnabled()) {
            logger.debug("SessionStarted: [{}]", session.toString());
        }
        for (SessionListener listener : SESSION_LISTENERS) {
            listener.onStart(session);
        }
    }

    /**
     * session停用代理处理函数
     * @param session
     */
    public static void onSessionStoped(Session session) {
        if(null == session) {
            return;
        }
        if(logger.isDebugEnabled()) {
            logger.debug("SessionStoped: [{}]", session.toString());
        }
        for (SessionListener listener : SESSION_LISTENERS) {
            listener.onStop(session);
        }
        getDefaultManager().getSessionDao().delete(session);
    }

    /**
     * session销毁代理处理函数
     * @param session
     */
    public static void onSessionExpiration(Session session) {
        if(null == session) {
            return;
        }
        if(logger.isDebugEnabled()) {
            logger.debug("SessionExpiration: [{}]", session.toString());
        }
        for (SessionListener listener : SESSION_LISTENERS) {
            listener.onExpiration(session);
        }
        getDefaultManager().getSessionDao().delete(session);
    }

    /**
     * 触发创建事件的接口
     * @param session
     */
    public static void onCreate(Session session) {
        SessionManagerUtils.onSessionStarted(session);
    }

    /**
     * 触发销毁事件的接口
     * @param session
     */
    public static void onInvalidate(Session session) {
        SessionManagerUtils.onSessionExpiration(session);
    }

    /**
     * 触发改变事件的接口
     * @param session
     */
    public static void onChange(Session session) {
        // 扩展用
    }

    private static SessionManager sessionManager;
    /**
     * 得到默认的AresSession管理类，找不到时报异常
     * @return
     */
    public static SessionManager getDefaultManager() throws RuntimeException {
        if(null != sessionManager) {
            return sessionManager;
        }
        synchronized (SessionManagerUtils.class) {
            if(null != sessionManager) {
                return sessionManager;
            }
            sessionManager = SpringContextUtils.getBean(SessionManager.class);
        }
        if (null == sessionManager) {
            throw new RuntimeException("获取默认的Session管理类失败");
        }
        return sessionManager;
    }
}
