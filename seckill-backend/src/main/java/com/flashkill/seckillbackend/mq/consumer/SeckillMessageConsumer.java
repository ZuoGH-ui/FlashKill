package com.flashkill.seckillbackend.mq.consumer;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.flashkill.seckillbackend.config.RabbitMQConfig;
import com.flashkill.seckillbackend.entity.SeckillActivity;
import com.flashkill.seckillbackend.entity.SeckillOrder;
import com.flashkill.seckillbackend.mapper.SeckillActivityMapper;
import com.flashkill.seckillbackend.mapper.SeckillOrderMapper;
import com.flashkill.seckillbackend.mq.message.SeckillMessage;
import com.flashkill.seckillbackend.redis.RedisKey;
import com.flashkill.seckillbackend.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class SeckillMessageConsumer {

    @Autowired
    private RedisService redisService;

    @Autowired
    private SeckillOrderMapper orderMapper;

    @Autowired
    private SeckillActivityMapper activityMapper;

    @RabbitListener(queues = RabbitMQConfig.SECKILL_QUEUE, concurrency = "10")
    public void handle(SeckillMessage message) {
        log.info("收到秒杀消息：{}", message);

        Long userId = message.getUserId();
        Long activityId = message.getActivityId();

        try {
            // 1. 查活动，拿秒杀价和商品ID
            SeckillActivity activity = activityMapper.selectById(activityId);
            if (activity == null) {
                log.error("活动不存在：{}", activityId);
                return;
            }

            // 2. 扣数据库库存（乐观锁，带 stock > 0 条件）
            int rows = activityMapper.update(null,
                    new LambdaUpdateWrapper<SeckillActivity>()
                            .setSql("stock = stock - 1")
                            .eq(SeckillActivity::getId, activityId)
                            .gt(SeckillActivity::getStock, 0));

            if (rows == 0) {
                log.error("数据库库存不足，activityId={}", activityId);
                redisService.set(RedisKey.seckillResult(userId, activityId),
                        "fail", 5, TimeUnit.MINUTES);
                return;
            }

            // 3. 创建订单
            String orderNo = "SK" + System.currentTimeMillis() + userId;
            SeckillOrder order = new SeckillOrder();
            order.setOrderNo(orderNo);
            order.setUserId(userId);
            order.setActivityId(activityId);
            order.setGoodsId(activity.getGoodsId());
            order.setAmount(activity.getSeckillPrice());
            order.setStatus(0);  // 待支付
            order.setCreateTime(LocalDateTime.now());

            orderMapper.insert(order);

            // 4. 写结果到 Redis
            redisService.set(RedisKey.seckillResult(userId, activityId),
                    "success:" + orderNo, 5, TimeUnit.MINUTES);

            log.info("订单创建成功：{}", orderNo);

        } catch (Exception e) {
            log.error("消费者处理失败", e);
            redisService.set(RedisKey.seckillResult(userId, activityId),
                    "fail", 5, TimeUnit.MINUTES);
        }
    }
}