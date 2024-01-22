package com.a708.drwa.rank.domain;

import com.a708.drwa.rank.enums.RankName;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "game_rank")
public class Rank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rank_id")
    private Integer id;

    @Enumerated(value = EnumType.STRING)
    private RankName rankName;
    private int pivotPoint;

    @Builder
    private Rank(RankName rankName, int pivotPoint) {
        this.rankName = rankName;
        this.pivotPoint = pivotPoint;
    }
}
