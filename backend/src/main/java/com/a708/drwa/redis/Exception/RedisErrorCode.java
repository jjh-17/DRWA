package com.a708.drwa.redis.Exception;

import com.a708.drwa.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisErrorCode implements ErrorCode {

    REDIS_SELECT_FAIL(500, "Redis Select Fail", "Redis에서 데이터 가져오기에 실패하였습니다."),
    REDIS_INSERT_FAIL(500, "Redis Insert Fail", "Redis에 데이터 저장을 실패하였습니다.");

    private final int statusCode;
    private final String errorCode;
    private final String message;

}