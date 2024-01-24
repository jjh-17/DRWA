package com.a708.drwa.game.data.dto.request;

import lombok.Builder;
import lombok.Getter;

// 게임 정보 저장을 위한 데이터를 Redis에서 받아올 때 필요한 Dto
@Builder
@Getter
public class GameInfoCreateRequestRedisDto {
    // 토론 방 번호
    private int debateId;
}
