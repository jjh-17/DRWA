package com.a708.drwa.game.service;

import com.a708.drwa.redis.domain.DebateRedisKey;
import com.a708.drwa.redis.util.RedisKeyUtil;
import com.a708.drwa.redis.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@SpringBootTest
@Transactional
class GameServiceTest {

    @Autowired
    GameService gameService;
    @Autowired RedisUtil redisUtil;
    RedisKeyUtil redisKeyUtil = new RedisKeyUtil();


    @Test
    void getMvpListTest() {
        Map<Integer, Integer> mvpMap = new HashMap<>();
        mvpMap.put(1, 2);
        mvpMap.put(2, 2);
        mvpMap.put(3, 5);
        mvpMap.put(4, 3);
        mvpMap.put(5, 3);

//        System.out.println(gameService.getMvpList(mvpMap));
    }

    @Test
    void deleteRedisDataTest() {
    }
}