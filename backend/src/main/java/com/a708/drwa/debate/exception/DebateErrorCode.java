package com.a708.drwa.debate.exception;

public enum DebateErrorCode {
    NOT_EXIST_DEBATE_ROOM_ERROR(404, "DEBATE_01", "존재하지 않는 토론 방입니다.");

    private final int statusCode;
    private final String errorCode;
    private final String message;

    DebateErrorCode(int statusCode, String errorCode, String message) {
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.message = message;
    }
}
