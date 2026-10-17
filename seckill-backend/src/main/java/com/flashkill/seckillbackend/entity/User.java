package com.flashkill.seckillbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
//用户信息
    @TableId(type = IdType.AUTO)
    private Long id;

    private String phone;

    private String nickname;

    private String role;

    private LocalDateTime createTime;
}