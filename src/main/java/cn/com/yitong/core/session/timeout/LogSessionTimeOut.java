package cn.com.yitong.core.session.timeout;

import cn.com.yitong.core.session.Session;
import cn.com.yitong.tools.vo.SimpleResult;

/**
 * session 超时处理 日志打印
 * @author zhanglong@yitong.com.cn
 * @date 15/11/17
 */
public class LogSessionTimeOut extends AbstractSessionTimeOut{

    /**
     * 配置bean 初始化注册监听
     */
    public void init() {
        if(logger.isDebugEnabled()) {
            logger.debug("session超时处理 日志打印实现注册");
        }
        SessionTimeOutMonitorManager.registerSessionTimeOutHandler(this);
    }

    /**
     * 处理具体实现方式
     * @param simpleResult
     * @param sessionId
     */
    public void sessionTimeOut(SimpleResult simpleResult, String sessionId) {
        if(logger.isDebugEnabled()) {
            logger.debug("日志打印：sessionId:[" + sessionId + "]超时,结果result：" + simpleResult.getResult() + ", 描述：" + simpleResult.getMsg());
        }
    }
}
