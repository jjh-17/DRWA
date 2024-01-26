package com.a708.drwa.game.domain;

import com.a708.drwa.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Record {

    // PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int recordId;

    // FK - member
    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    // FK - game_info
    @ManyToOne(targetEntity = GameInfo.class, fetch = FetchType.LAZY)
    @JoinColumn(name="game_id")
    private GameInfo gameInfo;

    // 승패 여부
    @Column(nullable = false, updatable = false, columnDefinition = "TINYINT")
    private int result;

    // 소속
    @Column(nullable = false, updatable = false, columnDefinition = "TINYINT")
    private int team;

    @Builder
    public Record(Member member, GameInfo gameInfo, int result, int team) {
        this.member = member;
        this.gameInfo = gameInfo;
        this.result = result;
        this.team = team;
    }

}