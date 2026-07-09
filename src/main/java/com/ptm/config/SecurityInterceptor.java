package com.ptm.config;

import com.ptm.util.ResponseResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SecurityInterceptor implements HandlerInterceptor {

    private static final List<String> WHITE_LIST = Arrays.asList(
            "/api/auth/login",
            "/api/auth/captcha",
            "/api/auth/logout"
    );

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws IOException {
        String uri = request.getRequestURI();

        // 白名单路径放行
        if (WHITE_LIST.contains(uri)) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            return true;
        }

        // 未登录返回 401
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(200);
        response.getWriter().write(objectMapper.writeValueAsString(ResponseResult.unauthorized()));
        return false;
    }
}
