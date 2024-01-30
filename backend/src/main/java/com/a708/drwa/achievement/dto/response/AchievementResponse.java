package com.a708.drwa.achievement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AchievementResponse {
    private String name;
    private String description;
}
