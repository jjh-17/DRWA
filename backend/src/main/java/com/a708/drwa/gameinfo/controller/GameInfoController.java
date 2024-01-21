package com.a708.drwa.gameinfo.controller;

import com.a708.drwa.gameinfo.domain.GameInfo;
import com.a708.drwa.gameinfo.domain.data.dto.request.GameInfoRequestDto;
import com.a708.drwa.gameinfo.service.GameInfoService;
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
    public ResponseEntity<?> createGameInfo(@RequestBody @Valid GameInfoRequestDto gameInfoRequestDto) {
        log.debug("게임 정보 저장 : " + gameInfoRequestDto);
        GameInfo result = gameInfoService.createGameInfo(gameInfoRequestDto);
        if(result==null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

}
