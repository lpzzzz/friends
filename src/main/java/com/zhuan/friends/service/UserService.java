package com.zhuan.friends.service;

import com.zhuan.friends.dao.LoginTicketMapper;
import com.zhuan.friends.dao.UserMapper;
import com.zhuan.friends.entity.LoginTicket;
import com.zhuan.friends.entity.User;
import com.zhuan.friends.utils.FriendsConstant;
import com.zhuan.friends.utils.FriendsUtils;
import com.zhuan.friends.utils.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;

@Service
public class UserService implements FriendsConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${friends.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private LoginTicketMapper loginTicketMapper; // 存储登录凭证


    /**
     * 根据id查询用户信息
     *
     * @param id
     * @return
     */
    public User selectById(int id) {
        return userMapper.selectById(id);
    }

    public List<User> selectAll(int offset, int limit) {
        return userMapper.selectAll(offset, limit);
    }

    public int selectUserRows() {
        return userMapper.selectUserRows();
    }

    public User selectUserByUsername(String username) {
        return userMapper.selectByUserName(username);
    }

    public LoginTicket selectLoginTicketByTicket(String ticket) {
        return loginTicketMapper.selectLoginTicket(ticket);
    }


    /**
     * 查询系统中的有缘人
     *
     * @param username
     * @param location
     * @param gender
     * @param offset
     * @param limit
     * @return
     */
    public List<User> selectLuckyUser(
            String username,
            String location,
            Integer gender,
            int offset,
            int limit) {
        return userMapper.selectLuckyUser(username, location, gender, offset, limit);
    }

    /**
     * 查询系统中和自己有缘的人的数量
     *
     * @param username
     * @param location
     * @param gender
     * @return
     */
    public int selectLuckyUserRows(String username,
                                   String location,
                                   Integer gender) {
        return userMapper.selectLuckyUserRows(username, location, gender);
    }

    /**
     * 用户完善个人信息
     *
     * @param id
     * @param headerUrl
     * @param username
     * @param age
     * @param location
     * @param marriage
     * @param tel
     * @param hobby
     */
    public void updateUserHeader(int id,
                                 String headerUrl,
                                 String username,
                                 Integer age,
                                 String location,
                                 Integer marriage,
                                 String tel,
                                 String hobby,
                                 Integer gender) {
        userMapper.updateUserHeader(id, headerUrl, username, age, location, marriage, tel, hobby, gender);
    }

    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }

        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "用户名不能为空");
            return map;
        }

        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }

        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }

        // 验证当前注册的账号是否已经存在
        User u = userMapper.selectByUserName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该用户名已经存在!");
            return map;
        }

        // 验证邮箱
        u = userMapper.selectByEmail(user.getEmail());

        if (u != null) {
            map.put("emailMsg", "该邮箱已经被注册!");
            return map;
        }
        user.setSalt(FriendsUtils.generateUUID().substring(0, 5)); // 加6为的随机盐
        user.setPassword(FriendsUtils.md5(user.getSalt() + user.getPassword()));
        user.setType(0);// 普通用户
        user.setStatus(0); // 未激活
        user.setTel("0");
        user.setMarriage(0); // 默认设置为未婚
        user.setHobby(""); // 默认设置爱好为空
        user.setLocation(""); // 默认设置地理位置为空
        user.setActivationCode(FriendsUtils.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",
                new Random().nextInt(1000))); // 设置随机的头像
        user.setCreateTime(new Date());
        user.setAge(0);// 默认设置年龄为0
        userMapper.insertUser(user);

        // 注册时发送激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // 激活链接的格式： http://localhost:9999/friends/activation/userId/code
        String url = domain + contextPath +
                "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);
        return map;
    }

    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT; // 该账号已经激活
        } else if (user.getActivationCode().equals(code)) {
            // 激活账号
            userMapper.updateUserStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        } else {
            // 激活失败
            return ACTIVATION_FAILURE;
        }
    }


    /**
     * 用户登录业务
     *
     * @param username       用户名
     * @param password       密码 用户输入未加密的原始密码
     * @param expiredSeconds 过期时间
     * @return
     */
    public Map<String, Object> login(String username, String password, long expiredSeconds) {
        Map<String, Object> map = new HashMap<>();
        // 验证username
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "用户名不能为空!");
            return map;
        }

        // 验证密码
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }

        // 验证账号的有效性
        User user = userMapper.selectByUserName(username);
        if (user == null) {
            map.put("usernameMsg", "该用户名不存在!");
            return map;
        }

        //验证账号状态
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号尚未激活!");
            return map;
        }

        // 验证密码
        password = FriendsUtils.md5(user.getSalt() + password);
        if (!(user.getPassword().equals(password))) {
            System.out.println((user.getPassword().equals(password)));
            map.put("passwordMsg", "密码错误");
            return map;
        }

        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setStatus(0);
        loginTicket.setTicket(FriendsUtils.generateUUID());
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        // 存入登录凭证
        loginTicketMapper.insertLoginTicket(loginTicket);

        // 成功 --> 将登录凭证返回
        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    /**
     * 退出功能
     *
     * @param ticket
     */
    public void loginOut(String ticket) {
        loginTicketMapper.updateLoginTicket(ticket, 1); // 将登录状态设置为无效
    }

    public Map<String, Object> updateUserPassword(int userId, String oldPassword, String newPassword, String prePassword) {
        Map<String, Object> map = new HashMap<>();
        User user = userMapper.selectById(userId);
        String md5OldPassword = FriendsUtils.md5(user.getSalt() + oldPassword); // 旧密码获得
        // 比较旧密码与数据库中的密码是否一致
        if (!user.getPassword().equals(md5OldPassword) || StringUtils.isBlank(oldPassword)) {
            map.put("oldPasswordMsg", "原始密码错误，请重新输入");
            return map;
        }

        if (StringUtils.isBlank(newPassword)) {
            map.put("newPasswordMsg", "请输入新密码!");
            return map;
        }

        if (StringUtils.isBlank(prePassword)) {
            map.put("prePasswordMsg", "请输入重复密码!");
            return map;
        }

        if (!newPassword.equals(prePassword)) {
            map.put("prePasswordMsg", "两次输入的密码不一致!");
            return map;
        }

        userMapper.updateUserPassword(userId, FriendsUtils.md5(
                user.getSalt() + newPassword));
        return map;
    }
}
