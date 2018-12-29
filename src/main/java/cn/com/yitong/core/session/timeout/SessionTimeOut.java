package cn.com.yitong.core.session.timeout;

import cn.com.yitong.tools.vo.SimpleResult;

/**
 * session 超时处理接口
 * @author zhanglong@yitong.com.cn
 * @date 15/11/17
 */
public interface SessionTimeOut {

    /**
     * session超时异常处理 统一接口
     * @param simpleResult
     */
    void sessionTimeOut(SimpleResult simpleResult, String sessionId);
}
