package com.a708.drwa.member.service;

import com.a708.drwa.member.domain.Member;
import com.a708.drwa.member.dto.SocialUserInfoResponse;
import com.a708.drwa.member.repository.MemberRepository;
import com.a708.drwa.member.service.Impl.GoogleLoginServiceImpl;
import com.a708.drwa.member.service.Impl.KakaoLoginServiceImpl;
import com.a708.drwa.member.service.Impl.NaverLoginServiceImpl;
import com.a708.drwa.member.type.SocialType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final GoogleLoginServiceImpl googleLoginService;
    private final NaverLoginServiceImpl naverLoginService;
    private final KakaoLoginServiceImpl kakaoLoginService;

    /**
     * 소셜 로그인 처리
     * @param userInfo 사용자 정보
     * @return 처리된 사용자 정보
     */
    @Transactional
    public Member handleSocialLogin(SocialUserInfoResponse userInfo) {
        // 사용자 정보를 기반으로 기존 사용자 조회
        Member member = memberRepository.findByUserId(userInfo.getId())
                // 기존 사용자가 없으면 새로운 사용자 등록
                .orElseGet(() -> registerNewUser(userInfo));

        return member;
    }

    /**
     * 새로운 사용자 등록
     * @param userInfo 사용자 정보
     * @return 등록된 사용자 정보
     */
    private Member registerNewUser(SocialUserInfoResponse userInfo) {
        Member newMember = Member.createMember(userInfo.getId(), userInfo.getSocialType());

        return memberRepository.save(newMember);
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
