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
class RecordServiceTest {

    @Autowired RecordService recordService;
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

        System.out.println(recordService.getMvpList(mvpMap));
    }

    @Test
    void deleteRedisDataTest() {
        // String 데이터 추가
        redisUtil.setData(
                redisKeyUtil.getKeyByDebateIdWithKeyword(3, DebateRedisKey.KEY_WORD),
                "keyword", (long) 180000);

        recordService.deleteRedisDebateData(3);
    }
}