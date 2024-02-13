package com.a708.drwa.member.domain;

import com.a708.drwa.member.type.SocialType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    @Column
    @Size(max = 10)
    private String authority;

    @OneToMany(mappedBy = "member", orphanRemoval = true)
    private List<MemberInterest> memberInterestList;

    @Builder
    public Member(String userId, SocialType socialType) {
        this.userId = userId;
        this.socialType = socialType;
        this.memberInterestList = new ArrayList<>();
    }

    public void removeInterest(MemberInterest memberInterest) {
        this.memberInterestList.remove(memberInterest);
    }

    public void removeAllInterests() {
        for(MemberInterest memberInterest : this.memberInterestList) {
            this.removeInterest(memberInterest);
        }
    }
}
