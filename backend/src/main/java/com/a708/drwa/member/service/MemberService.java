package com.a708.drwa.member.service;

import com.a708.drwa.member.domain.Member;
import com.a708.drwa.member.dto.SocialLoginResponse;
import com.a708.drwa.member.dto.SocialUserInfoResponse;
import com.a708.drwa.member.repository.MemberRepository;
import com.a708.drwa.member.service.Impl.GoogleLoginServiceImpl;
import com.a708.drwa.member.service.Impl.KakaoLoginServiceImpl;
import com.a708.drwa.member.service.Impl.NaverLoginServiceImpl;
import com.a708.drwa.member.type.SocialType;
import com.a708.drwa.member.util.JWTUtil;
import com.a708.drwa.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final GoogleLoginServiceImpl googleLoginService;
    private final NaverLoginServiceImpl naverLoginService;
    private final KakaoLoginServiceImpl kakaoLoginService;
    private final JWTUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Value("${jwt.refreshtoken.expiretime}")
    private Long refreshTokenExpireTime;

    /**
     * 소셜 로그인 인증 URL을 반환한다.
     * @param socialType : google, naver, kakao
     * @return : 인증 URL
     */
    public String getAuthorizationUrl(String socialType) {
        // 소셜로그인 타입에 따른 소셜로그인 서비스 인스턴스 반환
        SocialLoginService socialLoginService = getSocialLoginService(socialType);

        // 지원하지 않는 소셜 로그인 타입일 경우
        if (socialLoginService == null) {
            throw new IllegalArgumentException("Unsupported social login type");
        }

        // 인증 URL 반환
        return socialLoginService.getAuthorizationUrl();
    }

    /**
     * 소셜 로그인 후 인증 코드로부터 액세스 토큰을 반환한다.
     * @param socialType : google, naver, kakao
     * @param code : 소셜 로그인 후 인증 코드
     * @return : 액세스 토큰
     */
    @Transactional
    public SocialLoginResponse processSocialLogin(String socialType, String code) {
        SocialLoginService socialLoginService = getSocialLoginService(socialType);

        // 지원하지 않는 소셜 로그인 타입
        if (socialLoginService == null) {
            throw new IllegalArgumentException("Unsupported social login type");
        }

        // 액세스 토큰 반환
        String accessToken = socialLoginService.getAccessToken(code);

        // 액세스 토큰으로부터 사용자 정보를 반환한다.
        SocialUserInfoResponse socialUserInfoResponse = socialLoginService.getUserInfo(accessToken);

        // 소셜로그인 타입 설정
        socialUserInfoResponse.setSocialType(SocialType.fromString(socialType));

        // 사용자 정보를 기반으로 기존 사용자 조회
        Member member = memberRepository.findByUserId(socialUserInfoResponse.getId())
                // 기존 사용자가 없으면 새로운 사용자 등록
                .orElseGet(() -> registerNewUser(socialUserInfoResponse));

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
     * @param socialUserInfoResponse 사용자 정보
     * @return 등록된 사용자 정보
     */
    private Member registerNewUser(SocialUserInfoResponse socialUserInfoResponse) {
        Member member = Member.builder()
                .userId(socialUserInfoResponse.getId())
                .socialType(socialUserInfoResponse.getSocialType())
                .build();

        return memberRepository.save(member);
    }

    /**
     * 소셜 로그인 서비스를 반환한다.
     * @param socialType : google, naver, kakao
     * @return : 소셜 로그인 서비스
     */
    public SocialLoginService getSocialLoginService(String socialType) {
        return switch (socialType) {
            case "google" -> googleLoginService;
            case "naver" -> naverLoginService;
            case "kakao" -> kakaoLoginService;
            default -> // 지원하지 않는 소셜 로그인 타입
                    null;
        };
    }

    /**
     * 회원탈퇴 처리
     * @param userId : 사용자 아이디
     */
    public void deleteMember(String userId) {
        // 사용자 정보 DB에서 삭제
    	memberRepository.deleteByUserId(userId);

        // Redis에서 리프레시 토큰 삭제
        redisUtil.deleteData(userId);
    }

    /**
     * 리프레시 토큰 삭제
     * @param userId
     */
    public void deleteRefreshToken(String userId) {
    	redisUtil.deleteData(userId);
    }
}
