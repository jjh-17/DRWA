package com.a708.drwa.member.repository;

import com.a708.drwa.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findById(int id);

    Optional<Member> findByUserId(String userId);

    Optional<List<Member>> findAllByIdIn(List<Integer> ids);
}
