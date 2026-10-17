package com.flashkill.seckillbackend.controller;

import com.flashkill.seckillbackend.common.Result;
import com.flashkill.seckillbackend.dto.LoginDTO;
import com.flashkill.seckillbackend.dto.SendCodeDTO;
import com.flashkill.seckillbackend.service.UserService;
import com.flashkill.seckillbackend.vo.LoginVO;
import com.flashkill.seckillbackend.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /** 发送验证码 */
    @PostMapping("/sendCode")
    public Result<Void> sendCode(@Valid @RequestBody SendCodeDTO dto) {
        userService.sendCode(dto);
        return Result.success();
    }

    /** 登录 */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    /** 获取用户信息 */
    @GetMapping("/info")
    public Result<UserVO> info(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(userService.info(userId));
    }
}