package com.a708.drwa.redis.exception;

import com.a708.drwa.global.exception.GlobalException;

public class RedisException extends GlobalException {
    public RedisException(RedisErrorCode redisErrorCode) {
        super(redisErrorCode);
    }
}
