package com.a708.drwa.profile.repository;

import com.a708.drwa.profile.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Integer> {
    Optional<Profile> findByMemberId(Integer memberId);

    boolean existsByNickname(String nickname);

    Optional<List<Profile>> findAllByMemberIdIn(List<Integer> memberIds);
}
