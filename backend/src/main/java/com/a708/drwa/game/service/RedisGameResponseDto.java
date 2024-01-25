package com.a708.drwa.game.service;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Builder
@Getter
public class RedisGameResponseDto {
    private List<Object> teamAList;
    private List<Object> teamBList;
    private List<Object> jurorList;
    private List<Object> viewerList;
    private int teamAVoteNum;
    private int teamBVoteNum;
    private String keyword;
}
