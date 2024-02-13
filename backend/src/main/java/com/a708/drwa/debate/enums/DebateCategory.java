package com.a708.drwa.debate.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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

    @JsonValue // Jackson이 열거형을 직렬화할 때 이 메소드의 반환 값을 사용하게 함
    public String getValue() {
        return value;
    }

//    /**
//     * JSON 문자열을 Enum으로 변환한다. (대소문자 구분 없이)
//     *
//     * @param value JSON 문자열
//     * @return Enum
//     */
//    @JsonCreator // Jackson 라이브러리에 의해 사용됨
//    public static DebateCategory forValue(String value) {
//        for (DebateCategory category : DebateCategory.values()) {
//            if (category.name().equalsIgnoreCase(value)) {
//                return category;
//            }
//        }
//        throw new IllegalArgumentException("Invalid category: " + value);
//    }
}
