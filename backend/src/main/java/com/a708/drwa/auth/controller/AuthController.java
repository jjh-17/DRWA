package com.a708.drwa.auth.controller;

import com.a708.drwa.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * 리프레시 토큰을 받아서 유효성 검증 후, 새로운 액세스 토큰을 발급한다.
     * @param request header
     * @return
     */
    @PostMapping("/silent-refresh")
    public ResponseEntity<?> silentRefresh(HttpServletRequest request) {
            // silentRefreshProcess() 메소드를 통해 새로운 액세스 토큰을 발급받는다.
            return ResponseEntity.status(HttpStatus.OK)
                    .body(authService.silentRefreshProcess(request.getHeader("Authorization")));
    }
}

