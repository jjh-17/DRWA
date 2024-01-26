package com.a708.drwa.ranking.domain;

import com.a708.drwa.rank.enums.RankName;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class RankingMember implements Serializable {
    private Integer memberId;
    private String nickname;
    private RankName rankName;
    private int point;
    private String title;
    private int winRate;

    @Builder
    private RankingMember(Integer memberId, String nickname, RankName rankName, int point, String title, int winRate) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.rankName = rankName;
        this.point = point;
        this.title = title;
        this.winRate = winRate;
    }
}
