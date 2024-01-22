package com.a708.drwa.member.service.Impl;

import com.a708.drwa.member.dto.SocialUserInfoResponse;
import com.a708.drwa.member.service.SocialLoginService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Google OAuth2 인증을 위한 서비스
 */
@Service
public class GoogleLoginServiceImpl implements SocialLoginService {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.registration.google.scope}")
    private String scope;

    /**
     * Google OAuth2 인증 URL 생성
     * @return Google OAuth2 인증 URL
     */
    @Override
    public String getAuthorizationUrl() {
        return UriComponentsBuilder.fromHttpUrl("https://accounts.google.com/o/oauth2/v2/auth")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", scope)
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .build().encode().toUriString();
    }

    /**
     * Google OAuth2 인증 코드로부터 Access Token을 발급받는다.
     * @param code Google OAuth2 인증 코드
     * @return Access Token
     */
    @Override
    public String getAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> params = new HashMap<>();
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("code", code);
        params.put("redirect_uri", redirectUri);
        params.put("grant_type", "authorization_code");

        String accessTokenUri = "https://oauth2.googleapis.com/token";
        Map<String, String> response = restTemplate.postForObject(accessTokenUri, params, Map.class);

        return response != null ? response.get("access_token") : null;
    }

    /**
     * Google OAuth2 Access Token으로부터 사용자 정보를 가져온다.
     * @param accessToken Google OAuth2 Access Token
     * @return 사용자 정보
     */
    @Override
    public SocialUserInfoResponse getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String userInfoUri = "https://www.googleapis.com/oauth2/v2/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<SocialUserInfoResponse> response = restTemplate.exchange(
                userInfoUri, HttpMethod.GET, entity, SocialUserInfoResponse.class);

        return response.getBody();
    }
}
