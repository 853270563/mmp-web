package cn.com.yitong.ares.servCtl.model;

import cn.com.yitong.common.persistence.BaseEntity;

/**
 * 事件配置表
 *
 * @author kwang@yitong.com.cn
 */
public class MhhEventConfig extends BaseEntity {

    /**
     * 事件ID:   2位时,代表模块  4位时,代表功能  6位时,代表具体事件
     */
    private String eventId;
    /**
     * 事件名称
     */
    private String eventName;
    /**
     * 并发数
     */
    private Integer limitCount;
    /**
     * 事件等级:   1. 代表模块  2. 代表功能  3. 代表事件
     */
    private Long eventLevel;
    /**
     * 父事件
     */
    private String parentId;
    /**
     * 备注
     */
    private String remarks;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    public Integer getLimitCount() {
        return limitCount;
    }

    public void setLimitCount(Integer limitCount) {
        this.limitCount = limitCount;
    }
    public Long getEventLevel() {
        return eventLevel;
    }

    public void setEventLevel(Long eventLevel) {
        this.eventLevel = eventLevel;
    }
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public static class TF {

        public static String TABLE_NAME = "ARES_EVENT_CONFIG";   // 表名

        public static String TABLE_SCHEMA = "MM";   // 库名

        public static String eventId = "EVENT_ID";  // 事件ID:   2位时,代表模块  4位时,代表功能  6位时,代表具体事件
        public static String eventName = "EVENT_NAME";  // 事件名称
        public static String limitCount = "LIMIT_COUNT";  // 并发数
        public static String eventLevel = "EVENT_LEVEL";  // 事件等级:   1. 代表模块  2. 代表功能  3. 代表事件
        public static String parentId = "PARENT_ID";  // 父事件
        public static String remarks = "REMARKS";  // 备注

    }
}
