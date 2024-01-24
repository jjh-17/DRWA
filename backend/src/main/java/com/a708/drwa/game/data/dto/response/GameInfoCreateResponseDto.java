package com.a708.drwa.game.data.dto.response;

import lombok.Builder;
import lombok.Getter;

// 게임 정보 생성 응답 DTO
@Builder
@Getter
public class GameInfoCreateResponseDto {
    // 생성된 게임 Id
    private int gameId;
}
