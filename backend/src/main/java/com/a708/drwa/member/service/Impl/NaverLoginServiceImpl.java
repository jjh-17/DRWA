package com.a708.drwa.member.service.Impl;

import com.a708.drwa.member.dto.response.NaverUserInfoResponse;
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
        HttpHeaders headers = new HttpHeaders();
        // 헤더에 'Content-Type: application/x-www-form-urlencoded' 설정
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", code);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
        String accessTokenUri = "https://nid.naver.com/oauth2.0/token";

        // POST 요청으로 액세스 토큰 발급
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(accessTokenUri, requestEntity, Map.class);

        // 'response' 변수에서 'access_token' 추출
        if (response.getBody() != null && response.getBody().containsKey("access_token")) {
            return response.getBody().get("access_token").toString();
        } else {
            // 에러 메시지 로깅
            log.error("Failed to retrieve access token. Response: {}", response);
            return null;
        }
    }

    /**
     * 네이버 로그인 후 액세스 토큰으로부터 사용자 정보를 받아옴
     * @param accessToken 네이버 로그인 후 발급받은 액세스 토큰
     * @return 사용자 정보
     */
    @Override
    public NaverUserInfoResponse getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String userInfoUri = "https://openapi.naver.com/v1/nid/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<NaverUserInfoResponse> response = restTemplate.exchange(
                userInfoUri, HttpMethod.GET, entity, NaverUserInfoResponse.class);
        log.info("Response from Naver: {}", response.getBody());

        return response.getBody();
    }
}
