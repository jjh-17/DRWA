package com.a708.drwa.redis.domain;

public enum MemberRedisKey {
    POINT("point"),
    REFRESH_TOKEN("refreshToken"),
    WIN_COUNT("winCount"),
    LOSE_COUNT("loseCount"),
    TIE_COUNT("tieCount"),
    ACHIEVEMENTS("achievement"),
    SELECTED_ACHIEVEMENT("selected achievement"),
    RANK_NAME("rankName"),
    MVP_COUNT("mvpCount"),
    ;

    private final String string;

    MemberRedisKey(String string) {
        this.string = string;
    }

    public String string() {
        return string;
    }
}
