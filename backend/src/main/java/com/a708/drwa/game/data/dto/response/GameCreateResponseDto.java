package com.a708.drwa.game.data.dto.response;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;

// 전적 저장 후 클라이언트에게 전달할 데이터
@Builder
@Getter
public class GameCreateResponseDto {
    // === 투표 수 ===
    private int teamAVoteNum;
    private int teamBVoteNum;
    private int noVoteNum;          // 미투표 수

    // MVP로 선정된 멤버 ID
    private int mvpMemberId;

    // === 획득 포인트 ===
    private int mvpPoint;           // MVP 멤버 획득 포인트
    private int winnerPoint;        // 승리팀 멤버 획득 포인트

    // === 승리 팀 ===
    @Enumerated(EnumType.ORDINAL)
    private WinnerTeam winnerTeam;
}
