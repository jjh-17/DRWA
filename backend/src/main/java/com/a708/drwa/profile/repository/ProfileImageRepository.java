package com.a708.drwa.profile.repository;

import com.a708.drwa.profile.domain.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Integer> {
    ProfileImage findByMemberId(Integer memberId);
}
