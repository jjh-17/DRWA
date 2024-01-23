package com.a708.drwa.game.controller;

import com.a708.drwa.game.data.dto.request.GameInfoCreateRequestDto;
import com.a708.drwa.game.data.dto.request.RecordCreateRequestDto;
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

    // 게임 정보 저장
    @PostMapping("/gameinfo/create")
    public ResponseEntity<?> createGameInfo(@RequestBody GameInfoCreateRequestDto gameInfoRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(gameService.createGameInfo(gameInfoRequestDto));
    }

    @PostMapping("/record/create")
    public ResponseEntity<?> createRecord(@RequestBody RecordCreateRequestDto recordRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(gameService.createRecord(recordRequestDto));
    }

}
