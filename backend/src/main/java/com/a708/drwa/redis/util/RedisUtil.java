package com.a708.drwa.redis.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, Object> redisTemplate;

    public void setDate(String key, String value, Long expiredTime) {

    }
}
