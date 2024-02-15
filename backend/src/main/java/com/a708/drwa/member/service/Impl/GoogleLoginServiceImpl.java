package com.a708.drwa.member.service.Impl;

import com.a708.drwa.member.data.dto.response.GoogleUserInfoResponse;
import com.a708.drwa.member.service.SocialLoginService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

/**
 * Google OAuth2 인증을 위한 서비스
 */
@Slf4j
@Service
@Setter
@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.google")
public class GoogleLoginServiceImpl implements SocialLoginService {

    private String clientId;

    private String clientSecret;

    private String redirectUri;

    private List<String> scope;

    /**
     * Google OAuth2 인증 URL 생성
     * @return Google OAuth2 인증 URL
     */
    @Override
    public String getAuthorizationUrl() {
        // scope 리스트를 공백으로 구분하여 문자열로 변환
        String scopeString = String.join(" ", scope);

        log.info("clientId: {}, redirectUri: {}, scope: {}", clientId, redirectUri, scopeString);

        return UriComponentsBuilder.fromHttpUrl("https://accounts.google.com/o/oauth2/v2/auth")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", scopeString)
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
        try {
            RestTemplate restTemplate = new RestTemplate();
            // Google OAuth2 Access Token 발급 요청 헤더
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // Google OAuth2 Access Token 발급 요청 파라미터
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("code", code);
            params.add("redirect_uri", redirectUri);
            params.add("grant_type", "authorization_code");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            // Google OAuth2 Access Token 발급 URL
            String accessTokenUri = "https://oauth2.googleapis.com/token";

            // Google OAuth2 Access Token 발급 요청
            ResponseEntity<Map> response = restTemplate.postForEntity(accessTokenUri, request, Map.class);

            // Google OAuth2 Access Token 발급 응답
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, String> responseBody = response.getBody();
                return responseBody != null ? responseBody.get("access_token") : null;
            } else {
                // 적절한 오류 처리
                return null;
            }
        } catch (Exception e) {
            // 예외 처리 로직
            // restTemplate.postForEntity 메서드는 요청 실패 시 RestClientException을 발생시킬 수 있다
            return null;
        }
    }

    /**
     * Google OAuth2 Access Token으로부터 사용자 정보를 가져온다.
     * @param accessToken Google OAuth2 Access Token
     * @return 사용자 정보
     */
    @Override
    public GoogleUserInfoResponse getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String userInfoUri = "https://www.googleapis.com/oauth2/v2/userinfo";

        // Google OAuth2 사용자 정보 요청 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        // Google OAuth2 사용자 정보 요청
        ResponseEntity<GoogleUserInfoResponse> response = restTemplate.exchange(
                userInfoUri, HttpMethod.GET, entity, GoogleUserInfoResponse.class);

        // Google OAuth2 사용자 정보 응답
        return response.getBody();
    }
}
