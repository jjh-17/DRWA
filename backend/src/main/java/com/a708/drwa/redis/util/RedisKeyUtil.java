package com.a708.drwa.redis.util;

import com.a708.drwa.redis.domain.DebateRedisKey;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyUtil {

    // debateId를 이용한 키 값
    public String getKeyByDebateIdWithKeyword(int debateId, DebateRedisKey key) {
        return "debate:" + debateId + key;
    }

    // memberId를 이요한 키 값
    public String getKeyByMemberIdWithKeyword(int memberId, DebateRedisKey key) {
        return "member:" + memberId + key;
    }
}
