package com.a708.drwa.member.service;

import com.a708.drwa.member.domain.Member;
import com.a708.drwa.member.dto.SocialLoginResponse;
import com.a708.drwa.member.dto.GoogleUserInfoResponse;
import com.a708.drwa.member.dto.SocialUserInfoResponse;
import com.a708.drwa.member.repository.MemberRepository;
import com.a708.drwa.member.service.Impl.GoogleLoginServiceImpl;
import com.a708.drwa.member.service.Impl.KakaoLoginServiceImpl;
import com.a708.drwa.member.service.Impl.NaverLoginServiceImpl;
import com.a708.drwa.member.util.JWTUtil;
import com.a708.drwa.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl {
    private final MemberRepository memberRepository;
    private final GoogleLoginServiceImpl googleLoginService;
    private final NaverLoginServiceImpl naverLoginService;
    private final KakaoLoginServiceImpl kakaoLoginService;
    private final JWTUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Value("${jwt.refreshtoken.expiretime}")
    private Long refreshTokenExpireTime;

    /**
     * 소셜 로그인 처리
     * @param userInfo 사용자 정보
     * @return 처리된 사용자 정보
     */
    @Transactional
    public SocialLoginResponse handleSocialLogin(SocialUserInfoResponse userInfo) throws UnsupportedEncodingException {
        // 사용자 정보를 기반으로 기존 사용자 조회
        Member member = memberRepository.findByUserId(userInfo.getId())
                // 기존 사용자가 없으면 새로운 사용자 등록
                .orElseGet(() -> registerNewUser(userInfo));

        // JWT 토큰 생성
        String jwtAccessToken = jwtUtil.createAccessToken(member.getUserId());
        String jwtRefreshToken = jwtUtil.createRefreshToken(member.getUserId());

        // Redis에 리프레시 토큰 저장
        redisUtil.setData(member.getUserId(), jwtRefreshToken, refreshTokenExpireTime);

        // 응답 DTO 반환
        return new SocialLoginResponse(member.getUserId(), jwtAccessToken);
    }

    /**
     * 새로운 사용자 등록
     * @param userInfo 사용자 정보
     * @return 등록된 사용자 정보
     */
    private Member registerNewUser(SocialUserInfoResponse userInfo) {
        Member member = Member.builder()
                .userId(userInfo.getId())
                .socialType(userInfo.getSocialType())
                .build();

        return memberRepository.save(member);
    }

    /**
     * 소셜 로그인 서비스를 반환한다.
     * @param socialType : google, naver, kakao
     * @return : 소셜 로그인 서비스
     */
    public SocialLoginService getSocialLoginService(String socialType) {
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
