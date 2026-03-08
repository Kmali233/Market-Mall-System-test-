package com.kemali.interceptor;

// 一般用过滤器解析Token

import com.kemali.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {

    // 从配置文件中读取白名单
    @Value("${security.whitelist:}")
    private List<String> whitelist;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //1. 获取请求路径。
        String path = request.getRequestURI();

        //2. 判断请求是否在白名单中，如果是则直接放行
        if (isInWhitelist(path)) {
            log.info("拦截器：请求在白名单中 , 直接放行: {}", path);
            return true;
        }

        //3. 判断请求是否包含login，如果包含，说明是登录操作，放行。
        if(path.contains("login")){ //登录请求
            log.info("拦截器：登录请求 , 直接放行");
            return true;
        }

        //4. 获取请求头中的令牌（token）。
        String jwt = request.getHeader("token");

        //5. 判断令牌是否存在，如果不存在，返回错误结果（未登录）。
        if(!StringUtils.hasLength(jwt)){ //jwt为空

            log.info("拦截器：获取到jwt令牌为空, 返回错误结果");
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);

            return false;
        }

        //6. 解析token，如果解析失败，返回错误结果（未登录）。
        try {
            JwtUtils.parseJWT(jwt);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("拦截器：解析令牌失败, 返回错误结果");
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            return false;
        }

        //7. 放行。
        log.info("令牌合法, 放行");
        return true;
    }

    // 判断请求路径是否在白名单中
    private boolean isInWhitelist(String path) {
        if (whitelist == null || whitelist.isEmpty()) {
            return false;
        }

        for (String pattern : whitelist) {
            if (matchesPattern(path, pattern)) {
                return true;
            }
        }

        return false;
    }

    // 简单的路径匹配逻辑，支持通配符 **
    private boolean matchesPattern(String path, String pattern) {
        if (pattern.equals("**")) {
            return true;
        }

        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return path.startsWith(prefix);
        }

        return path.equals(pattern);
    }

}