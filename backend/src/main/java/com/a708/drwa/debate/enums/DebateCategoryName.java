package com.a708.drwa.debate.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DebateCategoryName {
    LOVE("LOVE"), ECONOMY("ECONOMY"), SPORTS("SPORTS"),
    ANIMAL("ANIMAL"), SHOPPING("SHOPPING"), FOOD("FOOD"),
    POLITICS("POLITICS"), SOCIETY("SOCIETY"), CHARACTER("CHARACTER"),
    CULTURE("CULTURE"), ETC("ETC");

    private final String name;
}
