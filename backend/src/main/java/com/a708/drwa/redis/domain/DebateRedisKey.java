package com.a708.drwa.redis.domain;

public enum DebateRedisKey {
    PHASE(":phase"),
    PHASE_DETAIL(":phaseDetail"),
    
    ;

    private final String string;

    DebateRedisKey(String string) {
        this.string = string;
    }

    public String string() {
        return string;
    }
}
