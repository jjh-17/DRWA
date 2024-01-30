package com.a708.drwa.achievement.exception;

import com.a708.drwa.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum AchievementErrorCode implements ErrorCode {
    TITLE_NOT_FOUND(404, "TITLE_01", "해당 칭호를 찾을 수 없습니다."),
    TITLE_REPRESENTATIVE_NOT_FOUND(404, "TITLE_02", "대표 칭호가 존재하지 않습니다."),


    ;
    private final int statusCode;
    private final String errorCode;
    private final String message;

    AchievementErrorCode(int statusCode, String errorCode, String message) {
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.message = message;
    }
}
