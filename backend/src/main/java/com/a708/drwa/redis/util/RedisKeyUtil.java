package com.a708.drwa.redis.util;

import com.a708.drwa.redis.domain.DebateRedisKey;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyUtil {
    public String getKeyByRoomIdWithKeyword(String... key) {
        return String.join(":", key);
    }
}
