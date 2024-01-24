package com.a708.drwa.game.data.dto.request;

import lombok.Builder;
import lombok.Getter;

// 게임 정보 생성 요청 DTO
@Builder
@Getter
public class GameInfoCreateRequestDto {
    // 토론 방 ID
    private int debateId;
}

