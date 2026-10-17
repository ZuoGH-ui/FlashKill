package com.flashkill.seckillbackend.interceptor;

import com.flashkill.seckillbackend.common.BusinessException;
import com.flashkill.seckillbackend.common.ResultCode;
import com.flashkill.seckillbackend.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        // 放行 OPTIONS 预检请求
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 取 token
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        token = token.substring(7);

        // 校验 token
        if (!JwtUtil.validate(token)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        // 解析用户信息，放进 request
        Claims claims = JwtUtil.parseToken(token);
        request.setAttribute("userId", claims.get("userId", Long.class));
        request.setAttribute("role", claims.get("role", String.class));

        return true;
    }
}