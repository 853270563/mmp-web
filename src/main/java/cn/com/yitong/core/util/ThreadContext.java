package cn.com.yitong.core.util;

import java.util.HashMap;
import java.util.Map;

import cn.com.yitong.common.utils.StringUtils;
import cn.com.yitong.core.session.Session;
import cn.com.yitong.core.session.util.SessionManagerUtils;
import cn.com.yitong.framework.filter.ReusableHttpServletRequest;

/**
 * 线程上下文管理类
 * @author lc3@yitong.com.cn
 */
public abstract class ThreadContext {

    private static final ThreadLocal<Map<Object, Object>> contexts =
            new InheritableThreadLocal<Map<Object, Object>>() {
                @Override
                protected Map<Object, Object> initialValue() {
                    return new HashMap<Object, Object>();
                }
            };

    public static final String SESSION_KEY = ThreadContext.class.getName() + "_SESSION_ID_KEY";
    public static final String HTTP_REQUEST_KEY = ThreadContext.class.getName() + "HTTP_REQUEST_KEY";
    public static final String IS_LOGINING_KEY = ThreadContext.class.getName() + "_IS_LOGINING";
    public static final String CODEC_TYPE_KEY = ThreadContext.class.getName() + "_CODEC_TYPE";
    public static final String REQUEST_MESSAGE_KEY =  ThreadContext.class.getName() + "_MESSAGE_ID";
    public static final String CHECK_MESSAGE_ID =  ThreadContext.class.getName() + "_CHECK_MSG_ID";

    protected ThreadContext(){}

    /**
     * 得到当前线程上下文
     * @return
     */
    public static Map<Object, Object> getContexts() {
        return null != contexts ? new HashMap<Object, Object>(contexts.get()) : null;
    }

    /**
     * 设置当前线程上下文
     * @param newResources 新的上下文环境
     */
    public static void setContexts(Map<Object, Object> newResources) {
        if(null == newResources || newResources.isEmpty()) {
            return;
        }
        contexts.get().clear();
        contexts.get().putAll(newResources);
    }

    /**
     * 从当前线程上下文中取值
     * @param key key
     * @param clazz 返回类型
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getValue(Object key, Class<T> clazz) {
        return (T) contexts.get().get(key);
    }

    /**
     * 从当前线程上下文中取值
     * @param key 键
     * @return
     */
    public static Object get(Object key) {
        return contexts.get().get(key);
    }

    /**
     * 设置当前线程上下文中的值
     * @param key 键
     * @param value 值
     */
    public static void put(Object key, Object value) {
        if(null == key) {
            throw new IllegalArgumentException("key不能为空");
        }

        if(null == value) {
            remove(key);
            return;
        }

        contexts.get().put(key, value);
    }

    /**
     * 删除当前线程上下文的值
     * @param key 键
     * @return
     */
    public static Object remove(Object key) {
        return contexts.get().remove(key);
    }

    /**
     * 删除当前线程上下文环境
     */
    public static void remove() {
        contexts.remove();
    }

    /**
     * 绑定会话到当前线程
     * @param session 会话
     */
    public static void bindSession(Session session) {
        if(null != session) {
            put(SESSION_KEY, session);
        }
    }

    /**
     * 解绑当前线程的会话
     * @return
     */
    public static Session unBindSession() {
        return (Session) remove(SESSION_KEY);
    }

    /**
     * 获得当前线程会话
     * @return
     */
    public static Session getSession(boolean autoCreate) {
        Session session = (Session) get(SESSION_KEY);
        if(null != session && !session.isExpire() || !autoCreate) {
            return session;
        }
        synchronized (SESSION_KEY) {
            session = (Session) get(SESSION_KEY);
            if(null != session) {
                return session;
            }
            ReusableHttpServletRequest httpRequest = getHttpRequest();
            if(null == httpRequest) {
                return null;
            }
            String sessionId = httpRequest.getRequestedSessionId();
            session = SessionManagerUtils.getDefaultManager().getOrCreateSession(
                    SecurityUtils.formatToken(sessionId));
            bindSession(session);
        }
        return session;
    }

    /**
     * 绑定HttpRequest到当前线程
     * @param request 会话
     */
    public static void bindHttpRequest(ReusableHttpServletRequest request) {
        if(null != request) {
            put(HTTP_REQUEST_KEY, request);
        }
    }

    /**
     * 获得当前线程HttpRequest
     * @return
     */
    public static ReusableHttpServletRequest getHttpRequest() {
        return (ReusableHttpServletRequest) get(HTTP_REQUEST_KEY);
    }

    /**
     * 解绑当前线程的HttpRequest
     * @return
     */
    public static ReusableHttpServletRequest unBindHttpRequest() {
        return (ReusableHttpServletRequest) remove(HTTP_REQUEST_KEY);
    }

    /**
     * 设置是否为登录操作
     * @param isLogining
     */
    public static void setIsLogining(boolean isLogining) {
        if(isLogining) {
            put(IS_LOGINING_KEY, true);
        }
    }

    /**
     * 判断当前操作是否为登录
     * @return
     */
    public static boolean isLogining() {
        return null != get(IS_LOGINING_KEY);
    }



    /**
     * 设置当前加解密类型
     * @param codecType
     */
    public static void setCodecType(int codecType) {
        put(CODEC_TYPE_KEY, codecType);
    }

    /**
     * 判断当前加解密类型
     * @return
     */
    public static Integer getCodecType() {
        return (Integer) get(CODEC_TYPE_KEY);
    }

    /**
     * 获取当前请求的msgId
     * @return
     */
    public static String getRequestMessageId() {
        String msgId = (String)get(REQUEST_MESSAGE_KEY);
        if(StringUtils.isEmpty(msgId)) {
            msgId =  getHttpRequest().getRequestMessageId();
            put(REQUEST_MESSAGE_KEY, msgId);
        }
        return msgId;
    }

    /**
     * 是否检查消息Id
     * @return
     */
    public static boolean isCheckMsgId() {
        return null != get(CHECK_MESSAGE_ID);
    }

    /**
     * 设置检查消息Id
     * @param isCheckMsgId
     */
    public static void setIsCheckMsgId(boolean isCheckMsgId) {
        if(isCheckMsgId) {
            put(CHECK_MESSAGE_ID, true);
        }
    }
}
