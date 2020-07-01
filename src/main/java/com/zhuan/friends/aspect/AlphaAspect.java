package com.zhuan.friends.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Component // 声明为组件 交给Spring容器管理
//@Aspect // 声明为一个切面
public class AlphaAspect {

    // 声明切入点表达式 *(返回值类型) com.zhuan.friends.service.*(类名).*[方法名](..)
    @Pointcut("execution(* com.zhuan.friends.service.*.*(..))")
    public void pointcut() {
    }


    // 前置通知
    @Before("pointcut()")
    public void before() {
        System.out.println("前置通知!");
    }

    @After("pointcut()")
    public void after() {
        System.out.println("后置通知!");
    }

    @AfterThrowing("pointcut()")
    public void afterThrowing() {
        System.out.println("异常通知!");
    }

    @AfterReturning("pointcut()")
    public void afterReturning() {
        System.out.println("最终通知!");
    }


    /**
     * 环绕通知 可以在 方法之前前后加上 切面
     *
     * @param joinPoint
     * @return
     */
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 方法之前
        System.out.println("环绕通知 : 方法执行之前执行");
        Object obj = joinPoint.proceed(); // 被执行的方法
        // 方法之后
        System.out.println("环绕通知 : 方法执行之后执行");
        return obj;
    }
}
