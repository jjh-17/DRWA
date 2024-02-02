package com.a708.drwa.redis.exception;

import com.a708.drwa.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisErrorCode implements ErrorCode {
    INVALID_KEY_ERROR(400, "REDIS_01", "요청된 키에 해당하는 값이 존재하지 않습니다."),
    REDIS_UPDATE_FAIL(500, "REDIS_02", "Redis 데이터 자장/수정에 실패하였습니다."),
    REDIS_READ_FAIL(500, "REDIS_03", "Redis에서 데이터 읽기에 실패하였습니다."),
    REDIS_DELETE_FAIL(500, "REDIS_04", "Redis에서 데이터 삭제에 실패하였습니다.")
    ;

    private final int statusCode;
    private final String errorCode;
    private final String message;

}