package com.flashkill.seckillbackend.redis;

public class RedisKey {

    /** 秒杀库存 */
    public static String seckillStock(Long activityId) {
        return "seckill:stock:" + activityId;
    }

    /** 秒杀用户已购 */
    public static String seckillUsers(Long activityId) {
        return "seckill:users:" + activityId;
    }

    /** 秒杀结果 */
    public static String seckillResult(Long userId, Long activityId) {
        return "seckill:result:" + userId + ":" + activityId;
    }
}