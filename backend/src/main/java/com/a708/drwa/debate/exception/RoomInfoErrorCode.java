package com.a708.drwa.debate.exception;

import com.a708.drwa.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum RoomInfoErrorCode implements ErrorCode {
    ROOM_INFO_OMISSION_ERROR(404, "ROOM_INFO_01", "값이 저장되어 있지 않습니다."),
    ;

    private final int statusCode;
    private final String errorCode;
    private final String message;

    RoomInfoErrorCode(int statusCode, String errorCode, String message) {
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.message = message;
    }
}
