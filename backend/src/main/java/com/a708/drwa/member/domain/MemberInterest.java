package com.a708.drwa.member.domain;

import com.a708.drwa.debate.enums.DebateCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "member_interest")
public class MemberInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberInterestId;

    // 지연로딩 : 관심사를 조회할 때 회원 정보는 조회하지 않는다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @Setter
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "debate_category")
    @Setter
    private DebateCategory debateCategory;

}
