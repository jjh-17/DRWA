package com.a708.drwa.achievement.repository;

import com.a708.drwa.achievement.domain.MemberAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberAchievementRepository extends JpaRepository<MemberAchievement, Integer> {
    List<MemberAchievement> findByMemberId(Integer memberId);
}
