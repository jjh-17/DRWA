package com.a708.drwa.member.exception;

import com.a708.drwa.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum MemberErrorCode implements ErrorCode {

    EXAMPLE_ERROR_CODE(400, "MEMBER_01", "예시용 에러코드입니다. 상태코드, DOMAIN_##, 메세지 형식으로 만들어주세요."), // 예시용 에러코드
    UNSUPPORTED_SOCIAL_LOGIN_TYPE(400, "MEMBER_02", "지원하지 않는 소셜 로그인 타입입니다."),
    TOKEN_NOT_FOUND(401, "MEMBER_03", "토큰이 존재하지 않습니다."),
    INVALID_TOKEN(403, "MEMBER_04", "유효하지 않은 토큰입니다."),
    MEMBER_NOT_FOUND(404, "MEMBER_05", "해당 유저를 찾을 수 없습니다."),
    TOO_MANY_CATEGORIES(400, "MEMBER_06", "관심사는 최대 3개까지 지정할 수 있습니다."),

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
