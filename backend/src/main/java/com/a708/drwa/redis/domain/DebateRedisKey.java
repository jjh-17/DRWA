package com.a708.drwa.redis.domain;


public enum DebateRedisKey {
    TITLE(":title"),
    HOST(":host"),
    IS_PRIVATE(":isPrivate"),
    PASSWORD(":password"),
    PLAYER_NUM(":playerNum"),
    JUROR_NUM(":jurorNum"),
    TOTAL_NUM(":totalNum"),
    OPINION_TIME(":opinionTime"),
    READY_TIME(":readyTime"),
    QUESTION_TIME(":questionTime"),
    DEBATE_USER_LIST(":debateUserList"),
    TEAM_A_LIST(":teamAList"),
    TEAM_B_LIST(":teamBList"),
    JUROR_LIST(":jurorList"),
    VIEWER_LIST(":viewerList"),
    KEY_WORD_A(":keywordA"),
    KEY_WORD_B(":keywordB"),
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
