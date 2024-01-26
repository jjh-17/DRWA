package com.a708.drwa.redis.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, Object> redisTemplate;
//    private final RedisTemplate<String, Integer> integerRedisTemplate;

    public void setData(String key, String value, Long expiredTime) {
        redisTemplate.opsForValue().set(key, value, expiredTime, TimeUnit.MILLISECONDS);
    }

    public String getData(String key) {
        return String.valueOf(redisTemplate.opsForValue().get(key));
    }

    public Integer getIntegerData(String key) {
        return Integer.parseInt(String.valueOf(redisTemplate.opsForValue().get(key)));
    }

//    public List<Integer> getIntegerListData(String key) {
//        return integerRedisTemplate.opsForList().range(key, 0, -1);
//    }

    public Map<Integer, Integer> getIntegerIntegerMapData(String key) {
        HashOperations<String, Integer, Integer> hashOperations = redisTemplate.opsForHash();
        return hashOperations.entries(key);
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }

}
