package com.flashkill.seckillbackend.service;

import com.flashkill.seckillbackend.dto.LoginDTO;
import com.flashkill.seckillbackend.dto.SendCodeDTO;
import com.flashkill.seckillbackend.vo.LoginVO;
import com.flashkill.seckillbackend.vo.UserVO;

public interface UserService {

    /** 发送验证码 */
    void sendCode(SendCodeDTO dto);

    /** 登录 */
    LoginVO login(LoginDTO dto);

    UserVO info(Long userId);
}