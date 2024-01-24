package com.a708.drwa.redis.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisMessageUtil {
    // ObjectMapper : JSON 형식을 사용할 때,
    // 응답들을 직렬화(Object -> String)
    // 요청들을 역직렬화(String -> Object)
    private final ObjectMapper objectMapper;

    // 이벤트

}
