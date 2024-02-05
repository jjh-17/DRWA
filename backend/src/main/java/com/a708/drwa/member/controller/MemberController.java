package com.a708.drwa.member.controller;

import com.a708.drwa.annotation.AuthRequired;
import com.a708.drwa.member.dto.SocialAuthURLResponse;
import com.a708.drwa.member.dto.SocialLoginRequest;
import com.a708.drwa.member.dto.SocialLoginResponse;
import com.a708.drwa.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    @Value("${jwt.accesstoken.expiretime}")
    private Long accessTokenExpireTime;

    private final MemberService memberService;

    /**
     * 소셜 로그인 인증 URL을 반환한다.
     *
     * @param socialType : google, naver, kakao
     * @return : 인증 URL
     */
    @GetMapping("/authURL/{socialType}")
    public ResponseEntity<?> getAuthURL(@PathVariable String socialType) {
        // 소셜 로그인 타입에 따른 인증 URL 반환
        String authorizationUrl = memberService.getAuthorizationUrl(socialType);
        return ResponseEntity.ok(new SocialAuthURLResponse(authorizationUrl));
    }

    /**
     * 소셜 로그인 후 인증 코드로부터 액세스 토큰을 반환한다.
     *
     * @param requestBody : 소셜 로그인 타입과 코드
     * @return : 액세스 토큰
     */
    @PostMapping("/login")
    public ResponseEntity<?> socialLogin(@RequestBody SocialLoginRequest requestBody, HttpServletResponse response) {
        // 요청 바디에서 소셜 로그인 타입과 코드 추출
        String socialType = requestBody.getSocialType();
        String code = requestBody.getCode();
        log.info("socialType : " + socialType);
        log.info("code : " + code);
        // 소셜 로그인 타입에 따른 액세스 토큰 반환
        SocialLoginResponse socialLoginResponse = memberService.processSocialLogin(socialType, code);

        // accessToken을 httpOnly 쿠키에 담아서 반환
        Cookie cookie = new Cookie("accessToken", socialLoginResponse.getAccessToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setMaxAge(accessTokenExpireTime.intValue());
        response.addCookie(cookie);

        // 엑세스토큰은 javascript에서 접근할 수 없도록 httpOnly 쿠키에 담아서 반환
        socialLoginResponse.setAccessToken(null);

        return ResponseEntity.ok(socialLoginResponse);
    }


    /**
     * 사용자 정보 삭제
     *
     * @param userId : 사용자 아이디
     * @return : 삭제 성공 여부
     */
    @DeleteMapping("/deleteAccount")
    @AuthRequired
    public ResponseEntity<?> deleteAccount(@RequestParam String userId, HttpServletResponse response) {
        // 계정 정보 삭제
        memberService.deleteMember(userId);

        // 쿠키를 삭제하기 위해 Max-Age를 0으로 설정
        Cookie cookie = new Cookie("accessToken", null); // 쿠키 이름은 로그인 때 사용한 것과 동일하게
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0); // 쿠키를 즉시 만료시킵니다.
        response.addCookie(cookie);

        return ResponseEntity.ok().body("Account successfully deleted.");
    }

    /**
     * 로그아웃
     *
     * @param userId : 사용자 아이디
     * @return : 로그아웃 성공 여부
     */
    @PostMapping("/logout")
    @AuthRequired
    public ResponseEntity<?> logout(@RequestParam String userId, HttpServletResponse response) {
        // Redis에서 리프레시 토큰 삭제
        memberService.deleteRefreshToken(userId);

        // 쿠키를 삭제하기 위해 Max-Age를 0으로 설정
        Cookie cookie = new Cookie("accessToken", null); // 쿠키 이름은 로그인 때 사용한 것과 동일하게
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0); // 쿠키를 즉시 만료시킵니다.
        response.addCookie(cookie);

        return ResponseEntity.ok().body("Successfully logged out.");
    }


}
