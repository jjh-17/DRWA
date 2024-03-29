package com.a708.drwa.global.exception.dto;

import com.a708.drwa.global.exception.GlobalException;
import lombok.*;
import org.springframework.http.ResponseEntity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Data
@Builder
public class GlobalExceptionResponse {
    private String message;
    private String errorCode;

    public static ResponseEntity<GlobalExceptionResponse> toResponse(GlobalException e) {
        return ResponseEntity
                .status(e.getStatusCode())
                .body(GlobalExceptionResponse.builder()
                        .errorCode(e.getErrorCode())
                        .message(e.getMessage())
                        .build()
                );
    }
}
