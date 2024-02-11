package com.a708.drwa.member.repository;

import com.a708.drwa.member.domain.MemberInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberInterestRepository extends JpaRepository<MemberInterest, Long> {
    /**
     * 회원의 관심사 조회
     * @param memberId
     * @return 회원의 관심사 목록 리스트
     */
    List<MemberInterest> findByMemberId(Long memberId);
}
