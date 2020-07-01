package com.zhuan.friends.utils;

import com.zhuan.friends.entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息 ， 用于代替session对象的
 */

@Component
public class HostHolder {

    ThreadLocal<User> users = new ThreadLocal<>();

    /**
     * 在线程中设置值
     *
     * @param user
     */
    public void setUsers(User user) {
        users.set(user);
    }

    /**
     * 获取线程中的值
     *
     * @return
     */
    public User getUser() {
        return users.get();
    }


    /**
     * 请求完成清理线程中的值
     */
    public void clear() {
        users.remove();
    }
}
