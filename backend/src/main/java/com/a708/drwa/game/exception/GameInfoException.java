package com.a708.drwa.game.exception;

import com.a708.drwa.global.exception.GlobalException;
import lombok.Getter;

@Getter
public class GameInfoException extends GlobalException {

    private final GameInfoErrorCode gameInfoErrorCode;

    public GameInfoException(GameInfoErrorCode gameInfoErrorCode) {
        super(gameInfoErrorCode);
        this.gameInfoErrorCode = gameInfoErrorCode;
    }
}
