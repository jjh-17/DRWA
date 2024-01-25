package com.a708.drwa.redis.util;

import com.a708.drwa.redis.domain.DebateRedisKey;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyUtil {

    // 일반 키 값
    public String getKeyByDebateIdWithKeyword(int debateId, DebateRedisKey key) {
        return "debate:" + debateId + key;
    }

    // MVP와 같이 key에 멤버 정보 또한 필요한 경우
    public String getKeyByDebateIdMemberIdWithKeyword(int debateId, int memberId, DebateRedisKey key) {
        return "debate:" + debateId + "member:" + memberId + key;
    }

}
