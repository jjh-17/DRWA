package com.a708.drwa.redis.util;

import com.a708.drwa.redis.domain.DebateRedisKey;
import com.a708.drwa.redis.domain.MemberRedisKey;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyUtil {

    // debateId를 이용하여 DebateRedis 키 생성
    public String getKeyByDebateIdWithKeyword(int debateId, DebateRedisKey key) {
        return "debate:" + debateId + key;
    }

    // memberId를 이용하여 DebateRedis 키 생성
    public String getDebateKeyByMemberIdWithKeyword(int memberId, DebateRedisKey key) {
        return "member:" + memberId + key;
    }

    // memberId를 이용하여 MemberRedis 키 생성
    public String getMemberKeyNyMEmberIdWithKeyword(int memberId, MemberRedisKey key) {
        return "member:" + memberId + key;
    }
}
