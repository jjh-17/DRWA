package com.a708.drwa.auth.exception;

import com.a708.drwa.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    INVALID_TOKEN_ERROR(400, "AUTH_01", "유효하지 않은 토큰입니다."),
    UNSUPPORTED_TOKEN_ERROR(403, "AUTH_02", "지원하지 않는 형식의 토큰입니다."),
    TOKEN_NOT_EXIST_ERROR(404, "AUTH_03", "토큰이 존재하지 않습니다."),
    EXPIRED_TOKEN_ERROR(408, "AUTH_04", "만료된 토큰입니다."),
    NO_INFO_IN_TOKEN_ERROR(412, "AUTH_05", "권한 정보가 존재하지 않습니다."),
    ;

    private final int statusCode;
    private final String errorCode;
    private final String message;
}
