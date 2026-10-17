package com.flashkill.seckillbackend.service;

public interface SeckillService {

    /** 执行秒杀，返回订单号；排队中返回 null */
    String execute(Long userId, Long activityId);

    /** 查询秒杀结果 */
    String queryResult(Long userId, Long activityId);
}