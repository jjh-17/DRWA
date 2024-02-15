package com.a708.drwa.debate.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    LEFT("left"),
    RIGHT("right"),
    JUROR("juror"),
    WATCHER("watcher");

    private final String value;

    @JsonCreator
    public static Role forValue(String value) {
        for (Role role : Role.values()) {
            if (role.name().equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid role: " + value);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
