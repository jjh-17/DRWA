package com.a708.drwa.redis.util;

import com.a708.drwa.redis.domain.DebateRedisKey;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyUtil {

    // 일반 키 값
    public String getKeyByDebateIdWithKeyword(int debateId, DebateRedisKey key) {
        return "debate:" + debateId + key;
    }

}
