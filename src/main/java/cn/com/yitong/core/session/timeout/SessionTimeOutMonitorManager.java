package cn.com.yitong.core.session.timeout;

import cn.com.yitong.tools.vo.SimpleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * @author zhanglong@yitong.com.cn
 * @date 15/11/17
 */
public class SessionTimeOutMonitorManager {

    protected static Logger logger = LoggerFactory.getLogger(SessionTimeOutMonitorManager.class);

    private static final Set<SessionTimeOut> sessionTimeOutHandlers = new HashSet<SessionTimeOut>();

    /**
     * 注册监听
     * @param sessionHandler
     */
    public static void registerSessionTimeOutHandler(SessionTimeOut sessionHandler) {
        synchronized(sessionTimeOutHandlers) {
            if(null != sessionTimeOutHandlers) {
                if(!sessionTimeOutHandlers.contains(sessionHandler)) {
                    sessionTimeOutHandlers.add(sessionHandler);
                }
            }
        }
    }

    /**
     * 监听处理
     * @param result 超时结果
     * @param sessionId sessionId
     */
    public static void doHandler(SimpleResult result, String sessionId) {
        synchronized(sessionTimeOutHandlers) {
            for (SessionTimeOut sessionHandler : sessionTimeOutHandlers) {
                sessionHandler.sessionTimeOut(result, sessionId);
            }
        }
    }
}
