package com.a708.drwa.debate.domain.enums;

public enum Role {
    A_TEAM("teamA"),
    B_TEAM("teamB"),
    JUROR("juror"),
    WATCHER("watcher");

    private final String string;

    Role(String string) {
        this.string = string;
    }

    public String string() {
        return string;
    }
}
