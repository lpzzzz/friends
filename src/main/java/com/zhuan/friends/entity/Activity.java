package com.zhuan.friends.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Activity {
    private int id;
    private int userId;
    private String title;
    private String content;
    private int type; // 0-普通活动 1-置顶活动
    private int status; // 0-正常 1-精华 3-拉黑
    private Date createTime;
    private int commentCount;
    private double score;
    private String faceImage;
    private Date expiredTime; // 截止报名时间
}
