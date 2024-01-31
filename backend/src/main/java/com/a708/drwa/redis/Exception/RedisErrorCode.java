package com.a708.drwa.redis.exception;

import com.a708.drwa.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisErrorCode implements ErrorCode {

    REDIS_UPDATE_FAIL(500, "REDIS_01", "Redis 데이터 저장/수정에 실패하였습니다."),
    REDIS_READ_FAIL(500, "REDIS_02", "Redis에서 데이터 읽기에 실패하였습니다."),
    REDIS_DELETE_FAIL(500, "REDIS_03", "Redis에서 데이터 삭제에 실패하였습니다."),
    REDIS_SERVER_ERROR(500, "REDIS_04", "Redis 서버 에러"),
    ;

    private final int statusCode;
    private final String errorCode;
    private final String message;

}