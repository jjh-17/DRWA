package com.a708.drwa.game.data.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

// 게임 결과를 정산하기 위해 Redis에서 가져와야할 데이터
@Builder
@Getter
public class RecordResponseRedisDto {
    // 팀 A 리스트
    private List<String> teamAList;

    // 팀 B 리스트
    private List<String> teamBList;

    // 배심원 리스트
    private List<String> jurorList;

    // 관전자 리스트
    private List<String> viewerList;

    // 키워드
    private String keyword;

    // 팀 A 투표 수
    private int voteTeamA;

    // 팀 B 투표 수
    private int voteTeamB;
}
