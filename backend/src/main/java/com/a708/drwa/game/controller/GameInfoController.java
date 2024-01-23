package com.a708.drwa.game.controller;

import com.a708.drwa.game.data.dto.request.GameInfoSaveRequestDto;
import com.a708.drwa.game.service.GameInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gameInfo")
@Slf4j
@CrossOrigin("*")
public class GameInfoController {

    private final GameInfoService gameInfoService;

    // 게임 정보 저장
    @PostMapping("/create")
    public ResponseEntity<?> createGameInfo(@RequestBody @Valid GameInfoSaveRequestDto gameInfoRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(gameInfoService.createGameInfo(gameInfoRequestDto));
    }

}
