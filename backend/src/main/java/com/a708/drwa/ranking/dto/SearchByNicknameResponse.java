package com.a708.drwa.ranking.dto;

import com.a708.drwa.ranking.domain.RankingMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class SearchByNicknameResponse {
    private RankingMember me;
    private List<RankingMember> topAndBottom;
}
