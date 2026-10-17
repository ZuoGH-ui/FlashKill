package com.flashkill.seckillbackend.util;

import com.flashkill.seckillbackend.entity.SeckillActivity;

import java.time.LocalDateTime;

public class ActivityStatusUtil {

    public static Integer calc(SeckillActivity a) {
        // 手动结束，直接返回
        if (a.getStatus() != null && a.getStatus() == 2) {
            return 2;
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(a.getStartTime())) {
            return 0;
        }
        if (now.isAfter(a.getEndTime())) {
            return 2;
        }
        return 1;
    }
}