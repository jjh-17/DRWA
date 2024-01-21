package com.a708.drwa.gameinfo.exception;

import com.a708.drwa.global.exception.ErrorCode;
import com.a708.drwa.global.exception.GlobalException;

public class GameInfoException extends GlobalException {
    public GameInfoException(ErrorCode errorCode) {
        super(errorCode);
    }
}
