package com.zhuan.friends.utils;

/**
 * 定义常量接口
 */
public interface FriendsConstant {
    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;


    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;


    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;

    /**
     * 过期时间
     */
    int EXPIRED_SECONDS = 3600 * 12; // 过期时间的秒值 12个小时

    /**
     * 实体类型 ： 活动
     */
    int ENTITY_TYPE_ACTIVITY = 0;

    /**
     * 实体类型 ： 评论
     */
    int ENTITY_TYPE_COMMENT = 1;

    /**
     * 实体类型 ： 爱情加油站 2
     */
    int ENTITY_TYPE_CHEERUP = 2;


    /**
     * 实体类型 ： 请求添加好友
     */
    int ENTITY_ADD_USER_CASE = 3;


    /**
     * 实体类型 ： 请求创建加油加油站
     */
    int ENTITY_ADD_USER_CASE_CHEER = 4;

    /**
     * 实体类型 ： 成功牵手
     */
    int ENTITY_ADD_USER_CASE_SUCCESS = 5;

    /**
     * 事件主题 ： 点赞
     */
    String TOPIC_LIKE = "friendLike";

    /**
     * 事件主题 ： 评论
     */
    String TOPIC_COMMENT = "friendComment";

    /**
     * 系统用户 Id
     */
    int SYSTEM_USER_ID = 1;

    /**
     * 事件主题 ： 添加好友
     */
    String TOPIC_ADD_USER_CASE = "addUserCase";


}
