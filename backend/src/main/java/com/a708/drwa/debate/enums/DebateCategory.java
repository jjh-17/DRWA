package com.a708.drwa.debate.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DebateCategory {
    LOVE("LOVE"),
    ECONOMY("ECONOMY"),
    SPORTS("SPORTS"),
    ANIMAL("ANIMAL"),
    SHOPPING("SHOPPING"),
    FOOD("FOOD"),
    POLITICS("POLITICS"),
    SOCIETY("SOCIETY"),
    CHARACTER("CHARACTER"),
    CULTURE("CULTURE"),
    ETC("ETC"),
    ;

    private final String value;
}
