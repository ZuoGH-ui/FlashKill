package com.flashkill.seckillbackend.vo;

import lombok.Data;

@Data
public class UserVO {

    private Long id;

    private String phone;

    private String nickname;

    private String role;
}