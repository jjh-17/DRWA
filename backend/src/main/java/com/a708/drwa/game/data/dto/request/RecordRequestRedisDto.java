package com.a708.drwa.game.data.dto.request;

import lombok.Builder;
import lombok.Getter;

// 게임 결과 정산을 위한 데이터를 Redis에서 받아올 때 필요한 Dto
@Builder
@Getter
public class RecordRequestRedisDto {
    // 토론 방 번호
    private int debateId;
}
