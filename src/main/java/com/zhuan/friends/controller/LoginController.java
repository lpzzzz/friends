package com.zhuan.friends.controller;

import com.zhuan.friends.entity.User;
import com.zhuan.friends.service.UserService;
import com.zhuan.friends.utils.FriendsConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class LoginController implements FriendsConstant {

    @Autowired
    private UserService userService;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        return "site/register";
    }


    /**
     * 请求登录页面
     *
     * @return
     */
    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "site/index";
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) { // 如果没有任何错误信息表示注册成功!
            model.addAttribute("msg",
                    "注册成功，我们已经向您所填写的邮箱发送了一封邮件，请尽快前往激活!");
            model.addAttribute("target", "/sports"); // 注册成功跳转到活动页面
            return "site/operate-result"; // 操作结果页面
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "site/register";// 如果注册失败回到注册页面
        }
    }

    /**
     * 激活账号的请求 需要获取链接中的用户id和激活码
     *
     * @return
     */
    //激活链接的格式： http://localhost:9999/friends/activation/userId/code
    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int activationStatus = userService.activation(userId, code);
        if (activationStatus == ACTIVATION_SUCCESS) {
            // 激活成功
            model.addAttribute("msg", "激活成功,请登录使用!");
            model.addAttribute("target", "/login"); // 跳转到登录页面
        } else if (activationStatus == ACTIVATION_REPEAT) {
            // 重复激活
            model.addAttribute("msg", "该账号已激活,请勿重复操作!");
            model.addAttribute("target", "/sports");
        } else {
            // 重复激活
            model.addAttribute("msg", "错误的验证码,激活失败!");
            model.addAttribute("target", "/sports");
        }

        return "site/operate-result";
    }

    /**
     * 实现登录功能 仅仅对密码 和 用户名进行验证
     *
     * @param username
     * @param password
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String username, String password,  // 如果我们的参数不是一个实体 SpringMVC不会将我们的值自动的装配到Model中
                        // 但是我们可以认为的加入 或者 使用另一种方式 因为这几个参数是存在于request对象中的
                        HttpServletResponse response, Model model) {
        // 定义过期时间常量
        Map<String, Object> map = userService.login(username, password, EXPIRED_SECONDS);
        if (map.containsKey("ticket")) { //如果该键存在 表示登录成功 设置值成功!
            // 设置Cookie值
            Cookie cookie = new Cookie("ticket", (String) map.get("ticket"));
            cookie.setPath(contextPath);
            cookie.setMaxAge(EXPIRED_SECONDS);
            response.addCookie(cookie);
            User user = userService.selectUserByUsername(username); // 根据用户名查询当前用户 判断他的信息是否需要完善
            if ("".equals(user.getHobby()) || user.getAge() == 0 ||
                    "0".equals(user.getTel()) || "".equals(user.getLocation())) { // 如果满足上述条件需要完善个人信息 跳转到完善个人信息页面
                return "redirect:/user/myself";
            } else {
                return "redirect:/sports";
            }

        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "site/index";
        }
    }

    /**
     * 退出登录 在UserService 业务层修改登录凭证的状态 为 1 表示当前登录无效 并且跳转到网站首页 活动页面
     *
     * @param ticket
     * @return
     */
    @RequestMapping(path = "/loginOut", method = RequestMethod.GET)
    public String loginOut(@CookieValue("ticket") String ticket) {
        userService.loginOut(ticket);
        return "redirect:/login"; // 重定向到活动页面
    }
}
