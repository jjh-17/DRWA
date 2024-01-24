package com.a708.drwa.game.data.dto.request;

import lombok.Builder;
import lombok.Getter;

// 게임 결과 요청 DTO
@Builder
@Getter
public class RecordRequestDto {
    // 토론 방 ID
    private int debateId;

    // 게임 결과를 요청한 멤버 ID
    private int memberId;

    // 게임 결과를 요청한 유저 ID
    private int userId;

    // 현재 게임 번호
    private int gameId;

    // MVP 선정 멤버 ID
    private int mvpMemberId;
}
