package com.a708.drwa.game.exception;

import com.a708.drwa.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GameErrorCode implements ErrorCode {

    BAD_REQUEST(400, "Bad Request", "필수 정보가 누락되었거나 정보가 일치하지 않습니다."),
    DEBATE_NOT_FOUND(404, "Debate Not Found", "토론방을 찾을 수 없습니다."),
    GAME_NOT_FOUND(404, "Game Not Found", "개임을 찾을 수 없습니다."),
    MEMBER_NOT_FOUND(404, "Member Not Found", "회원을 찾을 수 없습니다."),
    BATCH_UPDATE_FAIL(500, "Record Batch Update Fail", "전적 정보 업데이트에 실패하였습니다."),
    REDIS_GET_FAIL(500, "Redis Get Fail", "Redis에서 데이터 가져오기에 실패하였습니다."),
    REDIS_POST_FAIL(500, "Redis Update Fail", "Redis에 데이터 저장을 실패하였습니다.");

    private final int statusCode;
    private final String errorCode;
    private final String message;

}
