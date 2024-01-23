package com.a708.drwa.debate.controller;

import com.a708.drwa.debate.data.dto.request.DebateCreateRequestDto;
import com.a708.drwa.debate.data.dto.request.DebateJoinRequestDto;
import com.a708.drwa.debate.service.DebateService;
import com.a708.drwa.debate.service.OpenViduService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/debate")
public class DebateController {
    private final DebateService debateService;
    private final OpenViduService openViduService;

    /**
     * 방 생성 API
     * debateCreateDto -> debateRepository -> roomId
     * getToken by roomId -> openvidu.createSession()
     * @param debateCreateRequestDto
     * @return
     */
    @PostMapping("/create")
    public ResponseEntity<Void> create(@RequestBody DebateCreateRequestDto debateCreateRequestDto) {
        openViduService.create(debateService.create(debateCreateRequestDto));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    /**
     * 방 입장 API
     * debateJoinRequestDto -> get session from OpenVidu Session
     * -> get token by session -> return session
     * @param debateJoinRequestDto
     * @return
     */
    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody DebateJoinRequestDto debateJoinRequestDto) {
        String token = "";
        // TODO: 사용자 정보 Redis에 추가
        if(debateService.isExistDebate(debateJoinRequestDto))
            token = openViduService.join(debateJoinRequestDto.getDebateId());

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(token);
    }
}
