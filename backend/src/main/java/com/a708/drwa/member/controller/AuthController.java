package com.a708.drwa.member.controller;

import com.a708.drwa.member.dto.AuthDto;
import com.a708.drwa.member.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @Value("${jwt.accesstoken.expiretime}")
    private Long accessTokenExpireTime;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 리프레시 토큰을 받아서 유효성 검증 후, 새로운 액세스 토큰을 발급한다.
     * @param userId 사용자 아이디
     * @return 새로 발급한 액세스 토큰
     */
    @PostMapping("/silent-refresh")
    public ResponseEntity<?> silentRefresh(@RequestBody String userId, HttpServletResponse response) {
        // silentRefreshProcess() 메소드를 통해 새로운 액세스 토큰을 발급받는다.
        String newAccessToken  = authService.silentRefreshProcess(userId);

        // accessToken을 httpOnly 쿠키에 담아서 반환
        Cookie cookie = new Cookie("accessToken", newAccessToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setMaxAge(accessTokenExpireTime.intValue());
        response.addCookie(cookie);

        // userId만 반환
        AuthDto authDto = new AuthDto(userId, null);
        return ResponseEntity.ok(authDto);
    }


}

