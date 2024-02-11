package com.a708.drwa.member.service;

import com.a708.drwa.debate.enums.DebateCategory;
import com.a708.drwa.member.domain.Member;
import com.a708.drwa.member.dto.response.SocialLoginResponse;
import com.a708.drwa.member.dto.response.SocialUserInfoResponse;
import com.a708.drwa.member.exception.MemberErrorCode;
import com.a708.drwa.member.exception.MemberException;
import com.a708.drwa.member.repository.MemberRepository;
import com.a708.drwa.member.service.Impl.GoogleLoginServiceImpl;
import com.a708.drwa.member.service.Impl.KakaoLoginServiceImpl;
import com.a708.drwa.member.service.Impl.NaverLoginServiceImpl;
import com.a708.drwa.member.type.SocialType;
import com.a708.drwa.member.util.JWTUtil;
import com.a708.drwa.profile.dto.request.AddProfileRequest;
import com.a708.drwa.profile.dto.response.ProfileResponse;
import com.a708.drwa.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    private final RedisTemplate<String, Object> redisTemplate;
    private final MemberInterestService memberInterestService;
    private final ProfileService profileService;

    @Value("${jwt.refreshtoken.expiretime}")
    private Long refreshTokenExpireTime;

    /**
     * 소셜 로그인 인증 URL을 반환한다.
     *
     * @param socialType : google, naver, kakao
     * @return : 인증 URL
     */
    public String getAuthorizationUrl(String socialType) {
        // 소셜로그인 타입에 따른 소셜로그인 서비스 인스턴스 반환
        SocialLoginService socialLoginService = getSocialLoginService(socialType);

        // 인증 URL 반환
        return socialLoginService.getAuthorizationUrl();
    }

    /**
     * 소셜 로그인 후 인증 코드로부터 액세스 토큰을 반환한다.
     *
     * @param socialType : google, naver, kakao
     * @param code : 소셜 로그인 후 인증 코드
     * @return : 액세스 토큰
     */
    @Transactional
    public SocialLoginResponse processSocialLogin(String socialType, String code) {
        SocialLoginService socialLoginService = getSocialLoginService(socialType);

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

        int memberId = member.getId();
        String userId = member.getUserId();


        // JWT 토큰 생성
        String jwtAccessToken = jwtUtil.createAccessToken(memberId, userId);
        String jwtRefreshToken = jwtUtil.createRefreshToken(memberId, userId);

        // Redis에 리프레시 토큰 저장
        redisTemplate.opsForValue().set(userId, jwtRefreshToken, refreshTokenExpireTime);

        // 사용자 ID로 관심사 조회
        List<DebateCategory> interests = memberInterestService.findInterestsByMemberId((long) memberId);

        ProfileResponse profile = profileService.findProfileByMemberId(memberId);

        // 응답 DTO 반환
        return new SocialLoginResponse(memberId, userId, jwtAccessToken, interests, profile);
    }

    /**
     * 새로운 사용자 등록
     * @param socialUserInfoResponse 사용자 정보
     * @return 등록된 사용자 정보
     */
    @Transactional
    protected Member registerNewUser(SocialUserInfoResponse socialUserInfoResponse) {
        Member member = Member.builder()
                .userId(socialUserInfoResponse.getId())
                .socialType(socialUserInfoResponse.getSocialType())
                .build();

        memberRepository.save(member);

        // 프로필 초기화 로직
        AddProfileRequest addProfileRequest = new AddProfileRequest(member.getId(), member.getUserId());
        profileService.addProfile(addProfileRequest); // 프로필 생성

        return member;
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
            // 지원하지 않는 소셜 로그인 타입
            default -> throw new MemberException(MemberErrorCode.UNSUPPORTED_SOCIAL_LOGIN_TYPE);
        };
    }

    /**
     * 회원탈퇴 처리
     * @param userId : 사용자 아이디
     */
    @Transactional
    public void deleteMember(String userId) {
        // 사용자 조회
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 사용자 정보 DB에서 삭제
    	memberRepository.deleteByUserId(userId);

        // Redis에서 리프레시 토큰 삭제
        redisTemplate.delete(userId);
    }

    /**
     * 리프레시 토큰 삭제
     * @param userId
     */
    public void deleteRefreshToken(String userId) {
        boolean exists = Boolean.TRUE.equals(redisTemplate.hasKey(userId));
        if (!exists) {
            throw new MemberException(MemberErrorCode.TOKEN_NOT_FOUND);
        }
        redisTemplate.delete(userId);
    }
}
