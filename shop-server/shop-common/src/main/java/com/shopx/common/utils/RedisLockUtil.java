package com.shopx.common.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisLockUtil {

    private final StringRedisTemplate stringRedisTemplate;

    public boolean tryLock(String key, long expireSeconds) {
        Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", expireSeconds, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(result);
    }

    public void unlock(String key) {
        stringRedisTemplate.delete(key);
    }
}
