package com.a708.drwa.redis.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Setter;

@Setter
@JsonSerialize
public class DebateSettingMessage {
    // 방 설정
    private String event;

    // 방 생성
    private String title;
    private String keyword;
    private Boolean isPrivate;
    private Integer password;
    // playerNum 2->1:1      4->2:2     6->3:3
    private Integer playerNum;
    private Integer jurorNum;


    private Integer opinionTime;
    private Integer readyTime;
    private Integer questionTime;

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
    public Integer getJurorNum() {
        return jurorNum;
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
}
