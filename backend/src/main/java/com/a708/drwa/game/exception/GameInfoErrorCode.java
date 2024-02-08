package com.a708.drwa.game.exception;

import com.a708.drwa.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GameInfoErrorCode implements ErrorCode {

    GAME_INFO_CREATE_FAIL(500, "GAME_INFO_01", "게임 정보 저장 실패"),
    GAME_INFO_MISMATCH(500, "GAME_INFO_02", "게임 정보 불일치"),
    ;

    private final int statusCode;
    private final String errorCode;
    private final String message;

}
