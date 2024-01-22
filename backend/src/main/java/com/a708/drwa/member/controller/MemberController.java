package com.a708.drwa.member.controller;

import com.a708.drwa.member.domain.Member;
import com.a708.drwa.member.dto.SocialAuthURLResponse;
import com.a708.drwa.member.dto.SocialUserInfoResponse;
import com.a708.drwa.member.service.Impl.GoogleLoginServiceImpl;
import com.a708.drwa.member.service.Impl.KakaoLoginServiceImpl;
import com.a708.drwa.member.service.Impl.NaverLoginServiceImpl;
import com.a708.drwa.member.service.MemberService;
import com.a708.drwa.member.service.SocialLoginService;
import com.a708.drwa.member.type.SocialType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final GoogleLoginServiceImpl googleLoginService;
    private final NaverLoginServiceImpl naverLoginService;
    private final KakaoLoginServiceImpl kakaoLoginService;
    private final MemberService memberService;

    /**
     * 소셜 로그인 인증 URL을 반환한다.
     * @param socialType : google, naver, kakao
     * @return : 인증 URL
     */
    @GetMapping("/authURL/{socialType}")
    public ResponseEntity<SocialAuthURLResponse> getAuthURL(@PathVariable String socialType) {
        // 소셜 로그인 서비스
        SocialLoginService service = getSocialLoginService(socialType);
        // 응답 DTO
        SocialAuthURLResponse resposneDto = new SocialAuthURLResponse();

        // 지원하지 않는 소셜 로그인 타입
        if (service == null) {
            // 응답 DTO에 에러 메시지 저장
            resposneDto.setErrorMessage("Unsupported social login type");
            // 400 Bad Request 반환
            return ResponseEntity.badRequest().body(resposneDto);
        }

        // 인증 URL 생성
        String authorizationUrl = service.getAuthorizationUrl();
        resposneDto.setAuthorizationUrl(authorizationUrl);
        return ResponseEntity.ok(resposneDto);
    }

    /**
     * 소셜 로그인 후 인증 코드로부터 액세스 토큰을 반환한다.
     * @param socialType : google, naver, kakao
     * @param code : 소셜 로그인 후 인증 코드
     * @return : 액세스 토큰
     */
    @GetMapping("/login/{socialType}")
    public ResponseEntity<?> socialLogin(@PathVariable String socialType, @RequestParam String code) {
        SocialLoginService service = getSocialLoginService(socialType);

        // 지원하지 않는 소셜 로그인 타입
        if (service == null) {
            return ResponseEntity.badRequest().body("Unsupported social login type");
        }

        // 액세스 토큰 반환
        String accessToken = service.getAccessToken(code);

        // 액세스 토큰으로부터 사용자 정보를 반환한다.
        SocialUserInfoResponse responseUserInfoDto = service.getUserInfo(accessToken);

        // 소셜로그인 타입 설정
        responseUserInfoDto.setSocialType(SocialType.fromString(socialType));

        Member member = memberService.handleSocialLogin(responseUserInfoDto);

        return ResponseEntity.ok(accessToken);
    }


    /**
     * 소셜 로그인 서비스를 반환한다.
     * @param socialType : google, naver, kakao
     * @return : 소셜 로그인 서비스
     */
    private SocialLoginService getSocialLoginService(String socialType) {
        switch (socialType) {
            case "google":
                return googleLoginService;
            case "naver":
                return naverLoginService;
            case "kakao":
                return kakaoLoginService;
            default: // 지원하지 않는 소셜 로그인 타입
                return null;
        }
    }




}
