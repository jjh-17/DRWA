package com.a708.drwa.member.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 소셜 로그인 인증 URL을 반환하기 위한 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class SocialAuthURLResponse {
    private String authorizationUrl;
    private String state;
    private String errorMessage;

    public SocialAuthURLResponse(String authorizationUrl) {
        this.authorizationUrl = authorizationUrl;
    }

}
