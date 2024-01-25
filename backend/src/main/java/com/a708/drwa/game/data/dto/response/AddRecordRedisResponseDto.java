package com.a708.drwa.game.data.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

// 전적 저장을 위해 Redis에서 받아올 정보
@Builder
@Getter
public class AddRecordRedisResponseDto {
    private List<Object> teamAList;     // A팀 리스트
    private List<Object> teamBList;     // B팀 리스트
    private List<Object> jurorList;     // 배심원 리스트
    private List<Object> viewerList;    // 관전자 리스트
    
    private int teamAVoteNum;           // 팀A 투표 수 
    private int teamBVoteNum;           // 팀B 투표 수
    
    private String keyword;             // 키워드
}
