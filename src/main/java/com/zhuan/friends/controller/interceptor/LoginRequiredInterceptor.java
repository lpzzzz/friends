package com.zhuan.friends.controller.interceptor;

import com.zhuan.friends.annotation.LoginRequired;
import com.zhuan.friends.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 登录检查的拦截器
 */
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        if (handler instanceof HandlerMethod) { // 判断我们拦截的是否是方法
            // 如果是 我们将 Object类型的handler转换为 HandlerMethod
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod(); // 获取方法对象
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);// 获取该方法上的注解
            if (loginRequired != null && hostHolder.getUser() == null) { // 此判断成立说明 该用户未登录且访问了 需要登录之后才能访问的方法
                // 如果访问它则重定向到首页
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }

        return true;
    }
}
