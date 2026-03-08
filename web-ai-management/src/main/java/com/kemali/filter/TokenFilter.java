package com.kemali.filter;

import com.kemali.utils.CurrentHolder;
import com.kemali.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@WebFilter(urlPatterns = "/*")
@Component
public class TokenFilter implements Filter {

    // 从配置文件中读取白名单
    @Value("${security.whitelist:}")
    private List<String> whitelist;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1. 获取请求的url地址
        String uri = request.getRequestURI(); // /employee/login

        //2. 判断请求是否在白名单中，如果是则直接放行
        if (isInWhitelist(uri)) {
            log.info("请求在白名单中 , 直接放行: {}", uri);
            filterChain.doFilter(request, response);
            return;
        }

        //3. 判断是否是登录请求, 如果url地址中包含 login, 则说明是登录请求, 放行
        if (uri.contains("login")) {
            log.info("登录请求, 放行");
            filterChain.doFilter(request, response);
            return;
        }

        //4. 获取请求中的token
        String token = request.getHeader("token");

        //5. 判断token是否为空, 如果为空, 响应401状态码
        if (token == null || token.isEmpty()) {
            log.info("token为空, 响应401状态码");
            response.setStatus(401); // 响应 401 状态码
            return;
        }

        //6. 如果token不为空, 调用JWtUtils工具类的方法解析token, 如果解析失败, 响应401状态码
        try {
            Claims claims = JwtUtils.parseJWT(token);
            Integer empId = Integer.valueOf(claims.get("id").toString());
            // 线程绑定id
            CurrentHolder.setCurrentId(empId);

            log.info("token解析成功, 放行");
        } catch (Exception e) {
            log.info("token解析失败, 响应401状态码");
            response.setStatus(401);
            return;
        }

        //7. 放行
        filterChain.doFilter(request, response);

        //8. 清空当前线程绑定的id
        CurrentHolder.remove();
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