package com.a708.drwa.member.service.Impl;

import com.a708.drwa.member.dto.GoogleUserInfoResponse;
import com.a708.drwa.member.service.SocialLoginService;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
 * Kakao OAuth2.0 로그인 서비스
 */
@Service
@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.kakao")
public class KakaoLoginServiceImpl implements SocialLoginService {

//    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

//    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    /**
     * 카카오 로그인 페이지 URL 생성
     * @return 카카오 로그인 페이지 URL
     */
    @Override
    public String getAuthorizationUrl() {
        return UriComponentsBuilder.fromHttpUrl("https://kauth.kakao.com/oauth/authorize")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .build().encode().toUriString();
    }

    /**
     * 카카오 로그인 후 인증 코드로부터 액세스 토큰을 받아옴
     * @param code 카카오 로그인 후 인증 코드
     * @return 액세스 토큰
     */
    @Override
    public String getAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> params = new HashMap<>();
        params.put("client_id", clientId);
        params.put("code", code);
        params.put("redirect_uri", redirectUri);
        params.put("grant_type", "authorization_code");

        String accessTokenUri = "https://kauth.kakao.com/oauth/token";
        Map<String, String> response = restTemplate.postForObject(accessTokenUri, params, Map.class);

        return response != null ? response.get("access_token") : null;
    }

    /**
     * 카카오 로그인 후 액세스 토큰으로부터 사용자 정보를 가져옴
     * @param accessToken 카카오 로그인 후 발급받은 액세스 토큰
     * @return 사용자 정보
     */
    @Override
    public GoogleUserInfoResponse getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String userInfoUri = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<GoogleUserInfoResponse> response = restTemplate.exchange(
                userInfoUri, HttpMethod.GET, entity, GoogleUserInfoResponse.class);

        return response.getBody();
    }
}
