package com.zhuan.friends.controller;

import com.zhuan.friends.annotation.LoginRequired;
import com.zhuan.friends.dao.LoginTicketMapper;
import com.zhuan.friends.entity.Activity;
import com.zhuan.friends.entity.Page;
import com.zhuan.friends.entity.User;
import com.zhuan.friends.entity.UserCase;
import com.zhuan.friends.service.ActivityService;
import com.zhuan.friends.service.LikeService;
import com.zhuan.friends.service.UserCaseService;
import com.zhuan.friends.service.UserService;
import com.zhuan.friends.utils.FriendsUtils;
import com.zhuan.friends.utils.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.jws.WebParam;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${friends.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${friends.path.upload}")
    private String uploadPath;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private UserCaseService userCaseService; // 判断好友关系


    /**
     * 获取完善个人信息页面
     *
     * @return
     */
    @LoginRequired // 标识需要登录才能访问
    @RequestMapping(path = "/myself", method = RequestMethod.GET)
    public String getMyselfPage() {
        return "site/myself";
    }

    /**
     * 个人主页详情页面
     *
     * @return
     */
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfile(@PathVariable("userId") int userId, Model model, Page page) {
        User user = userService.selectById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }
        model.addAttribute("user", user);

        // 点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);
        User loginUser = hostHolder.getUser();
        UserCase userCase = null;
        if (loginUser != null) { // 判断当前用户是否登录
            // 判断好友关系
            String conversationId = getConversationId(user.getId(), loginUser.getId());
            userCase = userCaseService.selectUserCaseByConversationId(conversationId);
        }

        model.addAttribute("userCase", userCase);// 前端判断是否为空 如果为空表示未添加好友

        page.setLimit(5);
        page.setRows(activityService.selectActivityRows(userId));
        page.setPath("/user/profile/" + userId);
        List<Activity> activities = activityService.selectActivitys(userId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> activityList = new ArrayList<>();
        if (activities != null) {
            for (Activity activity : activities) {
                Map<String, Object> map = new HashMap<>();
                map.put("activity", activity);
                activityList.add(map);
            }
        }
        model.addAttribute("activityList", activityList);
        model.addAttribute("loginUser", loginUser);
        return "site/profile";
    }

    private String getConversationId(int userId, int loginUserId) {
        if (userId < loginUserId) {
            return userId + "_" + loginUserId;
        } else {
            return loginUserId + "_" + userId;
        }
    }


    @RequestMapping(path = "/luckyUser", method = RequestMethod.GET)
    public String luckyUserList(Model model, Page page) {
        User loginUser = hostHolder.getUser();
        if (loginUser == null) {
            throw new RuntimeException("您还未登录请先登录!");
        }

        page.setRows(userService.selectLuckyUserRows(loginUser.getUsername(), loginUser.getLocation(), loginUser.getGender()));
        page.setPath("/user/luckyUser");
        page.setLimit(5);

        List<User> luckyUsers = userService.selectLuckyUser(loginUser.getUsername(),
                loginUser.getLocation(),
                loginUser.getGender(),
                page.getOffset(),
                page.getLimit());
        model.addAttribute("luckyUsers", luckyUsers);
        return "site/lucky";
    }


    /**
     * @param headerImage 这里如果上传的是多个图片可以声明一个数组
     * @param username
     * @param age
     * @param location
     * @param marriage
     * @param tel
     * @param hobby
     * @param model
     * @return
     */
    @LoginRequired
    @RequestMapping(path = "/myself", method = RequestMethod.POST)
    public String updateMyself(MultipartFile headerImage, String username, Integer age,
                               String location,
                               Integer marriage,
                               String tel,
                               String hobby, Model model, Integer gender) {
        String headerUrl = null;
        if (StringUtils.isBlank(headerImage.toString())) {
            model.addAttribute("headerError", "您还没有选择图片!");
            return "site/myself";
        }

        if (StringUtils.isBlank(username)) {
            model.addAttribute("usernameError", "请填写用户名!");
            return "site/myself";
        }


        if (age == null) {
            model.addAttribute("ageError", "请填写年龄!");
            return "site/myself";
        }

        if (StringUtils.isBlank(location)) {
            model.addAttribute("locationError", "请填写地区!");
            return "site/myself";
        }

        if (marriage == null) {
            model.addAttribute("marriageError", "请填写婚姻状态!");
            return "site/myself";
        }

        if (StringUtils.isBlank(tel)) {
            model.addAttribute("telError", "请填写电话号码!");
            return "site/myself";
        }

        if (StringUtils.isBlank(hobby)) {
            model.addAttribute("hobbyError", "完善爱好能更快找到有缘人哦!");
            return "site/myself";
        }

        User loginUser = hostHolder.getUser();
        String filename = headerImage.getOriginalFilename(); // 原始文件名
        if (!StringUtils.isBlank(filename)) { // 如果原始文件名为空 表示用户为更换图片
            String suffix = filename.substring(filename.lastIndexOf(".")); // 截取后缀
            if (StringUtils.isBlank(suffix)) {
                model.addAttribute("headerError", "您上传的文件格式不对!");
                return "site/myself";
            }
            // 生成随机文件名称
            filename = FriendsUtils.generateUUID() + suffix;
            // 确定文件上传路径
            File dest = new File(uploadPath + "/" + filename);
            //判断文件夹是否存在
            if (!dest.exists()) {
                dest.mkdirs(); // 创建目录
            }
            try {
                headerImage.transferTo(dest);
            } catch (IOException e) {
                logger.error("上传文件失败!", e.getMessage());
                // 抛出异常打断程序
                throw new RuntimeException("上传文件失败,服务器发生异常", e);
            }
            // 拼接头像访问路径 // http://localhost:9999/friends/user/header/xxx.png
            headerUrl = domain + contextPath + "/user/header/" + filename;
        } else {
            // 拼接头像访问路径 // http://localhost:9999/friends/user/header/xxx.png
            headerUrl = loginUser.getHeaderUrl(); // 如果用户没有选择图片我们还是使用原来的图片
        }


        if (loginUser != null) {
            // 如果没有错误 更新用户信息
            userService.updateUserHeader(loginUser.getId(), headerUrl, username, age, location, marriage, tel, hobby, gender);
            model.addAttribute("loginUser", loginUser);
        }
        return "redirect:/sports";
    }

    /**
     * 获取磁盘中的头像，使用response将其输出到页面
     *
     * @param filename
     * @param response
     */
    @RequestMapping(path = "/header/{filename}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response) {
        filename = uploadPath + "/" + filename;
        // 获取文件后缀
        String suffix = filename.substring(filename.lastIndexOf("."));
        // 设置响应的类型
        response.setContentType("image/" + suffix);
        try (
                OutputStream os = response.getOutputStream();
                FileInputStream fis = new FileInputStream(filename); // 这里申明的变量会自动的在finally中关闭
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取文件失败 : " + e.getMessage());
        }
    }


    /**
     * 请求所有激活的最新会员信息
     *
     * @param model
     * @param page
     * @return
     */
    @RequestMapping(path = "/userList", method = RequestMethod.GET)
    public String getVipPage(Model model, Page page) {
        page.setLimit(8);
        page.setRows(userService.selectUserRows());
        page.setPath("/user/userList");
        List<User> users = userService.selectAll(page.getOffset(), page.getLimit());
        List<Map<String, Object>> userList = new ArrayList<>();
        if (users != null) {
            for (User user : users) {
                Map<String, Object> map = new HashMap<>();
                map.put("user", user);
                userList.add(map);
            }
        }
        model.addAttribute("userList", userList);
        return "site/vip";
    }


    @RequestMapping(path = "/updatePassword", method = RequestMethod.POST)
    public String updatePassword(@CookieValue("ticket") String ticket, String oldPassword, String newPassword, String prePassword, Model model) {
        User user = hostHolder.getUser();
        Map<String, Object> map = userService.updateUserPassword(user.getId(), oldPassword, newPassword, prePassword);
        if (map.isEmpty()) { // 这个为空表示登录成功
            userService.loginOut(ticket); // 退出登录重定向到登录页面
            return "redirect:/login";
        } else {
            model.addAttribute("oldPasswordMsg", map.get("oldPasswordMsg"));
            model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));
            model.addAttribute("prePasswordMsg", map.get("prePasswordMsg"));
            model.addAttribute("user", user);
            return "site/profile";
        }
    }
}
