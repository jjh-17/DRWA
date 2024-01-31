package com.a708.drwa.debate.controller;

import com.a708.drwa.debate.data.dto.request.DebateCreateRequestDto;
import com.a708.drwa.debate.data.dto.request.DebateJoinRequestDto;
import com.a708.drwa.debate.data.dto.request.DebateStartRequestDto;
import com.a708.drwa.debate.service.DebateService;
import com.a708.drwa.debate.service.OpenViduService;
import com.a708.drwa.redis.util.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisKeyUtil redisKeyUtil;

    /**
     * 방 생성 API
     * debateCreateDto -> debateRepository -> roomId
     * getToken by roomId -> openvidu.createSession()
     * @param debateCreateRequestDto
     * @return
     */
    @PostMapping("/create")
    public ResponseEntity<Void> create(@RequestBody DebateCreateRequestDto debateCreateRequestDto) {
        int debateId = debateService.create(debateCreateRequestDto);
        openViduService.create(debateId);

        DebateJoinRequestDto debateJoinRequestDto = new DebateJoinRequestDto();
        debateJoinRequestDto.setDebateId(debateId);

        String token = openViduService.join(debateId);


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
    public ResponseEntity<?> join(@RequestBody DebateJoinRequestDto debateJoinRequestDto) {
        try {
            // 참여
            String token = openViduService.join(debateJoinRequestDto.getDebateId());

            // 토큰 반환
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(token);
        } catch (Exception e) {
            // 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error joining the debate");
        }
    }

    @PostMapping("/start")
    public ResponseEntity<Void> start(@RequestBody DebateStartRequestDto debateStartRequestDto) {
        debateService.start(debateStartRequestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
