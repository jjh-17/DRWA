package com.a708.drwa.member.exception;

import com.a708.drwa.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum MemberErrorCode implements ErrorCode {

    EXAMPLE_ERROR_CODE(400, "MEMBER_01", "예시용 에러코드입니다. 상태코드, DOMAIN_##, 메세지 형식으로 만들어주세요.");

    private final int statusCode;
    private final String errorCode;
    private final String message;

    MemberErrorCode(int statusCode, String errorCode, String message) {
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.message = message;
    }
}
