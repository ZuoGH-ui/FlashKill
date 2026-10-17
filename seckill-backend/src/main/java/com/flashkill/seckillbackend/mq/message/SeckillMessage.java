package com.flashkill.seckillbackend.mq.message;

import lombok.Data;

import java.io.Serializable;

@Data
public class SeckillMessage implements Serializable {

    private Long userId;

    private Long activityId;
}