package com.a708.drwa.profile.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProfileResponse {
    private Integer profileId;
    private Integer memberId;
    private String nickname;
    private String point;
    private String mvpCount;
    // 랭크
}
