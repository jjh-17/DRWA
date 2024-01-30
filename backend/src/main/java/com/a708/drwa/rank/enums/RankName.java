package com.a708.drwa.rank.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public enum RankName {
    BRONZE("BRONZE"), SILVER("SILVER"), GOLD("GOLD"),
    PLATINUM("PLATINUM"), DIAMOND("DIAMOND"), MASTER("MASTER"),
    CHALLENGER("CHALLENGER")
    ;

    private final String name;
}
