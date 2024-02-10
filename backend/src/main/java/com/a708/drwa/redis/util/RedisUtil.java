package com.a708.drwa.redis.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, Object> redisTemplate;

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key) + "";
    }

    public void hSet(String key, String hKey, Object value) {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    public Object hGet(String key, String hKey) {
        return redisTemplate.opsForHash().get(key, hKey);
    }

    public void setList(String key, Object value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    public List<Object> getList(String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    public Integer getIntegerData(String key) {
        return Integer.parseInt(String.valueOf(redisTemplate.opsForValue().get(key)));
    }

    public List<?> getIntegerListData(String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    public Map<Integer, Integer> getIntegerIntegerMapData(String key) {
        HashOperations<String, Integer, Integer> hashOperations = redisTemplate.opsForHash();
        return hashOperations.entries(key);
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }
}
