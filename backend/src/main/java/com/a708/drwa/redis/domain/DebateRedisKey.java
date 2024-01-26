package com.a708.drwa.redis.domain;

public enum DebateRedisKey {
    // 방 제목
    TITLE(":title"),
    // 공개 여부
    IS_PRIVATE(":isPrivate"),
    // 비밀번호
    PASSWORD(":password"),
    // 참여자 인원 제한
    PLAYER_NUM(":playerNum"),
    // 총 인원 수
    TOTAL_NUM(":totalNum"),
    // 발언 시간
    OPINION_TIME(":opinionTime"),
    // 준비 시간
    READY_TIME(":readyTime"),
    // 질의 시간
    QUESTION_TIME(":questionTime"),
    // A팀 유저 목록
    TEAM_A_LIST(":teamAList"),
    // B 팀 유저 목록
    TEAM_B_LIST(":teamBList"),
    // 배심원 리스트
    JUROR_LIST(":jurorList"),
    // 관전자 리스트
    VIEWER_LIST(":viewerList"),
    // 제시어
    KEY_WORD(":keyWord"),
    // 페이즈
    PHASE(":phase"),
    // 현재 발언자
    CURRENT_SPEAKER(":currentSpeaker"),
    // MVP
    MVP(":mvp"),
    // A팀 투표율
    VOTE_TEAM_A(":voteTeamA"),
    // B팀 투표율
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

