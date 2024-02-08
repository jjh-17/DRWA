package com.a708.drwa.member.controller;

import com.a708.drwa.member.dto.AuthDto;
import com.a708.drwa.member.service.AuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 리프레시 토큰을 받아서 유효성 검증 후, 새로운 액세스 토큰을 발급한다.
     * @param userId 사용자 아이디
     * @return 새로 발급한 액세스 토큰
     */
    @PostMapping("/silent-refresh")
    public ResponseEntity<?> silentRefresh(@RequestBody String userId) {
            // silentRefreshProcess() 메소드를 통해 새로운 액세스 토큰을 발급받는다.
            String newAccessToken  = authService.silentRefreshProcess(userId);
            // 새로 발급한 액세스 토큰을 AuthDto 객체에 담아서 반환한다.
            AuthDto authDto = new AuthDto(userId, newAccessToken);
            return ResponseEntity.ok(authDto);
    }


}

