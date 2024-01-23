package com.a708.drwa.member.repository;

import com.a708.drwa.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByMemberId(int memberId);

    Optional<Member> findByUserId(String userId);
}
