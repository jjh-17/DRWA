package com.a708.drwa.rank.dto.response;

import com.a708.drwa.rank.enums.RankName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RankResponse {
    private Integer rankId;
    private RankName rankName;
    private int pivotPoint;
}
