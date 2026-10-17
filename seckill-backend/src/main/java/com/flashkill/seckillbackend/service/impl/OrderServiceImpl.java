package com.flashkill.seckillbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flashkill.seckillbackend.common.BusinessException;
import com.flashkill.seckillbackend.common.ResultCode;
import com.flashkill.seckillbackend.entity.Goods;
import com.flashkill.seckillbackend.entity.SeckillActivity;
import com.flashkill.seckillbackend.entity.SeckillOrder;
import com.flashkill.seckillbackend.mapper.GoodsMapper;
import com.flashkill.seckillbackend.mapper.SeckillActivityMapper;
import com.flashkill.seckillbackend.mapper.SeckillOrderMapper;
import com.flashkill.seckillbackend.service.OrderService;
import com.flashkill.seckillbackend.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private SeckillOrderMapper orderMapper;

    @Autowired
    private SeckillActivityMapper activityMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public List<OrderVO> list(Long userId) {
        LambdaQueryWrapper<SeckillOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SeckillOrder::getUserId, userId)
               .orderByDesc(SeckillOrder::getCreateTime);

        List<SeckillOrder> orders = orderMapper.selectList(wrapper);

        List<OrderVO> result = new ArrayList<>();
        for (SeckillOrder o : orders) {
            result.add(toVO(o));
        }
        return result;
    }

    @Override
    public OrderVO detail(Long userId, String orderNo) {
        SeckillOrder order = getByOrderNo(orderNo);
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        return toVO(order);
    }

    @Override
    public void cancel(Long userId, String orderNo) {
        SeckillOrder order = getByOrderNo(orderNo);
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        if (order.getStatus() != 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "只能取消待支付订单");
        }

        // 1. 改订单状态
        order.setStatus(2);
        order.setCancelTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // 2. 回滚 Redis 库存和用户记录（可选）
        // redisService.increment(RedisKey.seckillStock(order.getActivityId()));
        // redisService.delete(...);

        log.info("订单已取消：{}", orderNo);
    }

    @Override
    public void pay(Long userId, String orderNo) {
        SeckillOrder order = getByOrderNo(orderNo);
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        if (order.getStatus() != 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "订单状态不正确");
        }

        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);

        log.info("订单已支付：{}", orderNo);
    }

    private SeckillOrder getByOrderNo(String orderNo) {
        LambdaQueryWrapper<SeckillOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SeckillOrder::getOrderNo, orderNo);
        SeckillOrder order = orderMapper.selectOne(wrapper);
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return order;
    }

    private OrderVO toVO(SeckillOrder o) {
        OrderVO vo = new OrderVO();
        vo.setOrderNo(o.getOrderNo());
        vo.setAmount(o.getAmount());
        vo.setStatus(o.getStatus());
        vo.setCreateTime(o.getCreateTime());

        // 查商品名和图片
        Goods goods = goodsMapper.selectById(o.getGoodsId());
        if (goods != null) {
            vo.setGoodsName(goods.getName());
            vo.setGoodsImage(goods.getImage());
        }
        return vo;
    }
}