package com.zhuan.friends.controller;

import com.zhuan.friends.entity.Event;
import com.zhuan.friends.entity.User;
import com.zhuan.friends.entity.UserCase;
import com.zhuan.friends.event.EventProducer;
import com.zhuan.friends.service.LikeService;
import com.zhuan.friends.service.MessageService;
import com.zhuan.friends.service.UserCaseService;
import com.zhuan.friends.utils.FriendsConstant;
import com.zhuan.friends.utils.FriendsUtils;
import com.zhuan.friends.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements FriendsConstant {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private UserCaseService userCaseService;

    @Autowired
    private MessageService messageService;


    /**
     * @param entityType
     * @param entityId
     * @param entityUserId
     * @param cheerUserId  被加油实体的另一个用户的id
     * @param likedId      被点赞实体的id
     * @return
     */
    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int cheerUserId, int likedId) {
        User loginUser = hostHolder.getUser();
        // 点赞
        likeService.like(loginUser.getId(), entityType, entityId, entityUserId, cheerUserId);

        // 获取赞的数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        int likeStatus = likeService.findEntityLikeStatus(loginUser.getId(), entityType, entityId);
        // 将两个数据放到Map中返回
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);


        // 判断只有在点赞的时候才发送通知
        if (likeStatus == 1) {
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(loginUser.getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("likedId", likedId);
            // 判断如果是对爱情加油站点赞 需要通知的还包含另外一个用户
            if (cheerUserId != 0) { // 此时还需要发送一条消息给 被加油实体的另一个用户
                event.setData("cheerUpId", cheerUserId);
                Event cheeredEvent = new Event()
                        .setTopic(TOPIC_LIKE)
                        .setUserId(loginUser.getId())
                        .setEntityType(entityType)
                        .setEntityId(entityId)
                        .setEntityUserId(cheerUserId)
                        .setData("likedId", likedId)
                        .setData("cheerUpId", cheerUserId);
                eventProducer.fireEvent(cheeredEvent);
            }
            // 触发事件
            eventProducer.fireEvent(event);

            // 计算当前加油站的分数 更新到 数据库中 likedId 就是被赞实体的id
            // 实体的分数和 聊天数量 点赞数量 好友关系建立到至今的时间差有关
            String conversationId = getConversationId(entityUserId, cheerUserId);
            // 加油站两个用户的 聊天记录数
            int letterCount = messageService.selectLetterCount(conversationId);
            UserCase userCase = userCaseService.selectUserCaseById(likedId);
            // 两者相识的时间
            long userCaseTime = (new Date().getTime() - userCase.getCreateTime().getTime());
            // 计算权重
            double w = (likeCount * 5) + (letterCount * 10000) + (userCaseTime / (1000 * 3600 * 24));
            //计算分数
            double score = Math.log10(Math.max(w, 1)); // 对分数向上取整
            // 将分数更新到数据库
            userCaseService.updateUserCaseScore(likedId, score);
            System.out.println("分数" + score);
        }

        return FriendsUtils.getJSONString(0, null, map);
    }

    /**
     * 拼接 出 conversationId
     *
     * @param loginUserId
     * @param responseId
     * @return
     */
    private String getConversationId(int loginUserId, int responseId) {
        if (loginUserId < responseId) {
            return loginUserId + "_" + responseId;
        } else {
            return responseId + "_" + loginUserId;
        }
    }
}
