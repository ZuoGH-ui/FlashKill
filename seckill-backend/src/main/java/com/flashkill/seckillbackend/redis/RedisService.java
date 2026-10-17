package com.flashkill.seckillbackend.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /** 设置字符串 */
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /** 设置字符串 + 过期时间 */
    public void set(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /** 获取字符串 */
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /** 删除 key */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /** 判断 key 是否存在 */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public void setHash(String key, String field, String value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    public String getHash(String key, String field) {
        return (String) redisTemplate.opsForHash().get(key, field);
    }

    /** 自增 */
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    /** 设置过期时间 */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    public Long executeLua(String scriptPath, List<String> keys, Object... args) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource(scriptPath));
        script.setResultType(Long.class);
        return redisTemplate.execute(script, keys, args);
    }
}