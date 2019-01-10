package cn.com.yitong.ares.servCtl.dao;

/**
 * 服务控制的配置访问接口
 * @author lc3@yitong.com.cn
 */
public interface ServCtlConfigDao {

    /**
     * 获取事件对应的限制配置
     * @param eventId 事件ID
     * @return 0 为无限制；   其它为限制数量
     */
    int getLimitConfig(String eventId);
}
