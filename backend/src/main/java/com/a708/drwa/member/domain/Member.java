package com.a708.drwa.member.domain;

import com.a708.drwa.member.type.SocialType;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
@Builder
public class Member {

    // Primary Key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int memberId;

    // 소셜 로그인 사용자 ID
    @Column(unique = true)
    private String userId;

    @Column
    private SocialType socialType;

    public static Member createMember(String userId, SocialType socialType) {
        return Member.builder()
                .userId(userId)
                .socialType(socialType)
                .build();
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setSocialType(SocialType socialType) {
        this.socialType = socialType;
    }
}
