package com.a708.drwa.member.data.dto.response;

import lombok.*;

/**
 * 소셜 로그인 인증 URL을 반환하기 위한 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialAuthURLResponse {
    private String authorizationUrl;

    @Builder
    public SocialAuthURLResponse(String authorizationUrl) {
        this.authorizationUrl = authorizationUrl;
    }

}
