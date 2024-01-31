package com.a708.drwa.game.exception;

import com.a708.drwa.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GameErrorCode implements ErrorCode {

    BAD_REQUEST(400, "Bad Request", "필수 정보가 누락되었거나 정보가 일치하지 않습니다."),
    DEBATE_ID_MISMATCH(500, "Debate MISMATCH", "인자로 받은 토론방과 검색한 토론방의 ID가 일치하지 않습니다.."),
    RECORD_BATCH_INSERT_FAIL(500, "Record Batch Insert Fail", "전적 저장에 실패하였습니다."),
    GAME_INFO_INSERT_FAIL(500, "GameInfo Insert Fail", "게임 정보 저장에 실패하였습니다."),
    GAME_INFO_INSERT_MISMATCH(500, "GameInfo Insert Mismatch", "생성한 게임 정보와 저장된 게임 정보가 다릅니다."),
    ;

    private final int statusCode;
    private final String errorCode;
    private final String message;

}
