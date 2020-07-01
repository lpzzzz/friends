package com.zhuan.friends.controller;

import com.zhuan.friends.entity.Activity;
import com.zhuan.friends.entity.Comment;
import com.zhuan.friends.entity.Page;
import com.zhuan.friends.entity.User;
import com.zhuan.friends.service.ActivityService;
import com.zhuan.friends.service.CommentService;
import com.zhuan.friends.service.LikeService;
import com.zhuan.friends.service.UserService;
import com.zhuan.friends.utils.FriendsConstant;
import com.zhuan.friends.utils.FriendsUtils;
import com.zhuan.friends.utils.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/activity")
public class ActivityController implements FriendsConstant {
    @Autowired
    private ActivityService activityService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService; // 获取点赞数量 以及点赞状态

    /**
     * 创建活动 请求
     *
     * @param endDate
     * @param title
     * @param content
     * @return
     */
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addActivity(@DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate, String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return FriendsUtils.getJSONString(403, "您还未登录哦!");
        }

        System.out.println("活动的结束时间 : " + endDate);

        if (System.currentTimeMillis() > endDate.getTime()) { // 如果输入的时间 小于当前时间 提示重新输入
            return FriendsUtils.getJSONString(500, "截止时间不能小于当前时间!");
        }

        if (StringUtils.isBlank(title)) {
            return FriendsUtils.getJSONString(500, "标题不能为空!");
        }

        Activity activity = new Activity();
        activity.setUserId(user.getId());
        activity.setTitle(title);
        activity.setContent(content);
        activity.setCommentCount(0);
        activity.setScore(0);
        activity.setType(0);
        activity.setStatus(0);
        activity.setCreateTime(new Date());
        activity.setExpiredTime(endDate);

        activityService.insertActivity(activity);

        // 报错情况将来统一处理
        return FriendsUtils.getJSONString(0, "发布成功!");
    }

    /**
     * 根据id查询活动详情
     *
     * @param activityId
     * @param model
     * @return
     */
    @RequestMapping(path = "/detail/{activityId}", method = RequestMethod.GET)
    public String activityDetail(@PathVariable("activityId") int activityId, Model model, Page page) {
        User loginUser = hostHolder.getUser();
        model.addAttribute("loginUser", loginUser);
        List<User> userList = new ArrayList<>();
        Activity activity = activityService.selectActivityById(activityId);
        model.addAttribute("activity", activity);
        // 还需查询活动关联的用户信息
        User user = userService.selectById(activity.getUserId());
        model.addAttribute("user", user);

        int commentCount = commentService.selectCommentCount(ENTITY_TYPE_ACTIVITY, activityId);
        model.addAttribute("commentCount", commentCount);
        //设置分页显示条件
        page.setLimit(4);
        page.setRows(commentCount);
        page.setPath("/activity/detail/" + activityId);

        //评论 ： 对活动的评论
        //回复 ：对评论的评论
        List<Comment> comments = commentService.selectComments
                (ENTITY_TYPE_ACTIVITY, activityId, page.getOffset(), page.getLimit());
        // 点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_ACTIVITY, activityId);
        model.addAttribute("likeCount", likeCount);
        // 点赞状态 如果用户为登录显示的是未点赞状态
        int likeStatus = hostHolder.getUser() == null ? 0
                : likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_ACTIVITY, activityId);
        model.addAttribute("likeStatus", likeStatus);
        List<Map<String, Object>> commentVoList = new ArrayList<>();

        if (comments != null) {
            for (Comment comment : comments) {
                Map<String, Object> commentVo = new HashMap<>();
                commentVo.put("comment", comment);
                // 将该评论对应的用户查询之后封装到map中
                commentVo.put("user", userService.selectById(comment.getUserId()));
                // 查询回复列表
                List<Comment> replyeList = commentService.selectComments
                        (ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);// 查询所有的不分页
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyeList != null) {
                    for (Comment reply : replyeList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        replyVo.put("reply", reply);
                        User replyUser = userService.selectById(reply.getUserId());
                        replyVo.put("user", replyUser);
                        // 获取回复的目标 如果回复目标id为0 表示没有回复目标
                        User targetUser = reply.getTargetId() == 0 ? null : userService.selectById(reply.getTargetId());
                        // 将目标用户存入到map中
                        replyVo.put("targetUser", targetUser);
                        // 将回复用户不重复的添加到map中
                        System.out.println("回复的人:" + replyUser);
                        userList.add(replyUser);
                        replyVo.put("userList", removeUser(userList));
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replyVoList", replyVoList);
                commentVo.put("userList", removeUser(userList));
                // 获取回复的数量
                int replyCount = commentService.selectCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments", commentVoList);
        return "site/x1";
    }

    private List<User> removeUser(List<User> oldUserList) {
        if (!oldUserList.isEmpty()) {
            for (int i = 0; i < oldUserList.size(); i++) {
                for (int j = i + 1; j < oldUserList.size(); j++) {
                    if (oldUserList.get(i).getId() == oldUserList.get(j).getId()) {
                        oldUserList.remove(j);
                    }
                }
            }
        }
        return oldUserList;
    }

}
