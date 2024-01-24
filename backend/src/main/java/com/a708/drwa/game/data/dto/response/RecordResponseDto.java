package com.a708.drwa.game.data.dto.response;

import com.a708.drwa.game.domain.Result;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;

// 게임 결과 응답 DTO
@Builder
@Getter
public class RecordResponseDto {

    // 게임 결과
    @Enumerated(EnumType.ORDINAL)
    private Result result;

    // 승리 팀 투표 수
    private int winnerTeamVoteNum;

    // 패배 팀 투표 수
    private int loserTeamVoteNum;

    // 미투표 수
    private int noTeamVoteNum;

    // MVP로 선정된 유저 ID
    private int mvpUserId;

    // 획득 포인트(음수 가능)
    private int point;

}
