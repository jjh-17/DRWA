package com.a708.drwa.redis.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisPubService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisPubService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void publishMessage(String channelName, String message) {
        redisTemplate.convertAndSend(channelName, message);
    }

}