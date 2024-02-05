package com.a708.drwa.redis.domain;

public enum MemberRedisKey {
    POINT(":point"),
    REFRESH_TOKEN(":refreshToken"),
    winCount(":winCount"),
    LOSE_COUNT(":loseCount"),
    ACHIEVEMENTS(":achievement"),
    MVP_COUNT(":mvpCount"),
    ;

    private final String string;

    MemberRedisKey(String string) {
        this.string = string;
    }

    public String string() {
        return string;
    }
}
