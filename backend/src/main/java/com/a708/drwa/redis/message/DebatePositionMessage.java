package com.a708.drwa.redis.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Setter;

import java.util.List;

@Setter
@JsonSerialize
public class DebatePositionMessage {
    // 자신의 역할 선택할 때
    private String event;

    private List<Integer> teamAList;
    private List<Integer> teamBList;
    private List<Integer> jurorList;
    private List<Integer> viewerList;

    @JsonProperty
    public String getEvent() {
        return event;
    }
    @JsonProperty
    public List<Integer> getTeamAList() {
        return teamAList;
    }

    @JsonProperty
    public List<Integer> getTeamBList() {
        return teamBList;
    }

    @JsonProperty
    public List<Integer> getJurorList() {
        return jurorList;
    }

    @JsonProperty
    public List<Integer> getViewerList() {
        return viewerList;
    }
}
