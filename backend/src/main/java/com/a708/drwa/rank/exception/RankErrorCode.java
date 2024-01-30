package com.a708.drwa.rank.exception;

import com.a708.drwa.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum RankErrorCode implements ErrorCode {

    RANK_NOT_FOUND(404, "RANK_01", "해당 랭크가 존재하지 않습니다.")
    ;

    private final int statusCode;
    private final String errorCode;
    private final String message;

    RankErrorCode(int statusCode, String errorCode, String message) {
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.message = message;
    }
}
