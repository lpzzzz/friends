package com.zhuan.friends.controller;

import com.zhuan.friends.entity.Event;
import com.zhuan.friends.entity.Page;
import com.zhuan.friends.entity.User;
import com.zhuan.friends.entity.UserCase;
import com.zhuan.friends.event.EventProducer;
import com.zhuan.friends.service.LikeService;
import com.zhuan.friends.service.MessageService;
import com.zhuan.friends.service.UserCaseService;
import com.zhuan.friends.service.UserService;
import com.zhuan.friends.utils.FriendsConstant;
import com.zhuan.friends.utils.FriendsUtils;
import com.zhuan.friends.utils.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class UserCaseController implements FriendsConstant {

    @Autowired
    private UserCaseService userCaseService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private MessageService messageService;// 查询好友之间发送消息的数量

    /**
     * 爱情加油站
     *
     * @param model
     * @param page
     * @return
     */
    @RequestMapping(path = "/cheer/list", method = RequestMethod.GET)
    public String getCheerCase(Model model, Page page) {
        page.setPath("/cheer/list");
        page.setRows(userCaseService.selectUserCaseCount(0, 2)); // 2-创建了加油站
        page.setLimit(6);

        getUserCaseList(0, model, page, 2);
        // 查询出符合条件创建爱情加油站的好友 根据当前登录的用户id去查询
        User loginUser = hostHolder.getUser();
        if (loginUser != null) { // 只有当用户登录时才进行查询
            // 查询当前登录用户的所有好友关系
            List<UserCase> userCases = userCaseService.selectAllUserCase(loginUser.getId(), 1, 0, Integer.MAX_VALUE);
            List<Map<String, Object>> okUserList = new ArrayList<>(); // 封装 符合条件的好友关系
            // 筛选符合条件的好友关系返回
            if (userCases != null) {
                for (UserCase userCase : userCases) {
                    Map<String, Object> map = null;
                    int letterCount = messageService.selectLetterCount(userCase.getConversationId());
                    if (letterCount > 2) { // 如果他们之间的私信数量大于 10 条则符合条件
                        map = new HashMap<>();
                        //查询出 该还有关系中对应的 目标用户
                        int targetUserId = loginUser.getId() == userCase.getRequestId() ? userCase.getResponseId() : userCase.getRequestId();
                        // 根据目标用户id查询目标用户
                        User targetUser = userService.selectById(targetUserId);
                        map.put("targetUser", targetUser);
                        map.put("letterCount", letterCount);
                        okUserList.add(map);
                    }
                }
                model.addAttribute("okUserList", okUserList);
            }
        }

        return "site/cheerup"; // 返回加油站页面
    }

    /**
     * 成功案例列表
     *
     * @param model
     * @param page
     * @return
     */
    @RequestMapping(path = "/success/list", method = RequestMethod.GET)
    public String getSuccessCase(Model model, Page page) {
        page.setPath("/success/list");
        page.setRows(userCaseService.selectUserCaseCount(0, 3)); // 3-牵手成功
        page.setLimit(6);

        getUserCaseList(0, model, page, 3);

        return "site/event"; // 返回加油站页面
    }

    /**
     * 爱情加油站详情
     *
     * @param userCaseId
     * @param model
     * @return
     */
    @RequestMapping(path = "/cheer/detail/{userCaseId}", method = RequestMethod.GET)
    public String getCheerDetail(@PathVariable("userCaseId") int userCaseId, Model model) {
        UserCase userCase = userCaseService.selectUserCaseById(userCaseId);
        model.addAttribute("userCase", userCase);
        // 处理创建加油站的时间距离今天转换为天数 返回
        int days = (int) ((userCase.getCheerTime().getTime() - userCase.getCreateTime().getTime()) / 60 / 60 / 1000 / 24); // 转换为天
        model.addAttribute("days", days);
        User requestUser = userService.selectById(userCase.getRequestId());
        User responseUser = userService.selectById(userCase.getResponseId());

        if (requestUser.getCreateTime().getTime() > responseUser.getCreateTime().getTime()) { // 将时间在后面的用户进行排序
            model.addAttribute("requestUser", responseUser);
            model.addAttribute("responseUser", requestUser);
        } else {
            model.addAttribute("requestUser", requestUser);
            model.addAttribute("responseUser", responseUser);
        }

        // 判断两个用户来到平台时间较早的
        if (requestUser.getCreateTime().getTime() < responseUser.getCreateTime().getTime()) {
            model.addAttribute("startTime", requestUser.getCreateTime());
        } else {
            model.addAttribute("startTime", responseUser.getCreateTime());
        }


        return "site/cheer-detail";
    }


    /**
     * 封装爱情加油站和成功案例中的重复代码 方便共用一段代码
     *
     * @param model
     * @param page
     * @param status
     * @param userId
     */
    private void getUserCaseList(int userId, Model model, Page page, int status) {
        List<UserCase> userCases = userCaseService.selectAllUserCase(userId, status, page.getOffset(), page.getLimit());
        List<Map<String, Object>> userCaseList = new ArrayList<>();
        if (userCases != null) {
            for (UserCase userCase : userCases) {
                Map<String, Object> userCaseMap = new HashMap<>();
                userCaseMap.put("userCase", userCase);
                // 进度条处理 使用分数 进行处理 返回处理后的百分比
                String score = getScore(userCase.getScore()) + "%";
                userCaseMap.put("score", score);

                // 判断分数一旦满 且 当前状态为2 就设置状态为 3 成功
                if (userCase.getStatus() == 2 && getScore(userCase.getScore()) >= 100) {
                    userCaseService.updateUserCaseStatus(userCase.getId(), 3, null, new Date(), userCase.getContent());
                }

                // 进度条分数处理
                System.out.println("进度条分数:" + score);
                userCaseMap.put("requestUser", userService.selectById(userCase.getRequestId()));
                userCaseMap.put("responseUser", userService.selectById(userCase.getResponseId()));
                // 点赞数量
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_CHEERUP, userCase.getId());
                userCaseMap.put("likeCount", likeCount);
                // 点赞状态
                int likeStatus = hostHolder.getUser() == null ? 0
                        : likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_CHEERUP, userCase.getId());
                userCaseMap.put("likeStatus", likeStatus);
                userCaseList.add(userCaseMap);
            }
        }

        model.addAttribute("userCaseList", userCaseList);
    }


    private double getScore(double score) {
        return Math.ceil((score * 100)) / 10;
    }

    /**
     * 添加用户关系 ： 请求添加好友
     *
     * @param responseId
     * @param content
     * @return
     */
    @RequestMapping(path = "/userCase/addUserCase", method = RequestMethod.POST)
    @ResponseBody
    public String addUserCase(int responseId, String content) {
        User loginUser = hostHolder.getUser(); // 当前登录用户

        if (loginUser == null) {
            throw new RuntimeException("您还未登录,请先进行登录!");
        }

        // 用户关系
        UserCase userCase = new UserCase();
        userCase.setRequestId(loginUser.getId());
        userCase.setResponseId(responseId);
        userCase.setContent(content);
        userCase.setScore(0.0);
        userCase.setCreateTime(new Date());
        userCase.setStatus(0);
        // 判断ConversationId
        if (loginUser.getId() < responseId) {
            userCase.setConversationId(loginUser.getId() + "_" + responseId);
        } else {
            userCase.setConversationId(responseId + "_" + loginUser.getId());
        }
        userCaseService.insertUserCase(userCase); // 添加好友关系

        // 发送一个消息
        Event event = new Event()
                .setTopic(TOPIC_ADD_USER_CASE)
                .setEntityUserId(responseId)
                .setUserId(userCase.getRequestId())
                .setEntityId(userCase.getId())
                .setEntityType(ENTITY_ADD_USER_CASE)
                .setData("requestContent", userCase.getContent());
        eventProducer.fireEvent(event);
        return FriendsUtils.getJSONString(0, "发送成功,请等待对方同意!");
    }


    @RequestMapping(path = "/userCase/setUserCase", method = RequestMethod.POST)
    @ResponseBody
    public String setUserCase(int userCaseId) { // 只处理接接受的状态
        // 根据用户关系 id查询用户关系
        UserCase userCase = userCaseService.selectUserCaseById(userCaseId);
        if (userCase.getStatus() == 0) {
            userCaseService.updateUserCaseStatus(userCaseId, 1, null, null, userCase.getContent()); // 接受修改状态为普通好友关系
            return FriendsUtils.getJSONString(0, "你已接收该好友的请求!");
        } else {
            return FriendsUtils.getJSONString(1, "你已经接收了该好友的请求，请勿重复操作!");
        }
    }

    /**
     * 创建爱情加油站
     *
     * @param responseId
     * @param sendWord
     * @return
     */
    @RequestMapping(path = "/userCase/addUserCaseCheer", method = RequestMethod.POST)
    @ResponseBody
    public String addUserCaseCheer(int responseId, String sendWord) {
        User loginUser = hostHolder.getUser();

        if (loginUser == null) {
            throw new RuntimeException("你还未登录,请先登录!");
        }

        // 判断当前登录用户是否已经和其他人已经建立了爱情加油站关系 如果是 则不能创建
        List<UserCase> loginUserCases = userCaseService.selectAllUserCase(loginUser.getId(), 1, 0, Integer.MAX_VALUE); // 登录用户的好友关系
        // 查询请求的用户的好友关系
        List<UserCase> responseUserCases = userCaseService.selectAllUserCase(responseId, 1, 0, Integer.MAX_VALUE);

        if (loginUserCases != null) {
            for (UserCase loginUserCase : loginUserCases) {
                if (loginUserCase.getStatus() == 2 || loginUserCase.getStatus() == 3) {
                    return FriendsUtils.getJSONString(1, "你已经和其他用户建立了关系,不能脚踏多条船哦!");
                }
            }
        }

        if (responseUserCases != null) {
            for (UserCase responseUserCase : responseUserCases) {
                if (responseUserCase.getStatus() == 2 || responseUserCase.getStatus() == 3) {
                    return FriendsUtils.getJSONString(1, "你来迟了,你请求的用户已经和其他人建立了关系!");
                }
            }
        }


        // 发送请求消息给给被请求的用户 进行确认
        // 判断请求者是否输入寄语
        if (StringUtils.isBlank(sendWord)) {
            return FriendsUtils.getJSONString(1, "这是一个值得纪念的时刻,请输入寄语!");
        }


        Event event = new Event()
                .setTopic(TOPIC_ADD_USER_CASE)
                .setUserId(loginUser.getId())
                .setEntityUserId(responseId)
                .setEntityId(responseId)
                .setEntityType(ENTITY_ADD_USER_CASE_CHEER)
                .setData("requestContent", sendWord);

        eventProducer.fireEvent(event); // 触发事件

        return FriendsUtils.getJSONString(0, "已将请求发送给对方请等待对方确认!");
    }


    /**
     * 响应者接受创建加油站请求
     *
     * @param requestId
     * @param requestContent
     * @return
     */
    @RequestMapping(path = "/userCase/addUserCaseCheerAccept", method = RequestMethod.POST)
    @ResponseBody
    public String addUserCaseCheerAccept(int requestId, String requestContent) {
        User loginUser = hostHolder.getUser();
        if (loginUser == null) {
            throw new RuntimeException("您还未登录请先登录之后再进行操作!");
        }
        //判断 此时请求的用户是否已经创建了加油站
        List<UserCase> requestUserCases = userCaseService.selectAllUserCase(requestId, 1, 0, Integer.MAX_VALUE);
        if (requestUserCases != null) {
            for (UserCase requestUserCase : requestUserCases) {
                if (requestUserCase.getStatus() == 2 || requestUserCase.getStatus() == 3) {
                    return FriendsUtils.getJSONString(1, "你错过了,该用户已经和另一个人创建了加油站!");
                }
            }
        }
        // 拼接 当前用户 和 请求用户的id 之后再查询 到两者关系的id
        String conversationId = getConversationId(loginUser.getId(), requestId);
        // 查询该好友关系
        UserCase userCase = userCaseService.selectUserCaseByConversationId(conversationId);

        // 判断此时好友关系 状态
        if (userCase.getStatus() == 2 || userCase.getStatus() == 3) {
            return FriendsUtils.getJSONString(1, "你们加油站已经建立,请勿重复操作!");
        }

        // 更新此时好友关系状态 为加油站状态
        userCaseService.updateUserCaseStatus(userCase.getId(), 2, new Date(), null, requestContent);
        return FriendsUtils.getJSONString(0, "你已接受加油站创建请求!");
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
