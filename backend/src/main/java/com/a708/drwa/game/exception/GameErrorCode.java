package com.a708.drwa.game.exception;

import com.a708.drwa.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GameErrorCode implements ErrorCode {

    BAD_REQUEST(400, "Bad Request", "필수 정보가 누락되었거나 형식과 다른 데이터를 요청하셨습니다."),
    UNAUTHORIZED(401, "Unauthorized", "사용자 인증 정보를 찾을 수 없습니다."),
    FORBIDDEN(403, "Forbidden", "해당 요청은 수행할 수 없습니다."),
    DEBATE_NOT_FOUND(404, "Debate Not Found", "토론을 찾을 수 없습니다."),
    GAME_NOT_FOUND(404, "Game Not Found", "개임을 찾을 수 없습니다.");


    private final int statusCode;
    private final String errorCode;
    private final String message;

}
