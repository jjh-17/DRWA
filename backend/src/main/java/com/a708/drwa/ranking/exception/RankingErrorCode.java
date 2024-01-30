package com.a708.drwa.ranking.exception;

import com.a708.drwa.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum RankingErrorCode implements ErrorCode {
    RANKING_SERVER_ERROR(500, "RANKING_01", "랭킹 서버 오류."),

    ;
    private final int statusCode;
    private final String errorCode;
    private final String message;

    RankingErrorCode(int statusCode, String errorCode, String message) {
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.message = message;
    }
}
