package com.a708.drwa.member.domain;

import com.a708.drwa.member.type.SocialType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    // Primary Key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    public Member(int memberId) {
        this.id = memberId;
    }

    // 소셜 로그인 사용자 ID
    @Column(unique = true)
    private String userId;

    @Column
    @Enumerated(EnumType.ORDINAL)
    private SocialType socialType;

    @Builder
    public Member(String userId, SocialType socialType) {
        this.userId = userId;
        this.socialType = socialType;
    }
}
