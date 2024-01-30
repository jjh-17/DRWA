package com.a708.drwa.redis.domain;

public enum MemberRedisKey {
    POINT(":point"),
    REFRESH_TOKEN(":refreshToken")
    ;

    private final String string;

    MemberRedisKey(String string) {
        this.string = string;
    }

    public String string() {
        return string;
    }
}
