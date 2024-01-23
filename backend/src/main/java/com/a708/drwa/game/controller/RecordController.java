package com.a708.drwa.game.controller;

import com.a708.drwa.game.data.dto.request.RecordSaveRequestDto;
import com.a708.drwa.game.service.RecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/record")
@Slf4j
@CrossOrigin("*")
public class RecordController {

    private final RecordService recordService;

    @PostMapping("/create")
    public ResponseEntity<?> createRecord(@RequestBody @Valid RecordSaveRequestDto recordRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(recordService.createRecord(recordRequestDto));
    }

}
