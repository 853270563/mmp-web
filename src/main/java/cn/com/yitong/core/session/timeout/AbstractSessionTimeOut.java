package cn.com.yitong.core.session.timeout;

import cn.com.yitong.tools.vo.SimpleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * sessionTimeOut 基础实现
 * @author zhanglong@yitong.com.cn
 * @date 15/11/17
 */
public abstract class AbstractSessionTimeOut implements SessionTimeOut {

    protected Logger logger = LoggerFactory.getLogger(getClass());

}
