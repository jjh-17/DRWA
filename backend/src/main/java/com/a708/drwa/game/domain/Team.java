package com.a708.drwa.game.domain;

// 참여팀 : 배심원, 팀A, 팀B\
public enum Team {
    JUROR("juror"),
    A("teamA"),
    B("teamB"),
    ;

    private final String string;

    Team(String string) {
        this.string = string;
    }

    public String string() {
        return string;
    }
}
