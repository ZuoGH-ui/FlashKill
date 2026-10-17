package com.flashkill.seckillbackend.vo;

import lombok.Data;

@Data
public class LoginVO {

    private String token;

    private Long userId;

    private String nickname;

    private String role;
}