package cn.com.yitong.ares.servCtl.dao;

/**
 * 服务控制存储访问接口
 * @author lc3@yitong.com.cn
 */
public interface ServCtlDao {

    /**
     * 开始一会话事件
     * @param sessionId 会话ID
     * @param eventId 事件ID
     */
    void startEvent(String sessionId, String eventId);

    /**
     * 结束一会话事件
     * @param sessionId 会话ID
     * @param eventId 事件ID，为空时代表结束此session会话
     */
    void stopEvent(String sessionId, String eventId);

    /**
     * 得到此事件ID正在进行中的数量
     * @param eventId 事件ID
     * @return 正在进行中的数量
     */
    int getAccessCount(String eventId);
}
