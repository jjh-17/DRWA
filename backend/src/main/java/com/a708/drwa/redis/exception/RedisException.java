package com.a708.drwa.redis.exception;

import com.a708.drwa.global.exception.ErrorCode;
import com.a708.drwa.global.exception.GlobalException;

public class RedisException extends GlobalException {
    public RedisException(ErrorCode errorCode) {
        super(errorCode);
    }
}
