package com.a708.drwa.game.data.dto.response;

import lombok.Builder;
import lombok.Getter;

// 게임 정보 저장을 위해 Redis에서 가져와야할 데이터
@Builder
@Getter
public class GameInfoCreateResponseRedisDto {
    // 키워드
    private String keyword;
}
