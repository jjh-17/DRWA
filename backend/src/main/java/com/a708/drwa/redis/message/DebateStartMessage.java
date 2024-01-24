package com.a708.drwa.redis.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Setter;

@Setter
@JsonSerialize
public class DebateStartMessage {
    // 페이즈 변경 알려줌
    private String event;

    // 토론 시작
    // 진행단계
    private Integer phase;
    private String currentSpeaker;
    private String mvp;
    private Integer voteTeamA;
    private Integer voteTeamB;

    @JsonProperty
    public String getEvent() {
        return event;
    }

    @JsonProperty
    public Integer getPhase() {
        return phase;
    }

    @JsonProperty
    public String getCurrentSpeaker() {
        return currentSpeaker;
    }

    @JsonProperty
    public String getMvp() {
        return mvp;
    }

    @JsonProperty
    public Integer getVoteTeamA() {
        return voteTeamA;
    }

    @JsonProperty
    public Integer getVoteTeamB() {
        return voteTeamB;
    }
}
