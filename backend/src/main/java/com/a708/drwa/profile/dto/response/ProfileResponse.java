package com.a708.drwa.profile.dto.response;

import com.a708.drwa.rank.enums.RankName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProfileResponse {
    private Integer profileId;
    private Integer memberId;
    private String nickname;
    private int point;
    private int mvpCount;
    private RankName rankName;
    private int winCount;
    private int loseCount;
    private int tieCount;
    private int winRate;
}
