package com.a708.drwa.game.controller;

import com.a708.drwa.game.data.dto.request.AddRecordRequestDto;
import com.a708.drwa.game.data.dto.response.AddRecordResponseDto;
import com.a708.drwa.game.service.RecordService;
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

    private final RecordService gameService;

    // 게임 정보 저장
    @PostMapping("/create")
    public ResponseEntity<AddRecordResponseDto> createGameInfo(@RequestBody AddRecordRequestDto addRecordRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(gameService.addRecord(addRecordRequestDto));
    }
}
