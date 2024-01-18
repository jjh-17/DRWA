package com.a708.drwa.member.exception;

import com.a708.drwa.global.exception.ErrorCode;
import com.a708.drwa.global.exception.GlobalException;

public class MemberException extends GlobalException {
    public MemberException(ErrorCode errorCode) {
        super(errorCode);
    }
}
