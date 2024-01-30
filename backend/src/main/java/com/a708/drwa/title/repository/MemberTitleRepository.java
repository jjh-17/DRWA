package com.a708.drwa.title.repository;

import com.a708.drwa.title.domain.MemberTitle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberTitleRepository extends JpaRepository<MemberTitle, Integer> {
    List<MemberTitle> findByMemberId(Integer memberId);
}
