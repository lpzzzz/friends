package com.zhuan.friends.controller;

import com.alibaba.fastjson.JSONObject;
import com.zhuan.friends.entity.Message;
import com.zhuan.friends.entity.Page;
import com.zhuan.friends.entity.User;
import com.zhuan.friends.entity.UserCase;
import com.zhuan.friends.service.MessageService;
import com.zhuan.friends.service.UserCaseService;
import com.zhuan.friends.service.UserService;
import com.zhuan.friends.utils.FriendsConstant;
import com.zhuan.friends.utils.FriendsUtils;
import com.zhuan.friends.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
public class MessageController implements FriendsConstant {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private UserCaseService userCaseService;


    /**
     * 消息列表
     *
     * @param model
     * @param page
     * @return
     */
    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page) {
        User loginUser = hostHolder.getUser();

        // 制造一个异常
//        int i = Integer.valueOf("adb");// 尝试将字母字符串转换为数字 这里一定会出现异常
        page.setLimit(10);
        page.setRows(messageService.selectConversationCount(loginUser.getId()));
        page.setPath("/letter/list");

        // 查询会话列表
        List<Message> conversations = messageService.selectConversations(loginUser.getId(), page.getOffset(), page.getLimit());// 查询最新的一条返回到首页
        letterList(model, loginUser, conversations);
        return "site/letter";
    }

    private void letterList(Model model, User loginUser, List<Message> conversations) {
        List<Map<String, Object>> letterConversationList = new ArrayList<>();
        Map<String, Object> map = null;
        if (conversations != null) {
            for (Message message : conversations) {
                map = new HashMap<>();
                map.put("conversation", message); // 将每个会话存入到集合
                map.put("unreadCount", messageService.selectUnReadLetter(loginUser.getId(), message.getConversationId()));
                map.put("letterCount", messageService.selectLetterCount(message.getConversationId()));
                // 查询私信列表用户
                int targetUserId = message.getFromId() == loginUser.getId() ? message.getToId() : message.getFromId();
                map.put("targetUser", userService.selectById(targetUserId));
                letterConversationList.add(map);
            }
        }
        model.addAttribute("letterConversationList", letterConversationList);
        System.out.println(letterConversationList);
        // 查询总的未读消息数量
        int unReadLetterCount = messageService.selectUnReadLetter(loginUser.getId(), null);
        model.addAttribute("unReadLetterCount", unReadLetterCount);
    }


    /**
     * 消息详情
     *
     * @param conversationId
     * @param model
     * @return
     */
    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId,
                                  Model model) {
        User loginUser = hostHolder.getUser();
        model.addAttribute("loginUser", loginUser);

        List<Message> letters = messageService.selectLetters(conversationId, 0, Integer.MAX_VALUE);// 查询所有数据
        List<Map<String, Object>> letterList = new ArrayList<>();

        if (letters != null) {
            for (Message letter : letters) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", letter);
                map.put("fromUser", userService.selectById(letter.getFromId()));
                letterList.add(map);
            }
        }
        model.addAttribute("letterList", letterList);

        // 查询发送消息的目标用户
        model.addAttribute("targetUser", getTargetUser(conversationId));
        model.addAttribute("sendUser", getSendUser(conversationId));
        System.out.println(getSendUser(conversationId));
        System.out.println(loginUser);
        // 将消息列表中未读的消息修改为已读 首先需要查询未读消息
        // 判断当前用户是否是接收者
        // 更改消息状态

        List<Integer> ids = getUnreadMessageIds(letters);
        if (!ids.isEmpty()) {
            messageService.updateMessageStatus(ids, 1);
        }

        // 查询会话列表
        List<Message> conversations = messageService.selectConversations(loginUser.getId(), 0, Integer.MAX_VALUE);
        letterList(model, loginUser, conversations);
        model.addAttribute("conversationId", conversationId); // 将其传回到页面作比较

        // 查询并返回好友列表
        List<UserCase> userCases = userCaseService.selectAllUserCase(loginUser.getId(), 1, 0, Integer.MAX_VALUE);
        List<Map<String, Object>> userCaseList = new ArrayList<>();

        if (userCases != null) {
            for (UserCase userCase : userCases) {
                // 封装用户信息
                Map<String, Object> userCaseMap = new HashMap<>();
                userCaseMap.put("userCase", userCase);
                // 查询目标用户
                int userId = userCase.getRequestId() == loginUser.getId() ? userCase.getResponseId() : userCase.getRequestId();
                userCaseMap.put("targetUser", userService.selectById(userId));
                userCaseList.add(userCaseMap);
            }
        }
        // 查询并返回好友数量
        int userCount = userCaseService.selectUserCaseCount(loginUser.getId(), 1);
        model.addAttribute("userCount", userCount);
        model.addAttribute("userCaseList", userCaseList);// 返回好友数据到列表
        return "site/letter-detail-ok";
    }

    /**
     * 获取私信详情中发送消息的用户
     *
     * @param conversationId
     * @return
     */
    private User getTargetUser(String conversationId) {
        String[] ids = conversationId.split("_"); // 将id进行拆分
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if (hostHolder.getUser().getId() == id0) {
            return userService.selectById(id1);
        } else {
            return userService.selectById(id0);
        }
    }

    private User getSendUser(String conversationId) {
        String[] ids = conversationId.split("_"); // 将id进行拆分
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        System.out.println(id0);
        System.out.println(id1);
        if (hostHolder.getUser().getId() == id0) {
            return userService.selectById(id0);
        } else {
            return userService.selectById(id1);
        }
    }


    /**
     * 获取全部消息中未读消息的id集合
     *
     * @return
     */
    private List<Integer> getUnreadMessageIds(List<Message> messages) {
        List<Integer> ids = new ArrayList<>();

        if (messages != null) {
            for (Message message : messages) {
                if (message.getToId() == hostHolder.getUser().getId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }


    /**
     * 发送消息表现层实现
     *
     * @param toName
     * @param content
     * @return
     */
    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content) {
        // 通过用户名 查询用户
        User targetUser = userService.selectUserByUsername(toName);
        if (targetUser == null) {
            return FriendsUtils.getJSONString(0, "该用户不存在");
        }

        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(targetUser.getId());
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setStatus(0);
        message.setCreateTime(new Date());
        messageService.addMessages(message);
        return FriendsUtils.getJSONString(0);
    }

    @RequestMapping(path = "/notice/list", method = RequestMethod.GET)
    public String getNoticeList(Model model) {
        User loginUser = hostHolder.getUser();
        if (loginUser == null) {
            throw new RuntimeException("请先进行登录!");
        }

        // 查询评论类通知
        Message commentMessage = messageService.getLatestNotice(loginUser.getId(), TOPIC_COMMENT);
        // 封装评论通知
        Map<String, Object> commentMessageVo = null;
        if (commentMessage != null) {
            commentMessageVo = new HashMap<>();
            commentMessageVo.put("commentMessage", commentMessage);
            // 将转义的消息 恢复
            String content = HtmlUtils.htmlUnescape(commentMessage.getContent());
            // 将 消息内容恢复为 对象
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            // 将map中的值取出放入到新的messageVo中
            commentMessageVo.put("user", userService.selectById((Integer) data.get("userId")));
            commentMessageVo.put("entityType", data.get("entityType"));
            commentMessageVo.put("entityId", data.get("entityId"));
            commentMessageVo.put("activityId", data.get("activityId"));

            // 消息的数量
            int count = messageService.selectNoticeCount(loginUser.getId(), TOPIC_COMMENT);
            commentMessageVo.put("count", count);

            // 查询未读消息数量
            int unReadNoticeCount = messageService.selectUnreadNoticeCount(loginUser.getId(), TOPIC_COMMENT);
            commentMessageVo.put("unReadNoticeCount", unReadNoticeCount);
        }
        model.addAttribute("commentMessageVo", commentMessageVo);


        // 查询点赞类通知
        Message likeMessage = messageService.getLatestNotice(loginUser.getId(), TOPIC_LIKE);
        // 封装评论通知
        Map<String, Object> likeMessageVo = null;
        if (likeMessage != null) {
            likeMessageVo = new HashMap<>();
            likeMessageVo.put("likeMessage", likeMessage);

            // 将转义的消息 恢复
            String content = HtmlUtils.htmlUnescape(likeMessage.getContent());
            // 将 消息内容恢复为 对象
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            // 将map中的值取出放入到新的messageVo中
            likeMessageVo.put("user", userService.selectById((Integer) data.get("userId")));
            likeMessageVo.put("entityType", data.get("entityType"));
            likeMessageVo.put("entityId", data.get("entityId"));
            likeMessageVo.put("likedId", data.get("likedId"));

            // 消息的数量
            int count = messageService.selectNoticeCount(loginUser.getId(), TOPIC_LIKE);
            likeMessageVo.put("count", count);

            // 查询未读消息数量
            int unReadNoticeCount = messageService.selectUnreadNoticeCount(loginUser.getId(), TOPIC_LIKE);
            likeMessageVo.put("unReadNoticeCount", unReadNoticeCount);
        }
        model.addAttribute("likeMessageVo", likeMessageVo);


        // 好友请求类通知
        Message addUserCaseMessage = messageService.getLatestNotice(loginUser.getId(), TOPIC_ADD_USER_CASE);
        Map<String, Object> addUserCaseMessageVo = null;
        if (addUserCaseMessage != null) {
            addUserCaseMessageVo = new HashMap<>();
            addUserCaseMessageVo.put("addUserCaseMessage", addUserCaseMessage);

            // 将消息的内容返回html转义回来
            String content = HtmlUtils.htmlUnescape(addUserCaseMessage.getContent());
            // 将转移后的消息内容转换为对象
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            // 将转换后的内容封装到新的 messageVo中
            addUserCaseMessageVo.put("user", userService.selectById((Integer) data.get("userId")));
            // 请求消息
            addUserCaseMessageVo.put("requestContent", data.get("requestContent"));
            addUserCaseMessageVo.put("entityType", data.get("entityType"));
            addUserCaseMessageVo.put("entityId", data.get("entityId"));

            // 消息的数量
            int count = messageService.selectNoticeCount(loginUser.getId(), TOPIC_ADD_USER_CASE);
            addUserCaseMessageVo.put("count", count);

            // 未读消息数量
            int unReadNoticeCount = messageService.selectUnreadNoticeCount(loginUser.getId(), TOPIC_ADD_USER_CASE);
            addUserCaseMessageVo.put("unReadNoticeCount", unReadNoticeCount);
        }
        model.addAttribute("addUserCaseMessageVo", addUserCaseMessageVo);

        // 查询全部未读通知数量
        int allUnReadNoticeCount = messageService.selectUnreadNoticeCount(loginUser.getId(), null);
        model.addAttribute("allUnReadNoticeCount", allUnReadNoticeCount);
        System.out.println(allUnReadNoticeCount);
        return "site/notice";
    }


    @RequestMapping(path = "/notice/detail/{topic}", method = RequestMethod.GET)
    public String noticeDetail(@PathVariable("topic") String topic, Model model, Page page) {
        User loginUser = hostHolder.getUser();

        if (loginUser == null) {
            throw new RuntimeException("您还未登录,请先登录!");
        }
        page.setLimit(5);
        page.setRows(messageService.selectNoticeCount(loginUser.getId(), topic));
        page.setPath("/notice/detail/" + topic);

        // 查询某个主题下的通知
        List<Message> notices = messageService.selectNotices(loginUser.getId(), topic, page.getOffset(), page.getLimit());

        // 聚合与message相关的其他信息
        List<Map<String, Object>> noticeList = new ArrayList<>();
        if (notices != null) {
            for (Message notice : notices) {
                Map<String, Object> map = new HashMap<>();
                // 通知
                map.put("notice", notice);

                // 通知内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user", userService.selectById((Integer) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                if (TOPIC_COMMENT.equals(topic)) { // 如果主题是评论
                    map.put("activityId", data.get("activityId"));
                } else if (TOPIC_LIKE.equals(topic)) { // 如果主题是点赞
                    map.put("likedId", data.get("likedId"));
                } else if (TOPIC_ADD_USER_CASE.equals(topic)) {
                    map.put("requestContent", data.get("requestContent"));
                    map.put("userId", data.get("userId"));
                }

                // 发送通知的作者

                noticeList.add(map);

            }
        }
        System.out.println(notices);
        model.addAttribute("noticeList", noticeList);
        // 设置为已读
        List<Integer> ids = getUnreadMessageIds(notices);
        if (!ids.isEmpty()) {
            messageService.updateMessageStatus(ids, 1);
        }
        return "site/notice-detail";
    }

}
