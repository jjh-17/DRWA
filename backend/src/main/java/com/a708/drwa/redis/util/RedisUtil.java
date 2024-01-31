package com.a708.drwa.redis.util;

import com.a708.drwa.redis.Exception.RedisErrorCode;
import com.a708.drwa.redis.Exception.RedisException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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

    public void set(String key, Object value, Long expriredTime) {
        redisTemplate.opsForValue().set(key, value, expriredTime);
    }
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
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

    public void setMapData(String key, Map<Object, Object> map, int expiredTime) {
        redisTemplate.opsForHash().putAll(key, map);
        redisTemplate.expire(key, expiredTime, TimeUnit.MINUTES);
    }

    public String getData(String key) {
        String result = String.valueOf(redisTemplate.opsForValue().get(key));
        if(result==null) throw new RedisException(RedisErrorCode.REDIS_READ_FAIL);
        return result;
    }

    public int getIntegerData(String key) {
        String result = String.valueOf(redisTemplate.opsForValue().get(key));
        if (result == null) throw new RedisException(RedisErrorCode.REDIS_READ_FAIL);
        return Integer.parseInt(result);
    }

    public List<Object> getListData(String key) {
        List<Object> result = redisTemplate.opsForList().range(key, 0, -1);
        if(result==null || result.isEmpty()) throw new RedisException(RedisErrorCode.REDIS_READ_FAIL);
        return result;
    }

    public Map<Object, Object> getMapData(String key) {
        Map<Object, Object> result = redisTemplate.opsForHash().entries(key);
        if(result.isEmpty()) throw new RedisException(RedisErrorCode.REDIS_READ_FAIL);
        return result;
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }

    public Long deleteDataAll(List<String> keys) {
        return redisTemplate.delete(keys);
    }

}
