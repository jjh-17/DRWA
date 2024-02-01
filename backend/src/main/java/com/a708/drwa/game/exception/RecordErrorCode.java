package com.a708.drwa.game.exception;

import com.a708.drwa.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecordErrorCode implements ErrorCode {

    RECORD_CREATE_FAIL(500, "RECORD_01", "전적 저장 실패"),
    ;

    private final int statusCode;
    private final String errorCode;
    private final String message;

}
