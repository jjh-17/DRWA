package com.a708.drwa.game.exception;

import com.a708.drwa.global.exception.GlobalException;
import lombok.Getter;

@Getter
public class GameException extends GlobalException {

    private final GameErrorCode gameErrorCode;

    public GameException(GameErrorCode gameErrorCode) {
        super(gameErrorCode);
        this.gameErrorCode = gameErrorCode;
    }

}
