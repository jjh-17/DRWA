package com.a708.drwa.member.controller;

import com.a708.drwa.member.dto.SocialAuthURLResponse;
import com.a708.drwa.member.dto.SocialLoginResponse;
import com.a708.drwa.member.service.MemberService;
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


    private final MemberService memberService;

    /**
     * 소셜 로그인 인증 URL을 반환한다.
     * @param socialType : google, naver, kakao
     * @return : 인증 URL
     */
    @GetMapping("/authURL/{socialType}")
    public ResponseEntity<?> getAuthURL(@PathVariable String socialType) {
        try {
            // 소셜 로그인 타입에 따른 인증 URL 반환
            String authorizationUrl = memberService.getAuthorizationUrl(socialType);
            return ResponseEntity.ok(new SocialAuthURLResponse(authorizationUrl));
        } catch (IllegalArgumentException e) {
            // 지원하지 않는 소셜 로그인 타입
            return ResponseEntity.badRequest().body("Unsupported social login type");
        }
    }

    /**
     * 소셜 로그인 후 인증 코드로부터 액세스 토큰을 반환한다.
     * @param socialType : google, naver, kakao
     * @param code : 소셜 로그인 후 인증 코드
     * @return : 액세스 토큰
     */
    @GetMapping("/login/{socialType}")
    public ResponseEntity<?> socialLogin(@PathVariable String socialType, @RequestParam String code) throws UnsupportedEncodingException {
        try {
            // 소셜 로그인 타입에 따른 액세스 토큰 반환
            SocialLoginResponse socialLoginResponse = memberService.processSocialLogin(socialType, code);
            return ResponseEntity.ok(socialLoginResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Unsupported social login type");
        } catch (Exception e) {
            log.error("Error during social login: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }




}
