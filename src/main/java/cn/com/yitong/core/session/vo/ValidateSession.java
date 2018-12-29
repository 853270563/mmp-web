package cn.com.yitong.core.session.vo;

import cn.com.yitong.core.session.Session;
import cn.com.yitong.core.session.SessionException;

/**
 * 支持验证的Session
 * @author lc3@yitong.com.cn
 */
public interface ValidateSession extends Session {

    /**
     * 验证Session是否有效，无效时抛出异常
     * @throws cn.com.yitong.core.session.SessionException
     */
    void validate() throws SessionException;
}
