package com.a708.drwa.debate.controller;

import com.a708.drwa.debate.data.dto.request.DebateCreateRequestDto;
import com.a708.drwa.debate.data.dto.request.DebateJoinRequestDto;
import com.a708.drwa.debate.service.DebateService;
import com.a708.drwa.debate.service.OpenViduService;
import com.a708.drwa.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/session")
public class SessionController {
    private final DebateService debateService;
    private final OpenViduService openViduService;
    private final RedisUtil redisUtil;

    @PostMapping("/create")
    public ResponseEntity<Integer> create(DebateCreateRequestDto debateCreateRequestDto) {
        int debateId = debateService.create(debateCreateRequestDto);
        openViduService.create(debateId);



        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(debateId);
    }

    @PostMapping("/join")
    public ResponseEntity<?> join(DebateJoinRequestDto debateJoinRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
