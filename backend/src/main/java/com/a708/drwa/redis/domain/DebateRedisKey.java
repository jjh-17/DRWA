package com.a708.drwa.redis.domain;

public enum DebateRedisKey {
    // 방 설정
    ROOM_INFO("roomInfo"),
    // 전체 유저 리스트
    DEBATE_MEMBER_LIST("debateMember"),
    // 팀 별 유저리스트
    TEAM_A("teamA"),
    TEAM_B("teamB"),
    JUROR("juror"),
    VIEWER("viewer"),
    // 페이즈
    PHASE("phase"),
    // 현재 발언자
    CURRENT_SPEAKER("currentSpeaker"),
    // MVP
    MVP("mvp"),
    // A팀 투표율
    VOTE_TEAM_A("voteTeamA"),
    // B팀 투표율
    VOTE_TEAM_B("voteTeamB"),
    // 시작 시간
    START_TIME("startTime"),
    // 키워드
    KEYWORD_A("keywordA"),
    KEYWORD_B("keywordB"),
    ;

    private final String string;

    DebateRedisKey(String string) {
        this.string = string;
    }

    public String string() {
        return string;
    }
}

