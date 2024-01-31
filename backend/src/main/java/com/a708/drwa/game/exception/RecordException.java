package com.a708.drwa.game.exception;

import com.a708.drwa.global.exception.GlobalException;
import lombok.Getter;

@Getter
public class RecordException extends GlobalException {

    private final RecordErrorCode recordErrorCode;

    public RecordException(RecordErrorCode recordErrorCode) {
        super(recordErrorCode);
        this.recordErrorCode = recordErrorCode;
    }
}
