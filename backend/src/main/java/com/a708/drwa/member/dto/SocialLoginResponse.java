package com.a708.drwa.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 소셜 로그인 응답 DTO
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SocialLoginResponse {
    private int memberId;
    private String userId;
    private String access_token;

    public SocialLoginResponse(String userId, String jwtAccessToken) {
    }
}
