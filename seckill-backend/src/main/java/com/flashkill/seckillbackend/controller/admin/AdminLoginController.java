package com.flashkill.seckillbackend.controller.admin;

import com.flashkill.seckillbackend.common.BusinessException;
import com.flashkill.seckillbackend.common.Result;
import com.flashkill.seckillbackend.common.ResultCode;
import com.flashkill.seckillbackend.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminLoginController {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    /** 管理员登录 */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (!ADMIN_USERNAME.equals(username) || !ADMIN_PASSWORD.equals(password)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "账号或密码错误");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 0L);
        claims.put("role", "admin");
        String token = JwtUtil.createToken(claims);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("role", "admin");
        return Result.success(data);
    }
}