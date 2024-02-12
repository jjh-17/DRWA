package com.a708.drwa.debate.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DebateCategory {
    LOVE("LOVE"),
    ECONOMY("ECONOMY"),
    SPORTS("SPORTS"),
    ANIMAL("ANIMAL"),
    FOOD("FOOD"),
    POLITICS("POLITICS"),
    SOCIETY("SOCIETY"),
    CHARACTER("CHARACTER"),
    CULTURE("CULTURE"),
    MUSIC("MUSIC"),
    SHOPPING("SHOPPING"),
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
            if (category.name().equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid category: " + value);
    }
}
