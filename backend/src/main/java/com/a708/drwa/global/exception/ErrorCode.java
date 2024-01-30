package com.a708.drwa.global.exception;

public interface ErrorCode {
    int getStatusCode();
    String getErrorCode();
    String getMessage();
}
