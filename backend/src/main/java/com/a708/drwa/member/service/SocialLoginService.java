package com.a708.drwa.member.service;

import com.a708.drwa.member.dto.SocialUserInfoResponse;

/**
 * 소셜 로그인 서비스 인터페이스
 */
public interface SocialLoginService {
    /**
     * 로그인 페이지 URL 생성
     * @return 로그인 페이지 URL
     */
    String getAuthorizationUrl();

    /**
     * 로그인 후 인증 코드로부터 액세스 토큰을 받아옴
     * @param code 로그인 후 인증 코드
     * @return 액세스 토큰
     */
    String getAccessToken(String code);

    /**
     * 로그인 후 인증 코드로부터 액세스 토큰을 받아옴
     * @param code 로그인 후 인증 코드
     * @return 액세스 토큰
     */
    SocialUserInfoResponse getUserInfo(String code);
}
