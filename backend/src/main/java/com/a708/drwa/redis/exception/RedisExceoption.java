package com.a708.drwa.redis.exception;

import com.a708.drwa.global.exception.GlobalException;

public class RedisExceoption extends GlobalException {
    public RedisExceoption(RedisErrorCode redisErrorCode) {
        super(redisErrorCode);
    }
}
