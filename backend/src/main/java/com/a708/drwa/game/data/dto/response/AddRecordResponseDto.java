package com.a708.drwa.game.data.dto.response;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

// 전적 저장 후 클라이언트에게 전달할 데이터
@Builder
@Getter
public class AddRecordResponseDto {
    private int teamAVoteNum;               // 팀A 투표 수
    private int teamBVoteNum;               // 팀B 투표 수
    private int noVoteNum;                  // 미투표 수

    private List<Integer> mvpList;          // MVP로 선정된 멤버 ID 리스트
    private int mvpPoint;                   // MVP 추가 포인트

    private int winnerPoint;                // 승리팀 획득 포인트

    @Enumerated(EnumType.STRING)
    private WinnerTeam winnerTeam;          // 승리 팀
}
