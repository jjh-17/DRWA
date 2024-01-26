package com.a708.drwa.game.domain;

import lombok.RequiredArgsConstructor;

// 참여팀 : 배심원, 팀A, 팀B
@RequiredArgsConstructor
public enum Team {
    JUROR(0), A(1), B(2);

    private final int team;
}
