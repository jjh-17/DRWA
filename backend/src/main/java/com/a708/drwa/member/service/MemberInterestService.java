package com.a708.drwa.member.service;

import com.a708.drwa.debate.domain.Debate;
import com.a708.drwa.debate.enums.DebateCategory;
import com.a708.drwa.member.domain.Member;
import com.a708.drwa.member.domain.MemberInterest;
import com.a708.drwa.member.repository.MemberInterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
/**
 * 회원 토론 카테고리 관심사 서비스
 */
public class MemberInterestService {

    private final MemberInterestRepository memberInterestRepository;



    /**
     * 회원 토론 카테고리 관심사 업데이트
     *
     * @param memberId 회원 ID
     * @param categories 관심 카테고리 목록 (최대 3개까지 선택 가능)
     */
    @Transactional
    public void updateMemberInterests(int memberId, List<DebateCategory> categories) {
       // 최대 3개까지 선택 가능
        if (categories.size() > 3) {
            throw new IllegalArgumentException("최대 3개의 관심 카테고리만 선택할 수 있습니다.");
        }

        // 기존 관심 카테고리 삭제
        List<MemberInterest> existingInterests = memberInterestRepository.findByMemberId((long) memberId);
        memberInterestRepository.deleteAll(existingInterests);

        // 새로운 관심 카테고리 저장
        for (DebateCategory category : categories) {
            MemberInterest interest = new MemberInterest();
            interest.setMember(new Member(memberId)); // Member 엔티티 참조를 설정하는 가정
            interest.setDebateCategory(category);
            memberInterestRepository.save(interest);
        }
    }

    /**
     * 멤버 ID로 관심카테고리 조회
     *
     * @param memberId
     * @return
     */
    public List<DebateCategory> findInterestsByMemberId(Long memberId) {
        List<MemberInterest> interests = memberInterestRepository.findByMemberId(memberId);
        return interests.stream()
                .map(MemberInterest::getDebateCategory)
                .collect(Collectors.toList());
    }
}
