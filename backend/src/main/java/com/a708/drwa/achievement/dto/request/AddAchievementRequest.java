package com.a708.drwa.achievement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AddAchievementRequest {
    private String name;
    private String description;
}
