package com.a708.drwa.redis.Exception;

import com.a708.drwa.global.exception.GlobalException;
import lombok.Getter;

@Getter
public class RedisException extends GlobalException {
    private final RedisErrorCode redisErrorCode;

    public RedisException(RedisErrorCode redisErrorCode) {
        super(redisErrorCode);
        this.redisErrorCode = redisErrorCode;
    }
}
