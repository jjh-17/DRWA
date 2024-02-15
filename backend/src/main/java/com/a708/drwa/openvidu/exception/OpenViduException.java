package com.a708.drwa.openvidu.exception;

import com.a708.drwa.global.exception.ErrorCode;
import com.a708.drwa.global.exception.GlobalException;

public class OpenViduException extends GlobalException {

    public OpenViduException(ErrorCode errorCode) {
        super(errorCode);
    }
}
