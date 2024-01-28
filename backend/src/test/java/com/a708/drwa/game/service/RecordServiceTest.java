package com.a708.drwa.game.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@Transactional
class RecordServiceTest {

    @Autowired
    RecordService recordService;

    @Test
    public void getMvpListTest() {
        Map<Integer, Integer> mvpMap = new HashMap<>();
        mvpMap.put(1, 2);
        mvpMap.put(2, 2);
        mvpMap.put(3, 5);
        mvpMap.put(4, 3);

        System.out.println(recordService.getMvpList(mvpMap));
    }
}