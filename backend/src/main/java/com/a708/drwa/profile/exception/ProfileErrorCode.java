package com.a708.drwa.profile.exception;

import com.a708.drwa.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum ProfileErrorCode implements ErrorCode {
    PROFILE_NOT_FOUND(404, "PROFILE_01", "해당 프로필이 존재하지 않습니다."),

    ;
    private final int statusCode;
    private final String errorCode;
    private final String message;

    ProfileErrorCode(int statusCode, String errorCode, String message) {
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.message = message;
    }
}
