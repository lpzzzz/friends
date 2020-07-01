package com.zhuan.friends.controller.advice;

import com.zhuan.friends.utils.FriendsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice(annotations = Controller.class) // 这里并不是拦截所有的请求 只需要对Controller层进行拦截即可
public class ExceptionAdvice {

    // 声明一个Logger记录 错误日志信息
    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    /**
     * @param e
     * @param request
     * @param response
     */
    @ExceptionHandler(Exception.class)
    public void handlerException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 一旦该方法执行表示出现异常 需要记录异常日志
        logger.error("服务器出现异常 : " + e.getMessage());

        // 记录异常的详细栈信息
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }

        // 因为请求中包含 一般的请求与 异步请求 一般的请求需要的是重定向到某个页面 而异步请求是需要返回一个json对象或者一个字符串 所以需要进行判断
        String xRequestedWith = request.getHeader("x-requested-with");

        if ("XMLHttpRequest".equals(xRequestedWith)) { // 条件成立表示是异步请求需要写回一个json对象到页面上去
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(FriendsUtils.getJSONString(1, "服务器发生异常!"));
        } else {
            // 重定向到 500 页面
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
