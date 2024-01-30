package com.a708.drwa.member.controller;

import com.a708.drwa.member.dto.SocialAuthURLResponse;
import com.a708.drwa.member.dto.SocialLoginResponse;
import com.a708.drwa.member.dto.GoogleUserInfoResponse;
import com.a708.drwa.member.dto.SocialUserInfoResponse;
import com.a708.drwa.member.service.MemberServiceImpl;
import com.a708.drwa.member.service.SocialLoginService;
import com.a708.drwa.member.type.SocialType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {


    private final MemberServiceImpl memberServiceImpl;

    /**
     * 소셜 로그인 인증 URL을 반환한다.
     * @param socialType : google, naver, kakao
     * @return : 인증 URL
     */
    @GetMapping("/authURL/{socialType}")
    public ResponseEntity<SocialAuthURLResponse> getAuthURL(@PathVariable String socialType) {
        // 소셜 로그인 서비스
        SocialLoginService socialLoginService = memberServiceImpl.getSocialLoginService(socialType);
        // 응답 DTO
        SocialAuthURLResponse socialAuthURLResponse = new SocialAuthURLResponse();

        // 지원하지 않는 소셜 로그인 타입
        if (socialLoginService == null) {
            // 응답 DTO에 에러 메시지 저장
            socialAuthURLResponse.setErrorMessage("Unsupported social login type");
            // 400 Bad Request 반환
            return ResponseEntity.badRequest().body(socialAuthURLResponse);
        }

        // 인증 URL 생성
        String authorizationUrl = socialLoginService.getAuthorizationUrl();
        socialAuthURLResponse.setAuthorizationUrl(authorizationUrl);
        return ResponseEntity.ok(socialAuthURLResponse);
    }

    /**
     * 소셜 로그인 후 인증 코드로부터 액세스 토큰을 반환한다.
     * @param socialType : google, naver, kakao
     * @param code : 소셜 로그인 후 인증 코드
     * @return : 액세스 토큰
     */
    @GetMapping("/login/{socialType}")
    public ResponseEntity<?> socialLogin(@PathVariable String socialType, @RequestParam String code) throws UnsupportedEncodingException {
        SocialLoginService socialLoginService = memberServiceImpl.getSocialLoginService(socialType);

        // 지원하지 않는 소셜 로그인 타입
        if (socialLoginService == null) {
            return ResponseEntity.badRequest().body("Unsupported social login type");
        }

        // 액세스 토큰 반환
        String accessToken = socialLoginService.getAccessToken(code);

        log.info("accessToken: {}", accessToken);

        // 액세스 토큰으로부터 사용자 정보를 반환한다.
        SocialUserInfoResponse socialUserInfoResponse = socialLoginService.getUserInfo(accessToken);

        log.info("socialUserInfoResponse: {}", socialUserInfoResponse);

        // 소셜로그인 타입 설정
        socialUserInfoResponse.setSocialType(SocialType.fromString(socialType));

        // 소셜 로그인 처리
        SocialLoginResponse socialLoginResponse = memberServiceImpl.handleSocialLogin(socialUserInfoResponse);

        // 응답 DTO 반환
        return new ResponseEntity<SocialLoginResponse>(socialLoginResponse, null, HttpStatus.OK);
    }




}
