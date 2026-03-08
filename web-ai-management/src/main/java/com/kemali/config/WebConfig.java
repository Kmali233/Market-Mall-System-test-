package com.kemali.config;

import com.kemali.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    //自定义的JWT令牌拦截器
    @Autowired
    private TokenInterceptor tokenInterceptor;

    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
       //注册JWT令牌拦截器，拦截所有请求
        registry.addInterceptor(tokenInterceptor).addPathPatterns("/**");
    }
}