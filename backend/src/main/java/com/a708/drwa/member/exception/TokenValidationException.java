package com.a708.drwa.member.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TokenValidationException extends RuntimeException {

    private final HttpStatus httpStatus;

    public TokenValidationException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
