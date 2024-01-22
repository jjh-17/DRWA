package com.a708.drwa.global.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ResponseDto {
    private String message;
    private Object body;
    private int statusCode;
    private boolean state;
}