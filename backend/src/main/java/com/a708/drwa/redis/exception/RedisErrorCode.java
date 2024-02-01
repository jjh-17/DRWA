package com.a708.drwa.redis.exception;

import com.a708.drwa.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum RedisErrorCode implements ErrorCode {
    INVALID_KEY_ERROR(400, "REDIS_01", "요청된 키에 해당하는 값이 존재하지 않습니다.")
    ;


    private final int statusCode;
    private final String errorCode;
    private final String message;

    RedisErrorCode(int statusCode, String errorCode, String message) {
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.message = message;
    }
}
