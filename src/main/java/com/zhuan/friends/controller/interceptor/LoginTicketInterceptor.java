package com.zhuan.friends.controller.interceptor;

import com.zhuan.friends.entity.LoginTicket;
import com.zhuan.friends.entity.User;
import com.zhuan.friends.service.UserService;
import com.zhuan.friends.utils.CookieUtils;
import com.zhuan.friends.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String ticket = CookieUtils.getValue(request, "ticket");

        if (ticket != null) { // 如果ticket
            LoginTicket loginTicket = userService.selectLoginTicketByTicket(ticket); // 查询登录凭证
            // 是否有效或者 是否过期 是否为 null
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                User user = userService.selectById(loginTicket.getUserId());
                // 需要将user对象暂存 但是我们该存在哪里呢 这里需要考虑的就是 这是一个并发的场景 可能同时有多个浏览器访问一个服务器。
                // 服务器在处理浏览器请求的时候需要考虑到多线程的情况 在并发的情况下可能导致冲突。
                // 需要考虑线程得隔离
                // 本次请求持有的用户
                hostHolder.setUsers(user);
            }
        }

        return true;
    }

    /**
     * 在模板之前调用
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        hostHolder.clear(); // 清理hostHolder中的数据
    }
}
