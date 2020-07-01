package com.zhuan.friends.controller;

import com.zhuan.friends.entity.Activity;
import com.zhuan.friends.entity.Comment;
import com.zhuan.friends.entity.Event;
import com.zhuan.friends.event.EventProducer;
import com.zhuan.friends.service.ActivityService;
import com.zhuan.friends.service.CommentService;
import com.zhuan.friends.utils.FriendsConstant;
import com.zhuan.friends.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements FriendsConstant {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private EventProducer eventProducer;

    /**
     * 添加活动评论
     *
     * @param activityId
     * @param comment
     * @return
     */
    @RequestMapping(path = "/add/{activityId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("activityId") String activityId, Comment comment) {
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        comment.setTargetId(0);
        commentService.addComment(comment);

        // 触发评论事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setEntityId(comment.getEntityId())
                .setEntityType(comment.getEntityType())
                .setUserId(comment.getUserId())
                .setData("activityId", activityId);

        // 其中我们评论分为两种 一种是对帖子的评论 一种是对 评论的评论
        if (comment.getEntityType() == ENTITY_TYPE_ACTIVITY) { // 如果是对帖子的评论
            // 通过 评论的实体 id查询实体 在实体中查询 实体的拥有者
            Activity target = activityService.selectActivityById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        } else { // 如果评论的是评论
            // 通过 评论的实体 id查询实体 在实体中查询 实体的拥有者
            Comment target = commentService.selectCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        // 触发事件
        eventProducer.fireEvent(event);

        return "redirect:/activity/detail/" + activityId; // 添加完成评论重定向到活动详情
    }

}
