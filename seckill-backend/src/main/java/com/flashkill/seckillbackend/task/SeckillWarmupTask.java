package com.flashkill.seckillbackend.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flashkill.seckillbackend.entity.SeckillActivity;
import com.flashkill.seckillbackend.mapper.SeckillActivityMapper;
import com.flashkill.seckillbackend.redis.RedisKey;
import com.flashkill.seckillbackend.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class SeckillWarmupTask implements ApplicationRunner {

    @Autowired
    private SeckillActivityMapper activityMapper;

    @Autowired
    private RedisService redisService;

    @Override
    public void run(ApplicationArguments args) {
        log.info("========== 开始预热秒杀活动库存 ==========");

        LambdaQueryWrapper<SeckillActivity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SeckillActivity::getStatus, 0, 1)
                .gt(SeckillActivity::getEndTime, LocalDateTime.now());

        List<SeckillActivity> activities = activityMapper.selectList(wrapper);

        for (SeckillActivity activity : activities) {
            // 库存
            String stockKey = RedisKey.seckillStock(activity.getId());
            if (!Boolean.TRUE.equals(redisService.hasKey(stockKey))) {
                redisService.set(stockKey, String.valueOf(activity.getStock()));
            }

            // 活动信息（用 Hash 存）
            String infoKey = "seckill:activity:" + activity.getId();
            redisService.setHash(infoKey, "startTime", activity.getStartTime().toString());
            redisService.setHash(infoKey, "endTime", activity.getEndTime().toString());
            redisService.setHash(infoKey, "status", String.valueOf(activity.getStatus()));
        }

        log.info("========== 预热完成 ==========");
    }
}