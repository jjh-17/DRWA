package com.a708.drwa.redis.util;

import com.a708.drwa.redis.domain.DebateRedisKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RedisUtilTest {

    final RedisTemplate<String, Integer> integerRedisTemplate = new RedisTemplate<>();
    final RedisTemplate<String, String> stringRedisTemplate = new RedisTemplate<>();

    @Autowired RedisUtil redisUtil;
    RedisKeyUtil redisKeyUtil = new RedisKeyUtil();

    @BeforeEach
    void init() {
        redisUtil.deleteData("key");
        redisUtil.deleteData("key2");
    }

    @Test
    void setData() {
        redisUtil.setData("key", "value", (long)180000);
        redisUtil.setData("key", 4, (long)180000);
    }

    @Test
    void setDataFail() {
        redisUtil.setData("key", "value", (long)180000);
        redisUtil.setData("key", 4, (long)180000);
    }

    @Test
    void setIntegerData() {
        redisUtil.setData("key", 3, (long)180000);
    }
    @Test
    void setListData() {
        List<Object> list = new ArrayList<>();
        list.add(1); list.add(2);

        redisUtil.setListData("key", list, 3);
    }

    @Test
    void setMapData() {
        Map<Object, Object> map = new HashMap<>();
        map.put(redisKeyUtil.getDebateKeyByMemberIdWithKeyword(1, DebateRedisKey.MVP), 1);
        map.put(redisKeyUtil.getDebateKeyByMemberIdWithKeyword(2, DebateRedisKey.MVP), 2);

        redisUtil.setMapData("key", map, 3);
    }

    @Test
    void getData() {
        redisUtil.setData("key", "3", (long)180000);
        System.out.println(redisUtil.getIntegerData("key"));
    }

    @Test
    void getIntegerData() {
        redisUtil.setData("key", 3, (long)180000);
        System.out.println(redisUtil.getIntegerData("key"));
    }

    @Test
    void getListData() {
        List<Object> list = new ArrayList<>();
        list.add(1); list.add(2);

        redisUtil.setListData("key", list, 3);
        List<Object> result = redisUtil.getListData("key");
        System.out.println(result);
    }

    @Test
    void getMapData() {
        Map<Object, Object> map = new HashMap<>();
        map.put(redisKeyUtil.getDebateKeyByMemberIdWithKeyword(1, DebateRedisKey.MVP), 1);
        map.put(redisKeyUtil.getDebateKeyByMemberIdWithKeyword(2, DebateRedisKey.MVP), 2);
        redisUtil.setMapData("key", map, 3);

        System.out.println(redisUtil.getMapData("key"));
    }

    @Test
    void deleteDataAll() {
        redisUtil.setData("key1", "3", (long)180000);

        redisUtil.setData("key2", 3, (long)180000);

        List<Integer> list = new ArrayList<>();
        list.add(1); list.add(2);
        redisUtil.setData("key3", list, (long)180000);

        Map<Integer, Integer> map = new HashMap<>();
        map.put(1, 1);  map.put(2, 2);
        redisUtil.setData("key4", map, (long)180000);

        List<String> keys = new ArrayList<>();
        keys.add("key1"); keys.add("key2"); keys.add("key3");
        keys.add("key4"); keys.add("key5"); keys.add("key6");
        System.out.println(redisUtil.deleteDataAll(keys));
    }

    @Test
    void compareSet(){
        long beforeTime = System.currentTimeMillis();

        //
        integerRedisTemplate.opsForValue().set("key1", 1, 3, TimeUnit.MINUTES);
        integerRedisTemplate.opsForValue().set("key2", 2, 3, TimeUnit.MINUTES);
        stringRedisTemplate.opsForValue().set("key3", "3", 3, TimeUnit.MINUTES);

        integerRedisTemplate.opsForList().rightPushAll("key4", List.of(1, 2, 3));
        integerRedisTemplate.expire("key4", 3, TimeUnit.MINUTES);

        integerRedisTemplate.opsForHash().putAll("key5", Map.of(
                "k1", 1,
                "k2", 2
        ));
        integerRedisTemplate.expire("key5", 3, TimeUnit.MINUTES);

        stringRedisTemplate.opsForHash().putAll("key6", Map.of(
                "k1", 1
        ));
        stringRedisTemplate.expire("key6", 3, TimeUnit.MINUTES);


    }
}