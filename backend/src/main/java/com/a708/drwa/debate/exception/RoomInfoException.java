package com.a708.drwa.debate.exception;

import com.a708.drwa.global.exception.GlobalException;

public class RoomInfoException extends GlobalException {
    public RoomInfoException(RoomInfoErrorCode errorCode) {
        super(errorCode);
    }
}
