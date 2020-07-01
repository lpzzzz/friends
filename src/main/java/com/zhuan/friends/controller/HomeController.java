package com.zhuan.friends.controller;

import com.zhuan.friends.entity.Activity;
import com.zhuan.friends.entity.Message;
import com.zhuan.friends.entity.Page;
import com.zhuan.friends.entity.User;
import com.zhuan.friends.service.ActivityService;
import com.zhuan.friends.service.LikeService;
import com.zhuan.friends.service.MessageService;
import com.zhuan.friends.service.UserService;
import com.zhuan.friends.utils.FriendsConstant;
import com.zhuan.friends.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements FriendsConstant {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService; // 获取点赞数量

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/sports", method = RequestMethod.GET)
    public String getHome(Model model, Page page) {
        // SpringMVC 中方法调用之前SpringMVC会自动实例化Model 和 Page 而且会自动将Page注入给Model
        User loginUser = hostHolder.getUser();
        page.setRows(activityService.selectActivityRows(0));
        page.setLimit(12);
        page.setPath("/sports");
        List<Activity> activities = activityService.selectActivitys(0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> activityList = new ArrayList<>();
        if (activities != null) {
            for (Activity activity : activities) {
                Map<String, Object> map = new HashMap<>();
                map.put("activity", activity);
                User user = userService.selectById(activity.getUserId());
                map.put("user", user);
                // 点赞
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_ACTIVITY, activity.getId());
                map.put("likeCount", likeCount);
                activityList.add(map);
            }
        }
        // 查询会话列表
        model.addAttribute("activityList", activityList);
        return "sports";
    }


    /**
     * 错误页面请求处理
     *
     * @return
     */
    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public String error() {
        return "error/500";
    }
}
