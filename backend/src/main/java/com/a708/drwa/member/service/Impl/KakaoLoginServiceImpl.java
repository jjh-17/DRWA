package com.a708.drwa.member.service.Impl;

import com.a708.drwa.member.data.dto.response.KaKaoUserInfoResponse;
import com.a708.drwa.member.service.SocialLoginService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/**
 * Kakao OAuth2.0 로그인 서비스
 */
@Service
@Setter
@Slf4j
@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.kakao")
public class KakaoLoginServiceImpl implements SocialLoginService {

    private String clientId;

    private String clientSecret;

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
        HttpHeaders headers = new HttpHeaders();
        // 헤더에 Content-Type을 application/x-www-form-urlencoded로 지정
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        String accessTokenUri = "https://kauth.kakao.com/oauth/token";

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(accessTokenUri, request, Map.class);
            if (response.getBody() != null && response.getBody().containsKey("access_token")) {
                return response.getBody().get("access_token").toString();
            }
        } catch (HttpClientErrorException e) {
            log.error("Error getting access token from Kakao: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 카카오 로그인 후 액세스 토큰으로부터 사용자 정보를 가져옴
     * @param accessToken 카카오 로그인 후 발급받은 액세스 토큰
     * @return 사용자 정보
     */
    @Override
    public KaKaoUserInfoResponse getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String userInfoUri = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<KaKaoUserInfoResponse> response = restTemplate.exchange(
                userInfoUri, HttpMethod.GET, entity, KaKaoUserInfoResponse.class);

        return response.getBody();
    }
}
