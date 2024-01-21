package com.a708.drwa.record.domain;

import com.a708.drwa.gameinfo.domain.GameInfo;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int recordId;

    @ManyToOne(targetEntity = Member.class)
    @JoinColumn(name="member_id", nullable = false)
    private int memberId;

    @ManyToOne(targetEntity = GameInfo.class)
    @JoinColumn(name="game_id", nullable = false)
    private int gameId;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false, updatable = false, columnDefinition = "TINYINT")
    private Result result;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false, updatable = false, columnDefinition = "TINYINT")
    private Team team;

    @Builder
    public Record(
            int memberId,
            int gameId,
            Result result,
            Team team
    ) {
        this.memberId = memberId;
        this.gameId = gameId;
        this.result = result;
        this.team = team;
    }

}