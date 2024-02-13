package com.a708.drwa.auth.exception;

import com.a708.drwa.global.exception.ErrorCode;
import com.a708.drwa.global.exception.GlobalException;

public class AuthException extends GlobalException {
    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}
