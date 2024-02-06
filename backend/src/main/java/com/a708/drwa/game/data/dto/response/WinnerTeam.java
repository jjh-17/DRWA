package com.a708.drwa.game.data.dto.response;

// 승리한 팀
public enum WinnerTeam {
    A("teamA"),
    B("teamB"),
    TIE("tie"),
    ;

    private final String string;

    WinnerTeam(String string) {
        this.string = string;
    }

    public String string() {
        return string;
    }
}
