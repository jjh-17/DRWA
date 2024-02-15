package com.a708.drwa.openvidu.controller;

import com.a708.drwa.openvidu.data.dto.request.CreateRoomRequestDto;
import com.a708.drwa.openvidu.data.dto.request.JoinRoomRequestDto;
import com.a708.drwa.openvidu.data.dto.response.CreateRoomResponseDto;
import com.a708.drwa.openvidu.data.dto.response.GetConnectionResponseDto;
import com.a708.drwa.openvidu.service.OpenViduService;
import com.a708.drwa.utils.JWTUtil;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/openvidu")
public class SessionController {
    private final OpenViduService openViduService;
    private final JWTUtil jwtUtil;

    @PostMapping("/session/create")
    public ResponseEntity<CreateRoomResponseDto> createSession(@RequestBody CreateRoomRequestDto roomDto, HttpServletRequest request) throws OpenViduJavaClientException, OpenViduHttpException {
        log.info("------------------------------ OpenVidu Create Session ------------------------------");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(openViduService
                        .createSession(roomDto, request.getHeader("Authorization")));
    }

    @PostMapping("/session/enter")
    public ResponseEntity<GetConnectionResponseDto> createConnection(@RequestBody JoinRoomRequestDto joinRoomRequestDto, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(openViduService.getConnection(joinRoomRequestDto, request.getHeader("Authorization")));
    }
}
