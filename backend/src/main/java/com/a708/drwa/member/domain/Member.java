package com.a708.drwa.member.domain;

import com.a708.drwa.member.type.SocialType;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @Column
    private int reportedCnt;

    @OneToMany(mappedBy = "member", orphanRemoval = true)
    @JsonManagedReference
    private List<MemberInterest> memberInterestList = new ArrayList<>();

    @Builder
    public Member(String userId, SocialType socialType) {
        this.userId = userId;
        this.socialType = socialType;
    }

    public void addInterest(MemberInterest memberInterest) {
        memberInterestList.add(memberInterest);
    }

    public void removeInterest(MemberInterest memberInterest) {
        memberInterest.removeAssociations();
        memberInterestList.remove(memberInterest);
    }

    public void removeAllInterest() {
        memberInterestList.forEach(MemberInterest::removeAssociations);
        memberInterestList.clear();
    }

    public void report() {
        this.reportedCnt++;
    }
}
