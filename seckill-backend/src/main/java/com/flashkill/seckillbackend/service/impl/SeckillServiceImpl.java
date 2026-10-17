package com.flashkill.seckillbackend.service.impl;

import com.flashkill.seckillbackend.common.BusinessException;
import com.flashkill.seckillbackend.common.ResultCode;
import com.flashkill.seckillbackend.entity.SeckillActivity;
import com.flashkill.seckillbackend.mapper.SeckillActivityMapper;
import com.flashkill.seckillbackend.mq.message.SeckillMessage;
import com.flashkill.seckillbackend.mq.producer.SeckillMessageProducer;
import com.flashkill.seckillbackend.redis.RedisKey;
import com.flashkill.seckillbackend.redis.RedisService;
import com.flashkill.seckillbackend.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private SeckillActivityMapper activityMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private SeckillMessageProducer producer;

    @Override
    public String execute(Long userId, Long activityId) {
        // 1. 校验活动
        // 从 Redis 读活动信息
        String startTimeStr = redisService.getHash("seckill:activity:" + activityId, "startTime");
        String endTimeStr = redisService.getHash("seckill:activity:" + activityId, "endTime");

        if (startTimeStr == null) {
            // Redis 没有，查数据库兜底
            SeckillActivity activity = activityMapper.selectById(activityId);
            if (activity == null) {
                throw new BusinessException(ResultCode.NOT_FOUND);
            }
            startTimeStr = activity.getStartTime().toString();
            endTimeStr = activity.getEndTime().toString();
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(LocalDateTime.parse(startTimeStr))) {
            throw new BusinessException(ResultCode.ACTIVITY_NOT_START);
        }
        if (now.isAfter(LocalDateTime.parse(endTimeStr))) {
            throw new BusinessException(ResultCode.ACTIVITY_END);
        }

        // 2. 执行 Lua
        Long result = redisService.executeLua(
                "lua/seckill.lua",
                Arrays.asList(
                        RedisKey.seckillStock(activityId),
                        RedisKey.seckillUsers(activityId)
                ),
                userId.toString()
        );

        if (result == null || result == 0) {
            throw new BusinessException(ResultCode.STOCK_EMPTY);
        }
        if (result == -1) {
            throw new BusinessException(ResultCode.REPEAT_ORDER);
        }

        // 3. 发 MQ，异步下单
        SeckillMessage message = new SeckillMessage();
        message.setUserId(userId);
        message.setActivityId(activityId);
        producer.send(message);

        // 4. 标记排队中
        redisService.set(RedisKey.seckillResult(userId, activityId),
                "pending", 5, TimeUnit.MINUTES);

        return null;  // 排队中
    }

    @Override
    public String queryResult(Long userId, Long activityId) {
        return redisService.get(RedisKey.seckillResult(userId, activityId));
    }
}