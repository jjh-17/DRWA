package com.a708.drwa.ranking.domain;

import com.a708.drwa.rank.enums.RankName;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RankingMember implements Serializable {
    private Integer memberId;
    private String nickname;
    private RankName rankName;
    private int point;
    private String achievement;
    private int winRate;

    @Builder
    private RankingMember(Integer memberId, String nickname, RankName rankName, int point, String achievement, int winRate) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.rankName = rankName;
        this.point = point;
        this.achievement = achievement;
        this.winRate = winRate;
    }

    public void updatePoint(int point){
        this.point = point;
    }
    public void increasePoint(int point){
        this.point += point;
    }
}
