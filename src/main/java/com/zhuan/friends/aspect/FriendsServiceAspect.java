package com.zhuan.friends.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 创建一个专门记录日志的切面 对系统日志进行统一的记录
 */
@Component
@Aspect
public class FriendsServiceAspect {

    // 声明日志组件 Logger
    private static final Logger logger = LoggerFactory.getLogger(FriendsServiceAspect.class);

    // 声明切入点表达式
    @Pointcut("execution(* com.zhuan.friends.service.*.*(..))")
    public void pointcut() {
    }

    // 在方法执行之前就记录日志
    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        // 需要获取 访问者的 IP地址 需要使用到 request对象 该如何获取
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest(); // 获取到 request对象
        // 获取当前访问该方法的 ip 地址
        String ip = request.getRemoteHost();
        // 格式化当前访客访问的时间
        String now = new SimpleDateFormat("yyyy-Mm-dd HH:mm:ss").format(new Date());
        // 获取当前访客访问的全类名 以及方法名
        String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        // 日志格式  用户 [1.2.3.4] ,在 [yyyy-Mm-dd HH:mm:ss],访问了[com.zhuan.friends.service.xxx()] 使用String的format方法格式化输出
//        logger.info(String.format("用户 : [%s],在 : [%s],访问了 : [%s]", ip, now, target));
    }
}
