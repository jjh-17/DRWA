package com.a708.drwa.debate.exception;

import com.a708.drwa.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum DebateErrorCode implements ErrorCode {
    NOT_EXIST_DEBATE_ROOM_ERROR(404, "DEBATE_01", "존재하지 않는 토론 방입니다."),
    ALREADY_EXIST_DEBATE_ROOM_ERROR(400, "DEBATE_02", "이미 존재하는 방입니다."),
    OPENVIDU_INTERNAL_ERROR(404, "DEBATE_03", "서버 에러"),
    INTERNAL_ERROR(500, "DEBATE_04", "내부 에러"),
    CATEGORY_NOT_FOUND(400, "DEBATE_05", "존재하지 않는 카테고리입니다"),
    CAN_NOT_ENTER_AS_DEBATER(400, "DEBATE_06", "자리가 가득 차 참여자로 입장할 수 없습니다."),
    INVALID_ROLE(400, "DEBATE_07", "잘못된 역할로 요청했습니다."),
    ;

    private final int statusCode;
    private final String errorCode;
    private final String message;

    DebateErrorCode(int statusCode, String errorCode, String message) {
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.message = message;
    }
}
