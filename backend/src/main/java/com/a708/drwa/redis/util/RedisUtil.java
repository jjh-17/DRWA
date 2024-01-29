package com.a708.drwa.redis.util;

import com.a708.drwa.redis.Exception.RedisErrorCode;
import com.a708.drwa.redis.Exception.RedisException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, Object> redisTemplate;

    public void setData(String key, Object value, Long expiredTime) {
        redisTemplate.opsForValue().set(key, value, expiredTime, TimeUnit.MILLISECONDS);
    }

    public void setListData(String key, List<Object> list, int expiredTime) {
        ListOperations<String, Object> stringObjectListOperations = redisTemplate.opsForList();
        for(Object l : list)
            stringObjectListOperations.rightPush(key, l);
        redisTemplate.expire(key, expiredTime, TimeUnit.MINUTES);
    }

    public void setMapData(String key, Map<Object, Object> map, int expiredTime) {
        redisTemplate.opsForHash().putAll(key, map);
        redisTemplate.expire(key, expiredTime, TimeUnit.MINUTES);
    }

    public String getData(String key) {
        return String.valueOf(redisTemplate.opsForValue().get(key));
    }

    public int getIntegerData(String key) {
        String result = String.valueOf(redisTemplate.opsForValue().get(key));
        if(result==null) throw new RedisException(RedisErrorCode.REDIS_SELECT_FAIL);
        return Integer.parseInt(result);
    }

    public List<Object> getListData(String key) {
        List<Object> result = redisTemplate.opsForList().range(key, 0, -1);
        if(result==null || result.isEmpty()) throw new RedisException(RedisErrorCode.REDIS_SELECT_FAIL);
        return result;
    }

    public Map<Object, Object> getMapData(String key) {
        Map<Object, Object> result = redisTemplate.opsForHash().entries(key);
        if(result.isEmpty()) throw new RedisException(RedisErrorCode.REDIS_SELECT_FAIL);
        return result;
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }

    public Long deleteDataAll(List<String> keys) {
        return redisTemplate.delete(keys);
    }

}
