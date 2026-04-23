package com.aiProject.config;

import com.aiProject.interceptor.TokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TokenInterceptor())
                .addPathPatterns("/**")         // 拦截所有请求
                .excludePathPatterns(           // 放行的接口
                        "/user/login",
                        "/user/captcha",
                        "/user/refreshToken"
                );
    }
}
