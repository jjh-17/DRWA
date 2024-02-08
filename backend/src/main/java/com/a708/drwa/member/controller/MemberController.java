package com.a708.drwa.member.controller;

import com.a708.drwa.annotation.AuthRequired;
import com.a708.drwa.debate.enums.DebateCategory;
import com.a708.drwa.member.dto.response.SocialAuthURLResponse;
import com.a708.drwa.member.dto.request.SocialLoginRequest;
import com.a708.drwa.member.dto.response.SocialLoginResponse;
import com.a708.drwa.member.service.MemberInterestService;
import com.a708.drwa.member.service.MemberService;
import com.a708.drwa.member.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {


    private final MemberService memberService;
    private final MemberInterestService memberInterestService;
    private final JWTUtil jwtUtil;

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
    public ResponseEntity<?> socialLogin(@RequestBody SocialLoginRequest requestBody) {
        // 요청 바디에서 소셜 로그인 타입과 코드 추출
        String socialType = requestBody.getSocialType();
        String code = requestBody.getCode();
        log.info("socialType : " + socialType);
        log.info("code : " + code);
        // 소셜 로그인 타입에 따른 액세스 토큰 반환
        SocialLoginResponse socialLoginResponse = memberService.processSocialLogin(socialType, code);
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
    public ResponseEntity<?> deleteAccount(@RequestParam String userId) {
        // 계정 정보 삭제
        memberService.deleteMember(userId);
        return ResponseEntity.ok().body("Account successfully deleted.");
        // TODO : 관심 카테고리 삭제
        
        // TODO : 프로필 이미지 삭제
        // TODO : 랭킹 삭제. -> 그냥 flush 이런 거 없나
    }

    /**
     * 로그아웃
     *
     * @param userId : 사용자 아이디
     * @return : 로그아웃 성공 여부
     */
    @PostMapping("/logout")
    @AuthRequired
    public ResponseEntity<?> logout(@RequestParam String userId) {
        // Redis에서 리프레시 토큰 삭제
        memberService.deleteRefreshToken(userId);
        return ResponseEntity.ok().body("Successfully logged out.");
    }

    /**
     * 멤버 관심 카테고리 업데이트
     *
     * @param request
     * @param categories
     * @return
     * @throws ParseException
     */
    @AuthRequired
    @PostMapping("/update/interests")
    public ResponseEntity<?> updateInterests(HttpServletRequest request, @RequestBody List<DebateCategory> categories) throws ParseException {
        log.info("categories : " + categories);

        String authorizationHeader = request.getHeader("Authorization");
        log.info("authorizationHeader : " + authorizationHeader);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Invalid Authorization header.");
        }

        String token = authorizationHeader.substring(7); // "Bearer " 다음 부분을 추출합니다
        int memberId = jwtUtil.getMemberId(token);

        memberInterestService.updateMemberInterests(memberId, categories);
        return ResponseEntity.ok().build();
    }


}
