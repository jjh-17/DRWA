package com.a708.drwa.game.data.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 승리한 팀
@Getter
@RequiredArgsConstructor
public enum WinnerTeam {
    A(0), B(1), TIE(2);

    private final int winnerTeam;
}
