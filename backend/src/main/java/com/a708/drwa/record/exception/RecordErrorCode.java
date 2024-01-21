package com.a708.drwa.record.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecordErrorCode {

    EXAMPLE_ERROR_CODE(400, "RECORD_01", "예시용 에러코드입니다. 상태코드, DOMAIN_##, 메세지 형식으로 만들어주세요.");

    private final int statusCode;
    private final String errorCode;
    private final String message;

}
