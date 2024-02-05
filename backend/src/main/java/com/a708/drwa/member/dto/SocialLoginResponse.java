package com.a708.drwa.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 소셜 로그인 응답 DTO
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SocialLoginResponse {
    private String userId;
    @Setter
    private String accessToken;

}
