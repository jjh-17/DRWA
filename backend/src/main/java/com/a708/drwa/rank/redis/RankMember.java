package com.a708.drwa.rank.redis;

import com.a708.drwa.rank.enums.RankName;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class RankMember implements Serializable {
    private Integer memberId;
    private String nickname;
    private RankName rankName;
    private int point;
    private String title;
    private int winRate;
}
