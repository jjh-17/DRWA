package com.a708.drwa.game.service;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

// 전적 저장을 위해 Redis에서 받아올 정보
@Builder
@Getter
public class PrivateGameRedisResponseDto {
    private List<Object> teamAList;         // A팀 리스트
    private List<Object> teamBList;         // B팀 리스트
    private List<Object> jurorList;         // 배심원 리스트
    private List<Object> viewerList;        // 관전자 리스트

    private Map<Object, Object> mvpMap;   // MVP 정보 맵 => (투표한 memberId, 투표된 memberId)
    
    private int teamAVoteNum;               // 팀A 투표 수
    private int teamBVoteNum;               // 팀B 투표 수
    
    private String keywordA;                 // 키워드 1
    private String keywordB;                // 키워드 2

    private boolean isPrivate;
}
