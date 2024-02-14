package com.a708.drwa.debate.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum DebateCategory {
    CHARACTER("CHARACTER"),
    LOVE("LOVE"),
    FOOD("FOOD"),
    ECONOMY("ECONOMY"),
    SPORTS("SPORTS"),
    MUSIC("MUSIC"),
    POLITICS("POLITICS"),
    SOCIETY("SOCIETY"),
    ANIMAL("ANIMAL"),
    CULTURE("CULTURE"),
    ETC("ETC"),
    ;

    private final String value;

    /**
     * JSON 문자열을 Enum으로 변환한다. (대소문자 구분 없이)
     *
     * @param value JSON 문자열
     * @return Enum
     */
    @JsonCreator // Jackson 라이브러리에 의해 사용됨
    public static DebateCategory forValue(String value) {
        for (DebateCategory category : DebateCategory.values()) {
            log.info("category : {}", value);
            if (category.name().equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid category: " + value);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
