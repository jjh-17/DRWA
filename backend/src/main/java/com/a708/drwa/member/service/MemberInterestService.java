package com.a708.drwa.member.service;

import com.a708.drwa.debate.domain.Debate;
import com.a708.drwa.debate.enums.DebateCategory;
import com.a708.drwa.member.data.JWTMemberInfo;
import com.a708.drwa.member.data.dto.request.UpdateInterestRequestDto;
import com.a708.drwa.member.domain.Member;
import com.a708.drwa.member.domain.MemberInterest;
import com.a708.drwa.member.exception.MemberErrorCode;
import com.a708.drwa.member.exception.MemberException;
import com.a708.drwa.member.repository.MemberInterestRepository;
import com.a708.drwa.member.repository.MemberRepository;
import com.a708.drwa.utils.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
/**
 * 회원 토론 카테고리 관심사 서비스
 */
public class MemberInterestService {

    private final MemberInterestRepository memberInterestRepository;
    private final MemberRepository memberRepository;
    private final JWTUtil jwtUtil;


    /**
     * 회원 토론 카테고리 관심사 업데이트
     *
     * @param memberId 회원 ID
     * @param categories 관심 카테고리 목록 (최대 3개까지 선택 가능)
     */
    @Transactional
    public void updateMemberInterests(String token, UpdateInterestRequestDto updateInterestRequestDto) {
        // 최대 3개까지 선택 가능
        List<DebateCategory> categories = updateInterestRequestDto.getDebateCategories();
        if(categories.size() > 3)
            throw new MemberException(MemberErrorCode.TOO_MANY_CATEGORIES);

        // 멤버 찾기
        JWTMemberInfo memberInfo = jwtUtil.getMember(token);
        Member member = memberRepository.findById(memberInfo.getMemberId())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 멤버 관심사 삭제 후 일괄 변경
        member.removeAllInterests();
        for(DebateCategory debateCategory : updateInterestRequestDto.getDebateCategories()) {
            MemberInterest.builder()
                    .member(member)
                    .debateCategory(debateCategory)
                    .build();
        }
    }

    /**
     * 멤버 ID로 관심카테고리 조회
     *
     * @param token accessToken
     * @return
     */
    public List<DebateCategory> findInterestsByMemberId(String token) {
        List<MemberInterest> interests = memberRepository
                .findById(jwtUtil.getMember(token).getMemberId())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND))
                .getMemberInterestList();

        return interests.stream()
                .map(MemberInterest::getDebateCategory)
                .collect(Collectors.toList());
    }
}
