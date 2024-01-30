package com.a708.drwa.redis.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Setter;

import java.util.List;

@Setter
@JsonSerialize
public class JoinMessage {
    // 방 입장 ( 무조건 관전자로 들어옴 )
    private String event;

    private List<String> viewerList;

    @JsonProperty
    public String getEvent() {
        return event;
    }
    @JsonProperty
    public List<String> getViewerList() {
        return viewerList;
    }
}