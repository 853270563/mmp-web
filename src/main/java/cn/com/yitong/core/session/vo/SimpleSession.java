package cn.com.yitong.core.session.vo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import cn.com.yitong.common.utils.ConfigUtils;
import cn.com.yitong.core.session.Session;
import cn.com.yitong.core.session.SessionException;
import cn.com.yitong.core.session.util.SessionManagerUtils;
import cn.com.yitong.core.util.ConfigName;
import cn.com.yitong.core.util.SecurityUtils;

/**
 * 简单Session实现
 *
 * @author 李超（lc3@yitong.com.cn）
 */
public class SimpleSession implements Session, ValidateSession, Serializable {

    private static final long serialVersionUID = 4950012827438775928L;

    private final Map<Object, Object> datas = new HashMap<Object, Object>();

    /**
     * Session Id
     */
    private String id;
    /**
     * 创建时间
     */
    private Date createTime = new Date();
    /**
     * 最后访问时间
     */
    private Date lastAccessTime = new Date();
    /**
     * 超时时间间隔，单位：秒
     */
    private int timeout = ConfigUtils.getValue(ConfigName.SESSION_TIMEOUT_SECOND,
            ConfigName.SESSION_TIMEOUT_SECOND_DEFVAL);
    /**
     * 是否已销毁
     */
    private boolean isExpire = false;
    /**
     * 密钥
     */
    private String skey;
    /**
     * 用户标识
     */
    private String userId;
    /**
     * 设备编号
     */
    private String deviceCode;
    /**
     * 服务器标识
     */
    private String serverId;
    /**
     * 用户认证状态
     */
    private String authStatus;
    /**
     * 新增状态
     */
    private boolean isNew;
    /**
     * 改变状态
     */
    private boolean isChange;
    /**
     * 事件ID
     */
    private String eventId;
    /**
     * 消息ID
     */
    private String msgId;
    /**
     * 版本，用于乐观锁
     */
    private Long version;

    /**
     * 消息Id串
     */
    private String msgidSet;

    public SimpleSession(String id) {
        if(!StringUtils.hasText(id)) {
            id = SecurityUtils.genToken();
        }
        this.id = id;
    }

    public SimpleSession(Builder builder) {
        if(null != builder.datas) {
            datas.putAll(builder.datas);
        }
        id = builder.id;
        createTime = builder.createTime;
        lastAccessTime = builder.lastAccessTime;
        timeout = builder.timeout;
        isExpire = builder.isExpire;
        skey = builder.skey;
        userId = builder.userId;
        deviceCode = builder.deviceCode;
        serverId = builder.serverId;
        authStatus = builder.authStatus;
        eventId = builder.eventId;
        msgId = builder.msgId;
        version = builder.version;
        msgidSet = builder.msgidSet;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Date getCreateTime() {
        return this.createTime;
    }

    @Override
    public Date getLastAccessTime() {
        return this.lastAccessTime;
    }

    @Override
    public Session touch() {
        validate();
        this.lastAccessTime = new Date();
        onChange();
        return this;
    }

    @Override
    public int getTimeout() {
        return this.timeout;
    }

    @Override
    public Session setTimeout(int timeout) {
        validate();
        if(this.timeout == timeout) {
            return this;
        }
        this.timeout = timeout;
        onChange();
        return this;
    }

    @Override
    public Session invalidate() {
        if(this.isExpire) {
            return this;
        }
        synchronized (this) {
            if(this.isExpire) {
                return this;
            }
            this.isExpire = true;
            onInvalidate();
            return this;
        }
    }

    /**
     * 是否已销毁
     * @return 是否已销毁
     */
    public boolean isExpire() {
        return isExpire;
    }

    @Override
    public Session setAttribute(Object key, Object value) {
        validate();
        if(null == key || value == getValue(key)) {
            return this;
        }
        setValue(key, value);
        onChange();
        return this;
    }

    @Override
    public Object getAttribute(Object key) {
        validate();
        if(null == key) {
            return null;
        }
        return getValue(key);
    }

    /**
     * 批量设置属性
     * @param attrs
     * @return
     */
    public Session setAttributes(Map<Object, Object> attrs) {
        validate();
        datas.clear();
        if(null != attrs) {
            datas.putAll(attrs);
        }
        return this;
    }

    @Override
    public Object removeAttribute(Object key) {
        validate();
        if(null == key) {
            return null;
        }
        Object v = removeKey(key);
        onChange();
        return v;
    }

    /**
     * 检查session是否有效，无效时抛出异常
     */
    @Override
    public void validate() throws SessionException {
        if(isExpire || System.currentTimeMillis() - this.lastAccessTime.getTime() > timeout * 1000) {
            throw new SessionException();
        }
    }

    /**
     * 改变时处理函数
     */
    protected void onChange() {
        isChange = true;
        SessionManagerUtils.onChange(this);
    }

    /**
     * 销毁时处理函数
     */
    private void onInvalidate() {
        SessionManagerUtils.onInvalidate(this);
    }

    protected Session setValue(Object key, Object value) {
        datas.put(key, value);
        return this;
    }

    protected Object getValue(Object key) {
        return datas.get(key);
    }

    protected Object removeKey(Object key) {
        Object remove = datas.remove(key);
        onChange();
        return remove;
    }

    @Override
    public Collection<Object> getAttributeKeys() {
        return datas.keySet();
    }

    @Override
    public Session setSkey(String skey) {
        validate();
        this.skey = skey;
        onChange();
        return this;
    }

    @Override
    public String getSkey() {
//        validate();   // 为保证会话失效后，还能返回加密报文，支持失效后获取Skey
        return skey;
    }

    @Override
    public Session setUserId(String userId) {
        validate();
        this.userId = userId;
        onChange();
        return this;
    }

    @Override
    public String getUserId() {
        validate();
        return userId;
    }

    @Override
    public Session setDeviceCode(String deviceCode) {
        validate();
        this.deviceCode = deviceCode;
        onChange();
        return this;
    }

    @Override
    public String getDeviceCode() {
        validate();
        return deviceCode;
    }

    @Override
    public Session setServerId(String serverId) {
        validate();
        this.serverId = serverId;
        onChange();
        return this;
    }

    @Override
    public String getServerId() {
        validate();
        return serverId;
    }

    @Override
    public Session setAuthStatus(String authStatus) {
        validate();
        this.authStatus = authStatus;
        onChange();
        return this;
    }

    @Override
    public String getAuthStatus() {
        validate();
        return authStatus;
    }

    @Override
    public Session setEventId(String eventId) {
        this.eventId = eventId;
        return this;
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public Session setMsgId(String msgId) {
        this.msgId = msgId;
        return this;
    }

    @Override
    public String getMsgId() {
        return msgId;
    }

    @Override
    public String getMsgidSet() {
        return msgidSet;
    }

    @Override
    public Session setMsgidSet(String msgidSet) {
        this.msgidSet = msgidSet;
        return this;
    }

    @Override
    public Session setIsNew(boolean isNew) {
        this.isNew = isNew;
        return this;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @Override
    public Session setIsChange(boolean isChange) {
        this.isChange = isChange;
        return this;
    }

    @Override
    public boolean isChange() {
        return isChange;
    }

    @Override
    public Long getVersion() {
        return null != version ? version : 0;
    }

    @Override
    public Session setVersion(Long version) {
        this.version = version;
        return this;
    }

    @Override
    public String toString() {
        return "SimpleSession{" +
                ", id='" + id + '\'' +
                ", createTime=" + createTime +
                ", lastAccessTime=" + lastAccessTime +
                ", timeout=" + timeout +
                ", isExpire=" + isExpire +
                ", skey='" + skey + '\'' +
                ", userId='" + userId + '\'' +
                ", deviceCode='" + deviceCode + '\'' +
                ", serverId='" + serverId + '\'' +
                ", authStatus='" + authStatus + '\'' +
                ", isNew=" + isNew +
                ", isChange=" + isChange +
                ", eventId='" + eventId + '\'' +
                ", msgId='" + msgId + '\'' +
                ", version=" + version +
                '}';
    }

    public static class Builder {
        private Map<Object, Object> datas = new HashMap<Object, Object>();
        private String id;
        private Date createTime = new Date();
        private Date lastAccessTime = new Date();
        private int timeout = ConfigUtils.getValue(ConfigName.SESSION_TIMEOUT_SECOND,
                ConfigName.SESSION_TIMEOUT_SECOND_DEFVAL);
        private boolean isExpire = false;
        private String skey;
        private String userId;
        private String deviceCode;
        private String serverId;
        private String authStatus;
        private String eventId;
        private String msgId;
        private Long version;
        private String msgidSet;
        public SimpleSession builder() {
            return new SimpleSession(this);
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        public void setLastAccessTime(Date lastAccessTime) {
            this.lastAccessTime = lastAccessTime;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public void setExpire(boolean isExpire) {
            this.isExpire = isExpire;
        }

        public void setSkey(String skey) {
            this.skey = skey;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public void setDeviceCode(String deviceCode) {
            this.deviceCode = deviceCode;
        }

        public void setServerId(String serverId) {
            this.serverId = serverId;
        }

        public void setAuthStatus(String authStatus) {
            this.authStatus = authStatus;
        }

        public void setDatas(Map<Object, Object> datas) {
            this.datas = datas;
        }

        public void setEventId(String eventId) {
            this.eventId = eventId;
        }

        public void setMsgId(String msgId) {
            this.msgId = msgId;
        }

        public void setVersion(Long version) {
            this.version = version;
        }

        public String getMsgidSet() {
            return msgidSet;
        }

        public void setMsgidSet(String msgidSet) {
            this.msgidSet = msgidSet;
        }
    }
}
