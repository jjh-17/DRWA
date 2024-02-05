package com.a708.drwa.game.controller;

import com.a708.drwa.game.data.dto.request.AddGameRequestDto;
import com.a708.drwa.game.data.dto.response.AddGameResponseDto;
import com.a708.drwa.game.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    @PostMapping("/end")
    public ResponseEntity<AddGameResponseDto> createPublicGame(@RequestBody AddGameRequestDto addGameRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(gameService.addGame(addGameRequestDto));
    }
}
