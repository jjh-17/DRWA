package com.a708.drwa.redis.Exception;

import com.a708.drwa.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisErrorCode implements ErrorCode {

    REDIS_UPDATE_FAIL(500, "Redis Craete Fail", "Redis 데이터 자장/수정에 실패하였습니다."),
    REDIS_READ_FAIL(500, "Redis Read Fail", "Redis에서 데이터 읽기에 실패하였습니다."),
    REDIS_DELETE_FAIL(500, "Redis Delete Fail", "Redis에서 데이터 삭제에 실패하였습니다.");

    private final int statusCode;
    private final String errorCode;
    private final String message;

}