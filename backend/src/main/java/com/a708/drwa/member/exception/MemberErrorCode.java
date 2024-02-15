package com.a708.drwa.member.exception;

import com.a708.drwa.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MemberErrorCode implements ErrorCode {
    UNSUPPORTED_SOCIAL_LOGIN_TYPE(400, "MEMBER_01", "지원하지 않는 소셜 로그인 타입입니다."),
    TOKEN_NOT_FOUND(401, "MEMBER_02", "토큰이 존재하지 않습니다."),
    INVALID_TOKEN(403, "MEMBER_03", "유효하지 않은 토큰입니다."),
    MEMBER_NOT_FOUND(404, "MEMBER_04", "해당 유저를 찾을 수 없습니다."),
    TOO_MANY_CATEGORIES(400, "MEMBER_05", "관심사는 최대 3개까지 지정할 수 있습니다."),
    YOU_ARE_BANNED(401, "MEMBER_06", "신고 횟수가 누적되어 제재되었습니다."),

    ;
    private final int statusCode;
    private final String errorCode;
    private final String message;

    MemberErrorCode(int statusCode, String errorCode, String message) {
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.message = message;
    }
}
