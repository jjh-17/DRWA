package com.a708.drwa.member.controller;

import com.a708.drwa.annotation.AuthRequired;
import com.a708.drwa.member.data.dto.request.SocialLoginRequest;
import com.a708.drwa.member.data.dto.request.UpdateInterestRequestDto;
import com.a708.drwa.member.data.dto.response.MemberInterestCategoriesResponse;
import com.a708.drwa.member.data.dto.response.SocialAuthURLResponse;
import com.a708.drwa.member.data.dto.response.SocialLoginResponse;
import com.a708.drwa.member.service.MemberInterestService;
import com.a708.drwa.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {


    private final MemberService memberService;
    private final MemberInterestService memberInterestService;

    /**
     * 소셜 로그인 인증 URL을 반환한다.
     *
     * @param socialType : google, naver, kakao
     * @return : 인증 URL
     */
    @GetMapping("/authURL/{socialType}")
    public ResponseEntity<SocialAuthURLResponse> getAuthURL(@PathVariable String socialType) {
        // 소셜 로그인 타입에 따른 인증 URL 반환
        return ResponseEntity
                .ok(memberService.getAuthorizationUrl(socialType));
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
    public ResponseEntity<String> logout(HttpServletRequest request) {
        // Redis에서 리프레시 토큰 삭제
        memberService.logout(request.getHeader("Authorization"));
        return ResponseEntity.ok().body("Successfully logged out.");
    }

    /**
     * 멤버 관심 카테고리 업데이트
     *
     * @param request
     * @param
     * @return
     * @throws ParseException
     */
    @AuthRequired
    @PostMapping("/interests")
    public ResponseEntity<Void> updateInterests(@RequestBody UpdateInterestRequestDto updateInterestRequestDto, HttpServletRequest request) {
        log.info("debateCategories : {}", updateInterestRequestDto.getDebateCategories().toString());
        memberInterestService.updateMemberInterests(request.getHeader("Authorization"), updateInterestRequestDto);
        return ResponseEntity.ok().build();
    }

    @AuthRequired
    @GetMapping("/interests")
    public ResponseEntity<MemberInterestCategoriesResponse> updateInterests(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(memberInterestService.findInterestsByMemberId(request.getHeader("Authorization")));
    }
}
