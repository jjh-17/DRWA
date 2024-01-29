package com.a708.drwa.member.service.Impl;

import com.a708.drwa.member.dto.SocialUserInfoResponse;
import com.a708.drwa.member.service.SocialLoginService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
 * Naver OAuth2.0 로그인 서비스
 */
@Slf4j
@Service
@Setter
@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.naver")
public class NaverLoginServiceImpl implements SocialLoginService {

//    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String clientId;

//    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String clientSecret;

//    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String redirectUri;

    /**
     * 네이버 로그인 페이지 URL 생성
     * @return 네이버 로그인 페이지 URL
     */
    @Override
    public String getAuthorizationUrl() {
        log.info("clientId: {}, redirectUri: {}", clientId, redirectUri);

        return UriComponentsBuilder.fromHttpUrl("https://nid.naver.com/oauth2.0/authorize")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("state", "RANDOM_STATE") // 상태 토큰은 보안을 위해 무작위 문자열을 사용해야 합니다.
                .build().encode().toUriString();
    }


    /**
     * 네이버 로그인 후 인증 코드로부터 액세스 토큰을 받아옴
     * @param code 네이버 로그인 후 인증 코드
     * @return 액세스 토큰
     */
    @Override
    public String getAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> params = new HashMap<>();
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("code", code);
//        params.put("state", state);
        params.put("redirect_uri", redirectUri);
        params.put("grant_type", "authorization_code");

        String accessTokenUri = "https://nid.naver.com/oauth2.0/token";
        Map<String, String> response = restTemplate.postForObject(accessTokenUri, params, Map.class);

        return response != null ? response.get("access_token") : null;
    }

    /**
     * 네이버 로그인 후 액세스 토큰으로부터 사용자 정보를 받아옴
     * @param accessToken 네이버 로그인 후 발급받은 액세스 토큰
     * @return 사용자 정보
     */
    @Override
    public SocialUserInfoResponse getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String userInfoUri = "https://openapi.naver.com/v1/nid/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<SocialUserInfoResponse> response = restTemplate.exchange(
                userInfoUri, HttpMethod.GET, entity, SocialUserInfoResponse.class);

        return response.getBody();
    }
}
