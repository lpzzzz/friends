package com.zhuan.friends.config;


import com.zhuan.friends.controller.interceptor.AlphaInterceptor;
import com.zhuan.friends.controller.interceptor.LoginRequiredInterceptor;
import com.zhuan.friends.controller.interceptor.LoginTicketInterceptor;
import com.zhuan.friends.controller.interceptor.MessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 实现拦截器的配置类
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private AlphaInterceptor alphaInterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Autowired
    private MessageInterceptor messageInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(alphaInterceptor)
                // 需要排除的路径  excludePathPatterns
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg")
                // 明确 指定 需要拦截的路径
                .addPathPatterns("/register", "/login");
        registry.addInterceptor(loginTicketInterceptor)
                // 需要排除的路径  excludePathPatterns
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
        registry.addInterceptor(loginRequiredInterceptor)
                // 需要排除的路径  excludePathPatterns
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
        registry.addInterceptor(messageInterceptor)
                // 需要排除的路径  excludePathPatterns
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
    }
}
