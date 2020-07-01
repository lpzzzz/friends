package com.zhuan.friends.entity;

import lombok.Data;

import java.util.Date;

/**
 * 用户实体类
 */
@Data
public class User {
    private int id;
    private String username;
    private int gender; // 用户性别 0-男 1-女
    private String password;
    private int age;
    private String location;
    private int marriage;
    private String tel;
    private String salt;
    private String email;
    private int type;
    private int status;
    private String activationCode;
    private String headerUrl;
    private Date createTime;
    private String hobby; // 爱好
}
