package com.a708.drwa.debate.controller;

import com.a708.drwa.debate.data.dto.request.DebateStartRequestDto;
import com.a708.drwa.debate.service.DebateService;
import com.a708.drwa.openvidu.service.OpenViduService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/debate")
public class DebateController {
    private final DebateService debateService;
    private final OpenViduService openViduService;

    @GetMapping("/{categoryName}")
    public ResponseEntity<?> getDebatesByCategory(@PathVariable String categoryName) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/popular")
    public ResponseEntity<?> getDebatesByTotalCnt() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(debateService.getTop5Debates());
    }

    @GetMapping
    public ResponseEntity<?> getDebateByMemberInterests(HttpServletRequest request) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/start")
    public ResponseEntity<Void> start(@RequestBody DebateStartRequestDto debateStartRequestDto) {
        debateService.start(debateStartRequestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
