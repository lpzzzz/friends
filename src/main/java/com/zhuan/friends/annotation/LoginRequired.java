package com.zhuan.friends.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 是否需要登录注解 ： 用于表示是否登录 哪些功能需要登录之后才能访问
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME) // 运行时生效
public @interface LoginRequired {
}
