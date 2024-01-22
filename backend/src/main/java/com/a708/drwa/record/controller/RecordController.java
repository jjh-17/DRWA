package com.a708.drwa.record.controller;

import com.a708.drwa.record.domain.Record;
import com.a708.drwa.record.data.dto.request.RecordSaveRequestDto;
import com.a708.drwa.record.service.RecordService;
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
        log.debug("게임 정보 저장 : " + recordRequestDto);
        Record result = recordService.createRecord(recordRequestDto);
        if(result==null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

}
