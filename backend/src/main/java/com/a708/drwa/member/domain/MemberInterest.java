package com.a708.drwa.member.domain;

import com.a708.drwa.debate.enums.DebateCategory;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "debate_category")
    private DebateCategory debateCategory;

    @Builder
    public MemberInterest(Member member, DebateCategory debateCategory) {
        this.member = member;
        this.debateCategory = debateCategory;
        member.getMemberInterestList().add(this);
    }

    public void removeAssociations() {
        this.member.removeInterest(this);
        this.member = null;
    }
}
