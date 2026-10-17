package com.flashkill.seckillbackend.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flashkill.seckillbackend.common.BusinessException;
import com.flashkill.seckillbackend.common.ResultCode;
import com.flashkill.seckillbackend.dto.LoginDTO;
import com.flashkill.seckillbackend.dto.SendCodeDTO;
import com.flashkill.seckillbackend.entity.User;
import com.flashkill.seckillbackend.mapper.UserMapper;
import com.flashkill.seckillbackend.redis.RedisService;
import com.flashkill.seckillbackend.service.UserService;
import com.flashkill.seckillbackend.util.JwtUtil;
import com.flashkill.seckillbackend.vo.LoginVO;
import com.flashkill.seckillbackend.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private static final String CODE_KEY = "seckill:code:";

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void sendCode(SendCodeDTO dto) {
        String phone = dto.getPhone();

        // 1. 限流：60 秒内只能发一次
        String limitKey = "seckill:code:limit:" + phone;
        if (Boolean.TRUE.equals(redisService.hasKey(limitKey))) {
            throw new BusinessException(ResultCode.TOO_FREQUENT);
        }

        // 2. 生成 6 位验证码
        String code = RandomUtil.randomNumbers(6);
        log.info("手机号 {} 的验证码：{}", phone, code);  // 开发用，生产别打

        // 3. 存 Redis，5 分钟过期
        redisService.set(CODE_KEY + phone, code, 5, TimeUnit.MINUTES);

        // 4. 设置限流，60 秒
        redisService.set(limitKey, "1", 60, TimeUnit.SECONDS);

        // 5. 真实项目这里调短信服务商发短信，现在是模拟
        // smsService.send(phone, code);
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        String phone = dto.getPhone();
        String code = dto.getCode();

        // 1. 校验验证码
        String saved = redisService.get(CODE_KEY + phone);
        if (saved == null || !saved.equals(code)) {
            throw new BusinessException(ResultCode.CODE_ERROR);
        }
        // 用完删掉
        redisService.delete(CODE_KEY + phone);

        // 2. 查用户，没有就注册
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            user = new User();
            user.setPhone(phone);
            user.setNickname("用户" + phone.substring(7));
            user.setRole("user");
            userMapper.insert(user);
        }

        // 3. 生成 token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role", user.getRole());
        String token = JwtUtil.createToken(claims);

        // 4. 返回
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setRole(user.getRole());
        return vo;
    }

    @Override
    public UserVO info(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setPhone(user.getPhone());
        vo.setNickname(user.getNickname());
        vo.setRole(user.getRole());
        return vo;
    }
}