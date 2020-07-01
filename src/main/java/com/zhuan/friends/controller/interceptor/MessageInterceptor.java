package com.zhuan.friends.controller.interceptor;

import com.zhuan.friends.entity.Message;
import com.zhuan.friends.entity.User;
import com.zhuan.friends.service.MessageService;
import com.zhuan.friends.service.UserService;
import com.zhuan.friends.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理未读消息
 * 1. Controller调用之前
 * 2. 调用Controller之后模板之前 使用该方式处理 重写post方法
 * 3. 模板之后
 */
@Component
public class MessageInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;


    /**
     * 在调用Controller之后模板之前进行处理
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        User loginUser = hostHolder.getUser();
        // 判断用户是否登录
        if (loginUser != null && modelAndView != null) {
            // 查询未读消息数量
            int unReadLetterCount = messageService.selectUnReadLetter(loginUser.getId(), null);
            modelAndView.addObject("unReadLetterCount", unReadLetterCount);
            // 查询未读通知数量
            int unreadNoticeCount = messageService.selectUnreadNoticeCount(loginUser.getId(), null);
            modelAndView.addObject("unreadNoticeCount", unreadNoticeCount);
            modelAndView.addObject("allUnreadCount", unReadLetterCount + unreadNoticeCount);
            // 查询会话列表 查询最新的一条消息返回到模板
            List<Message> conversations = messageService.selectConversations(loginUser.getId(), 0, 1);// 查询最新的一条返回到首页
            List<Map<String, Object>> conversationList = new ArrayList<>();
            if (conversations != null) {
                for (Message conversation : conversations) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("conversation", conversation);
                    // 查询私信列表用户
                    int targetUserId = conversation.getFromId() == loginUser.getId() ? conversation.getToId() : conversation.getFromId();
                    map.put("targetUser", userService.selectById(targetUserId));
                    conversationList.add(map);
                }
            }
            modelAndView.addObject("conversationList", conversationList);
        }
    }
}
