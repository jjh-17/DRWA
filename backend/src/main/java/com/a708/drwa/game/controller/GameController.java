package com.a708.drwa.game.controller;

import com.a708.drwa.game.data.dto.request.AddGameRequestDto;
import com.a708.drwa.game.data.dto.response.AddPublicGameResponseDto;
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

//    // 사설 토론방 종료 후 전적/게임정보 저장, Redis 수정
//    @PostMapping("/create/private")
//    public ResponseEntity<AddPrivateGameResponseDto> createPrivateGame(@RequestBody AddGameRequestDto addGameRequestDto) {
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(gameService.addPrivateGame(addGameRequestDto));
//    }

    // 사설 토론방 종료 후 전적/게임정보 저장, Redis 수정
    @PostMapping("/create/public")
    public ResponseEntity<AddPublicGameResponseDto> createPublicGame(@RequestBody AddGameRequestDto addGameRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(gameService.addPublicGame(addGameRequestDto));
    }
}
