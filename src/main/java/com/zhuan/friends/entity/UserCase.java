package com.zhuan.friends.entity;

import lombok.Data;

import java.util.Date;

/**
 *
 */
@Data
public class UserCase {
    private int id;
    private int requestId; // 发起请求用户id
    private int responseId; // 响应用户id
    private String conversationId; // 请求 与 响应组合 id
    private String content; // 加油站内容 成功案例内容
    private int status; // 状态 0-删除或未接受 1-成为好友 2-成功牵手
    private Date createTime; // 成为好友的时间
    private Date cheerTime; // 加油站创建时间
    private Date successTime; // 成功牵手时间
    private Double score; // 好友成熟度
}
