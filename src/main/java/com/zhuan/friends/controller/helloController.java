package com.zhuan.friends.controller;

import com.zhuan.friends.utils.FriendsUtils;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/hello")
public class helloController {

    @RequestMapping("/test")
    @ResponseBody
    public String hello() {
        return "hello world !";
    }

    // Cookie示例 设置Cookie 是服务器将Cookie通过 响应头携带 Cookie信息到浏览器
    @RequestMapping(path = "/cookie/set", method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("code", FriendsUtils.generateUUID());
        // 设置cookie的最大生存时间
        cookie.setMaxAge(60 * 10);
        cookie.setPath("/friends/hello"); // 为该类的请求路径范围内
        response.addCookie(cookie);
        return "set Cookie";
    }

    // 获取Cookie 需要使用一个注解 获取Cookie cookie信息将获取到浏览器的请求头中。
    @RequestMapping(path = "/cookie/get", method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code) {
        System.out.println(code);
        return "get Cookie";
    }

    // Session 示例
    // 服务器设置Session后会响应一个Cookie 作为每一个浏览器的识别的唯一标识
    @RequestMapping(path = "/session/set", method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session) {
        session.setAttribute("id", 1);
        session.setAttribute("name", "张三");
        return "set Session";
    }

    // 浏览器请求Session需要发送cookie工服务器进行识别
    @RequestMapping(path = "/session/get", method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session) {
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }

    // AJAX 示例

    /**
     * 发送AJAX请求示例
     * @param name
     * @param age
     * @return
     */
    @RequestMapping(path = "/ajax" ,method = RequestMethod.POST)
    @ResponseBody
    public String sendAjax(String name , int age) {
        System.out.println(name);
        System.out.println(age);
        return FriendsUtils.getJSONString(0 , "操作成功!");
    }
}
