package com.a708.drwa.achievement.repository;

import com.a708.drwa.achievement.domain.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AchievementRepository extends JpaRepository<Achievement, Integer> {
    Optional<Achievement> findByName(String name);
}
