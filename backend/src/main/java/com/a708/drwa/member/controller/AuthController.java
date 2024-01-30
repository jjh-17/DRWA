package com.a708.drwa.member.controller;

import com.a708.drwa.member.dto.AuthDto;
import com.a708.drwa.member.service.AuthService;
import com.a708.drwa.member.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Value("${jwt.accesstoken.expiretime}")
    private int accessTokenExpireTime;

    @Value("${jwt.refreshtoken.expiretime}")
    private int refreshTokenExpireTime;

    private final JWTUtil jwtUtil;
    private final AuthService authService;

    public AuthController(JWTUtil jwtUtil, AuthService authService) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    /**
     * Refresh Token을 이용하여 Access Token을 재발급한다.
     * @param userId : 사용자 아이디
     * @return 재발급된 액세스 토큰
     * @throws UnsupportedEncodingException : 인코딩 예외
     */
    @PostMapping("/silent-refresh")
    public ResponseEntity<Map<String, Object>> silentRefresh(@RequestBody String userId) throws UnsupportedEncodingException {
        // AuthService를 통해 Redis에서 리프레시 토큰 조회
        AuthDto authDto = authService.getRefreshToken(userId);

        // 리프레시 토큰이 없으면 401 에러
        if (authDto == null || authDto.getRefreshToken() == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Map<String, Object> result = new HashMap<String, Object>();

        // 리프레시 토큰이 만료되었으면 403 에러
        if(!jwtUtil.vaildCheck(authDto.getRefreshToken())) {
            result.put("message", "로그인이 필요합니다");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.FORBIDDEN);
        }

        // 유효한 경우에는 refreshToken과 accessToken을 재발급.
        String newAccessToken = jwtUtil.createAccessToken(userId);
        String newRefreshToken = jwtUtil.createRefreshToken(userId);

        // AuthService를 통해 Redis에 저장된 리프레시 토큰 갱신
        authService.updateRefreshToken(new AuthDto(userId, newRefreshToken), refreshTokenExpireTime);

        // 새로 발급된 엑세스 토큰 반환
        result.put("accessToken", newAccessToken);

        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.CREATED);
    }


}

