package com.zhuan.friends.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * 封装事件对象
 */
public class Event {

    private String topic; // 主题名称
    private int userId; // 触发事件的用户id
    private int entityType; // 事件实体类型
    private int entityId; // 事件实体id
    private int entityUserId; // 事件实体的用户id
    private Map<String, Object> data = new HashMap<>(); // 为了更好的扩展性 更多的数据封装到Map中

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}
