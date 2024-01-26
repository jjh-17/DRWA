package com.a708.drwa.member.controller;

import com.a708.drwa.member.service.AuthService;
import com.a708.drwa.member.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    public ResponseEntity<Map<String, Object>> slientRefresh(HttpServletRequest request, HttpServletResponse response) {


        Map<String, Object> result = new HashMap<String, Object>();

        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.CREATED);
    }


}

