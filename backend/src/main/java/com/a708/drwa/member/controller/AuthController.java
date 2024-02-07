package com.a708.drwa.member.controller;

import com.a708.drwa.member.dto.AuthDto;
import com.a708.drwa.member.service.AuthService;
import com.a708.drwa.member.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 리프레시 토큰을 받아서 유효성 검증 후, 새로운 액세스 토큰을 발급한다.
     *
     * @return 새로 발급한 액세스 토큰
     */
    @PostMapping("/silent-refresh")
    public ResponseEntity<?> silentRefresh(HttpServletRequest request) throws ParseException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Invalid Authorization header.");
        }

        String token = authorizationHeader.substring(7); // "Bearer " 다음 부분을 추출합니다.

        // silentRefreshProcess() 메소드를 통해 새로운 액세스 토큰을 발급받는다.
        String newAccessToken = authService.silentRefreshProcess(token);
        // 새로 발급한 액세스 토큰을 AuthDto 객체에 담아서 반환한다.
        return ResponseEntity.ok(newAccessToken);
    }


}

