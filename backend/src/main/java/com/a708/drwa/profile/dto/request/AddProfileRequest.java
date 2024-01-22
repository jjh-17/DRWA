package com.a708.drwa.profile.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AddProfileRequest {
    private Integer memberId;
    private String nickname;
    /**
     * TODO : add profile image
     */
}
