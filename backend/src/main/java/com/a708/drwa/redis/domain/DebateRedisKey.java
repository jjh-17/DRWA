package com.a708.drwa.redis.domain;


public enum DebateRedisKey {
    TITLE(":title"),
    IS_PRIVATE(":isPrivate"),
    PASSWORD(":password"),
    PLAYER_NUM(":playerNum"),
    TOTAL_NUM(":totalNum"),
    OPINION_TIME(":opinionTime"),
    READY_TIME(":readyTime"),
    QUESTION_TIME(":questionTime"),
    TEAM_A_LIST(":teamAList"),
    TEAM_B_LIST(":teamBList"),
    JUROR_LIST(":jurorList"),
    VIEWER_LIST(":viewerList"),
    KEY_WORD(":keyWord"),
    PHASE(":phase"),
    CURRENT_SPEAKER(":currentSpeaker"),
    MVP(":mvp"),
    VOTE_TEAM_A(":voteTeamA"),
    VOTE_TEAM_B(":voteTeamB"),
    ;

    private final String string;

    DebateRedisKey(String string) {
        this.string = string;
    }

    public String string() {
        return string;
    }
}