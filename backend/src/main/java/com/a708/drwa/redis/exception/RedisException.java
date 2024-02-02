package com.a708.drwa.redis.exception;

import com.a708.drwa.global.exception.GlobalException;
import lombok.Getter;

@Getter
public class RedisException extends GlobalException {
    public RedisException(RedisErrorCode redisErrorCode) {
        super(redisErrorCode);
    }
}
