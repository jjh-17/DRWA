package com.a708.drwa.game.data.dto.response;

import lombok.Builder;
import lombok.Getter;

// 전적 저장을 위해 Redis에서 받아올 정보
@Builder
@Getter
public class PrivateGameRedisResponseDto {
    private String keywordA;        // 키워드 1
    private String keywordB;        // 키워드 2
}
