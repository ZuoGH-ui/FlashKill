package com.flashkill.seckillbackend.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flashkill.seckillbackend.entity.SeckillOrder;
import com.flashkill.seckillbackend.mapper.SeckillOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class OrderTimeoutTask {

    @Autowired
    private SeckillOrderMapper orderMapper;

    /** 每分钟扫一次，取消 15 分钟未支付的订单 */
    @Scheduled(fixedRate = 60_000)
    public void cancelTimeoutOrders() {
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(15);

        LambdaQueryWrapper<SeckillOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SeckillOrder::getStatus, 0)
               .lt(SeckillOrder::getCreateTime, deadline);

        List<SeckillOrder> orders = orderMapper.selectList(wrapper);
        for (SeckillOrder o : orders) {
            o.setStatus(3);  // 已超时
            o.setCancelTime(LocalDateTime.now());
            orderMapper.updateById(o);
            log.info("订单超时取消：{}", o.getOrderNo());
        }
    }
}