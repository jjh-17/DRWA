package com.a708.drwa.openvidu.exception;

import com.a708.drwa.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum OpenViduErrorCode implements ErrorCode {

    OPENVIDU_CAN_NOT_CREATE_SESSION(400, "OPENVIDU_01", "세션을 생성할 수 없습니다."),
    OPENVIDU_NOT_FOUND_SESSION(404, "OPENVIDU_02", "존재하지 않는 방입니다."),
    ;

    private final int statusCode;
    private final String errorCode;
    private final String message;

    OpenViduErrorCode(int statusCode, String errorCode, String message) {
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.message = message;
    }
}
