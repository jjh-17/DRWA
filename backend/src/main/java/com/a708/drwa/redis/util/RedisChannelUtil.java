package com.a708.drwa.redis.util;

import org.springframework.stereotype.Component;

@Component
public class RedisChannelUtil {

    // 토론방 모두가 구독하는 채널
    public String debateChannelKey(int debateId){
        return "debate:"+ debateId;
    }
}
