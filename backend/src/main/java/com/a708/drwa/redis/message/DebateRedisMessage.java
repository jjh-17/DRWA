package com.a708.drwa.redis.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Setter;

import java.util.List;

@Setter
@JsonSerialize
public class DebateRedisMessage {
    // 무슨 이벤트?
    private String event;

    // 방 생성
    private String title;
    private String keyword;
    private Boolean isPrivate;
    private Integer password;
    // playerNum 2->1:1      4->2:2     6->3:3
    private Integer playerNum;
    private Integer opinionTime;
    private Integer readyTime;
    private Integer questionTime;

    // 대기방
    // 플레이어, 배심원
    private List<String> teamAList;
    private List<String> teamBList;
    private List<String> jurorList;
    // 관전자 수
    private Integer viewerCnt;

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
    public String getTitle() {
        return title;
    }

    @JsonProperty
    public String getKeyword() {
        return keyword;
    }

    @JsonProperty
    public Boolean getPrivate() {
        return isPrivate;
    }

    @JsonProperty
    public Integer getPassword() {
        return password;
    }

    @JsonProperty
    public Integer getPlayerNum() {
        return playerNum;
    }

    @JsonProperty
    public Integer getOpinionTime() {
        return opinionTime;
    }

    @JsonProperty
    public Integer getReadyTime() {
        return readyTime;
    }

    @JsonProperty
    public Integer getQuestionTime() {
        return questionTime;
    }

    @JsonProperty
    public List<String> getTeamAList() {
        return teamAList;
    }

    @JsonProperty
    public List<String> getTeamBList() {
        return teamBList;
    }

    @JsonProperty
    public List<String> getJurorList() {
        return jurorList;
    }

    @JsonProperty
    public Integer getViewerCnt() {
        return viewerCnt;
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
