package com.a708.drwa.game.service;

import com.a708.drwa.debate.data.DebateMember;
import com.a708.drwa.debate.data.DebateMembers;
import com.a708.drwa.debate.data.RoomInfo;
import com.a708.drwa.debate.domain.Debate;
import com.a708.drwa.debate.enums.Role;
import com.a708.drwa.member.domain.Member;
import com.a708.drwa.redis.domain.DebateRedisKey;
import com.a708.drwa.redis.util.RedisKeyUtil;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class GameServiceTest {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    RedisKeyUtil redisKeyUtil = new RedisKeyUtil();
    final String debateKey = "1";


    @BeforeEach
    void before() {
        HashOperations<String, DebateRedisKey, Object> hashOperations = redisTemplate.opsForHash();
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();

        Map<Integer, DebateMember> memberDtoHashMap = new HashMap<>();
        memberDtoHashMap.put(1, DebateMember.builder().memberId(1).nickName("1").role(Role.A_TEAM).build());
        memberDtoHashMap.put(2, DebateMember.builder().memberId(2).nickName("2").role(Role.B_TEAM).build());
        memberDtoHashMap.put(3, DebateMember.builder().memberId(3).nickName("3").role(Role.JUROR).build());
        memberDtoHashMap.put(4, DebateMember.builder().memberId(4).nickName("4").role(Role.WATCHER).build());
        for(Map.Entry<Integer, DebateMember> member : memberDtoHashMap.entrySet()) {
            DebateMember memberDto = member.getValue();
            String teamKey = member.getValue().getRole().string();
            listOperations.rightPush(redisKeyUtil.getKeyByRoomIdWithKeyword(debateKey, teamKey), memberDto);
        }

        Map<String, Integer> mvpMap = new HashMap<>();
        mvpMap.put("1", 4);
        mvpMap.put("2", 4);
        mvpMap.put("3", 4);
        hashOperations.put(debateKey, DebateRedisKey.MVP, mvpMap);

        hashOperations.put(debateKey, DebateRedisKey.KEYWORD_A, "test");
    }

    @Test
    void redisTest() {
        HashOperations<String, DebateRedisKey, Object> hashOperations = redisTemplate.opsForHash();
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();

        // 방 정보 가져오기
        RoomInfo roomInfo = (RoomInfo) hashOperations.get(debateKey, DebateRedisKey.ROOM_INFO);

        // 참여자 리스트
        DebateMembers debateMembers = (DebateMembers) hashOperations.get(debateKey, DebateRedisKey.DEBATE_MEMBER_LIST);

        // MVP
        Map<String, Integer> mvpMap = (HashMap<String, Integer>) hashOperations.get(debateKey, DebateRedisKey.MVP);
        System.out.println(hashOperations.get(debateKey, DebateRedisKey.MVP.toString()));

        System.out.println(hashOperations.get(debateKey, DebateRedisKey.KEYWORD_A.toString()));

    }
}