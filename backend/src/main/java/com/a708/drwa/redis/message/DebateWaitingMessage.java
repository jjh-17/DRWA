package com.a708.drwa.redis.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Setter;

import java.util.List;

@Setter
@JsonSerialize
public class DebateWaitingMessage {
    // 페이즈 변경 알려줌
    private String event;

    // 대기방
    // 플레이어, 배심원
    private List<String> teamAList;
    private List<String> teamBList;
    private List<String> jurorList;
    // 관전자 수
    private Integer viewerCnt;

    @JsonProperty
    public String getEvent() {
        return event;
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
}
