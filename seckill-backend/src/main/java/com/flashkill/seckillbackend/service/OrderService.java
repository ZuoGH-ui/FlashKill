package com.flashkill.seckillbackend.service;

import com.flashkill.seckillbackend.vo.OrderVO;

import java.util.List;

public interface OrderService {

    /** 我的订单列表 */
    List<OrderVO> list(Long userId);

    /** 订单详情 */
    OrderVO detail(Long userId, String orderNo);

    /** 取消订单 */
    void cancel(Long userId, String orderNo);

    /** 支付订单（模拟） */
    void pay(Long userId, String orderNo);
}