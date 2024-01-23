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
    private String access_token;
    private String token_type;
    private String refresh_token;
    private String expires_in;
    private String scope;
    private String refresh_token_expires_in;
}
