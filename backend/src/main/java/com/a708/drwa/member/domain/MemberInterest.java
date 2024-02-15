package com.a708.drwa.member.domain;

import com.a708.drwa.debate.enums.DebateCategory;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class MemberInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer memberInterestId;

    // 지연로딩 : 관심사를 조회할 때 회원 정보는 조회하지 않는다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonBackReference
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "debate_category")
    private DebateCategory debateCategory;

    @Builder
    public MemberInterest(Member member, DebateCategory debateCategory) {
        this.debateCategory = debateCategory;
        setMember(member);
    }

    public void setMember(Member member) {
        this.member = member;
        member.addInterest(this);
    }

    public void removeAssociations() {
        this.member = null;
    }
}
