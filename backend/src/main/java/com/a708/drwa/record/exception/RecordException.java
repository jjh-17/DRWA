package com.a708.drwa.record.exception;

import com.a708.drwa.global.exception.ErrorCode;
import com.a708.drwa.global.exception.GlobalException;

public class RecordException extends GlobalException {
    public RecordException(ErrorCode errorCode) {
        super(errorCode);
    }
}
