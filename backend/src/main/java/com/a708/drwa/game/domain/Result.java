package com.a708.drwa.game.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 승패 : 승, 패, 무승부, 탈주
@Getter
@RequiredArgsConstructor
public enum Result {
    WIN(0), LOSE(1), TIE(2), ESCAPE(3);

    private final int result;
}
