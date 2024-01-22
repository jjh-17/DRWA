package com.a708.drwa.debate.exception;

import com.a708.drwa.global.exception.GlobalException;

public class DebateException extends GlobalException {
    public DebateException(DebateErrorCode errorCode) {
        super(errorCode);
    }
}
