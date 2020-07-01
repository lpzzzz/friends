package com.zhuan.friends.service;

import com.zhuan.friends.dao.ActivityMapper;
import com.zhuan.friends.dao.UserMapper;
import com.zhuan.friends.entity.Activity;
import com.zhuan.friends.entity.User;
import com.zhuan.friends.utils.FriendsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

@Service
public class AlphaService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private TransactionTemplate transactionTemplate; // 用于创建编程式事务控制

    //REQUIRED A调用B 支持当前事务（A事务），如果不存在外部事务就创建新事务
    //REQUIRES_NEW 创建一个新的事务并且暂停当前事务（外部事务）
    //NESTED 如果当前存在事务（外部事务），则嵌套在该事务中执行 B在执行时有独立的提交回滚，否则就与REQUIRED一样

    /**
     * 声明式事务控制
     * isolation = Isolation.READ_COMMITTED  隔离级别
     * propagation = Propagation.REQUIRED 传播机制
     *
     * @return
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save() {
        // 新增用户
        User user = new User();
        user.setUsername("测试用户556");
        user.setAge(23);
        user.setEmail("12345670@qq.com");
        user.setCreateTime(new Date());
        user.setActivationCode(FriendsUtils.generateUUID());
        user.setHeaderUrl("http://images.nowcoder.com/head/556t.png");
        user.setLocation("云南昭通");
        user.setHobby("打篮球");
        user.setMarriage(0);
        user.setSalt(FriendsUtils.generateUUID().substring(0, 5));
        user.setPassword(FriendsUtils.md5(user.getSalt() + "123"));
        user.setTel("12345678910");
        user.setType(0);
        user.setStatus(0);
        userMapper.insertUser(user);

        // 新增活动
        Activity activity = new Activity();
        activity.setUserId(103);
        activity.setTitle("标题");
        activity.setContent("内容");
        activity.setCommentCount(0);
        activity.setType(0);
        activity.setScore(0.0);
        activity.setStatus(0);
        activity.setFaceImage("http://images.nowcoder.com/head/556t.png");
        activity.setCreateTime(new Date());
        activityMapper.insertActivity(activity);

//        Integer.valueOf("abc"); // 制造异常
        return "ok";
    }

    public Object save1() {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                // 新增用户
                User user = new User();
                user.setUsername("测试用户556");
                user.setAge(23);
                user.setEmail("12345670@qq.com");
                user.setCreateTime(new Date());
                user.setActivationCode(FriendsUtils.generateUUID());
                user.setHeaderUrl("http://images.nowcoder.com/head/556t.png");
                user.setLocation("云南昭通");
                user.setHobby("打篮球");
                user.setMarriage(0);
                user.setSalt(FriendsUtils.generateUUID().substring(0, 5));
                user.setPassword(FriendsUtils.md5(user.getSalt() + "123"));
                user.setTel("12345678910");
                user.setType(0);
                user.setStatus(0);
                userMapper.insertUser(user);

                // 新增活动
                Activity activity = new Activity();
                activity.setUserId(103);
                activity.setTitle("标题");
                activity.setContent("内容");
                activity.setCommentCount(0);
                activity.setType(0);
                activity.setScore(0.0);
                activity.setStatus(0);
                activity.setFaceImage("http://images.nowcoder.com/head/556t.png");
                activity.setCreateTime(new Date());
                activityMapper.insertActivity(activity);

//                Integer.valueOf("abc"); // 制造异常
                return "ok";
            }
        });
    }
}
