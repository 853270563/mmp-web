package cn.com.yitong.core.session;

import java.util.Collection;
import java.util.Date;

/**
 * Session 接口
 *
 * @author 李超（lc3@yitong.com.cn）
 */
public interface Session {

    /**
     * 获取ID
     * @return ID
     */
    String getId();

    /**
     * 获取创建时间
     * @return 创建时间
     */
    Date getCreateTime();

    /**
     * 获取最后访问时间
     * @return 最后访问时间
     */
    Date getLastAccessTime();

    /**
     * 更新最后访问时间
     * @return 自身
     */
    Session touch();

    /**
     * 获得超时时间
     * @return 超时时间
     */
    int getTimeout();

    /**
     * 设置超时时间
     * @param timeout 超时时间，单位秒
     * @return 自身
     */
    Session setTimeout(int timeout);

    /**
     * 使Session失效
     * @return
     */
    Session invalidate();

    /**
     * 是否已销毁
     * @return 是否已销毁
     */
    boolean isExpire();

    /**
     * 设置变量值
     * @param key 键
     * @param value 值
     * @return 自身
     */
    Session setAttribute(Object key, Object value);

    /**
     * 获得键对应的值
     * @param key 键
     * @return 键对应的值
     */
    Object getAttribute(Object key);

    /**
     * 删除键
     * @param key 键
     * @return 键对应的值
     */
    Object removeAttribute(Object key);

    /**
     * 主键集合
     * @return 主键集合
     */
    Collection<Object> getAttributeKeys();

    /**
     * 设置密钥，经过二次加密后的
     * @param skey 密钥
     * @return
     */
    Session setSkey(String skey);

    /**
     * 获取密钥
     * @return
     */
    String getSkey();

    /**
     * 设置用户标识
     * @return
     */
    Session setUserId(String userId);

    /**
     * 获取用户标识
     * @return
     */
    String getUserId();

    /**
     * 设置设备编号
     * @return
     */
    Session setDeviceCode(String deviceCode);

    /**
     * 获得设备编号
     * @return
     */
    String getDeviceCode();

    /**
     * 设置服务器标识
     * @return
     */
    Session setServerId(String serverId);

    /**
     * 获取服务器标识
     * @return
     */
    String getServerId();

    /**
     * 设置用户认证状态
     * @return
     */
    Session setAuthStatus(String authStatus);

    /**
     * 获取用户认证状态
     * @return
     */
    String getAuthStatus();

    /**
     * 设置事件ID
     * @param eventId
     * @return
     */
    Session setEventId(String eventId);

    /**
     * 获取事件ID
     * @return
     */
    String getEventId();

    /**
     * 设备消息ID
     * @param msgId
     * @return
     */
    Session setMsgId(String msgId);

    /**
     * 获取消息ID
     * @return
     */
    String getMsgId();

    /**
     * 设置是否为新增状态
     * @param isNew
     * @return
     */
    Session setIsNew(boolean isNew);

    /**
     * 得到是否为新增状态
     * @return
     */
    boolean isNew();

    /**
     * 设置是否改变
     * @param isChange
     * @return
     */
    Session setIsChange(boolean isChange);
    /**
     * 是否改变
     * @return
     */
    boolean isChange();

    /**
     * 获得版本，用于乐观锁
     * @return
     */
    Long getVersion();

    /**
     * 设置版本，用于乐观锁
     * @param version
     */
    Session setVersion(Long version);

    /**
     * 获取信息串
     * @return
     */
    String getMsgidSet();

    /**
     * 设置消息串
     * @param msgidSet
     * @return
     */
    Session setMsgidSet(String msgidSet);
}
